package com.itts.modules.checkin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itts.common.util.TimeFormatUtils;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.achievement.event.LearningActivityEvent;
import com.itts.modules.checkin.dto.StudyCheckinRequest;
import com.itts.modules.checkin.dto.StudyCheckinResponse;
import com.itts.modules.checkin.entity.StudyCheckin;
import com.itts.modules.checkin.mapper.StudyCheckinMapper;
import com.itts.modules.checkin.service.StudyCheckinService;
import com.itts.modules.learning.service.UserLearningStatsService;
import com.itts.modules.student.entity.UserLearningStreak;
import com.itts.modules.student.mapper.UserLearningStreakMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 学习打卡服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudyCheckinServiceImpl extends ServiceImpl<StudyCheckinMapper, StudyCheckin>
        implements StudyCheckinService {

    private static final ZoneId CHINA_ZONE = ZoneId.of("Asia/Shanghai");

    private final CourseMapper courseMapper;
    private final ObjectMapper objectMapper;
    private final UserLearningStatsService userLearningStatsService;
    private final UserLearningStreakMapper userLearningStreakMapper;
    private final com.itts.modules.student.service.StudentService studentService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudyCheckinResponse checkin(Long userId, StudyCheckinRequest request) {
        LocalDate today = LocalDate.now(CHINA_ZONE);
        log.info("用户打卡 - userId: {}, 当前日期: {}, 时区: {}", userId, today, CHINA_ZONE);

        // 检查今日是否已打卡
        StudyCheckin existingCheckin = getOne(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .eq(StudyCheckin::getCheckinDate, today)
        );

        StudyCheckin checkin;
        boolean isFirstCheckinToday = (existingCheckin == null);

        if (existingCheckin != null) {
            // 更新今日打卡记录
            existingCheckin.setStudyMinutes(
                existingCheckin.getStudyMinutes() + (request.getStudyMinutes() != null ? request.getStudyMinutes() : 0)
            );
            if (request.getStudyContent() != null) {
                String note = existingCheckin.getNote();
                existingCheckin.setNote(
                    note != null ? note + "\n" + request.getStudyContent() : request.getStudyContent()
                );
            }
            if (request.getCourseId() != null) {
                // [Phase 1.5] 使用 ObjectMapper 处理 JSON 数组，替代手动拼接
                try {
                    String coursesStudied = existingCheckin.getCoursesStudied();
                    List<Long> courseIds;
                    if (coursesStudied == null || coursesStudied.isEmpty()) {
                        courseIds = new ArrayList<>();
                    } else {
                        courseIds = objectMapper.readValue(coursesStudied, new TypeReference<List<Long>>() {});
                    }
                    if (!courseIds.contains(request.getCourseId())) {
                        courseIds.add(request.getCourseId());
                    }
                    existingCheckin.setCoursesStudied(objectMapper.writeValueAsString(courseIds));
                } catch (Exception e) {
                    log.warn("解析coursesStudied JSON失败，重新初始化", e);
                    existingCheckin.setCoursesStudied("[" + request.getCourseId() + "]");
                }
            }
            updateById(existingCheckin);
            checkin = existingCheckin;
        } else {
            // 创建新打卡记录
            checkin = new StudyCheckin();
            checkin.setUserId(userId);
            checkin.setCheckinDate(today);
            checkin.setStudyMinutes(request.getStudyMinutes() != null ? request.getStudyMinutes() : 0);
            checkin.setNote(request.getStudyContent());
            if (request.getCourseId() != null) {
                checkin.setCoursesStudied("[" + request.getCourseId() + "]");
            }
            try {
                save(checkin);
            } catch (DuplicateKeyException e) {
                // 并发打卡：唯一约束 (user_id, checkin_date) 冲突，改为更新现有记录
                log.info("并发打卡冲突，转为更新模式: userId={}, date={}", userId, today);
                existingCheckin = getOne(
                    new LambdaQueryWrapper<StudyCheckin>()
                        .eq(StudyCheckin::getUserId, userId)
                        .eq(StudyCheckin::getCheckinDate, today)
                );
                if (existingCheckin != null) {
                    existingCheckin.setStudyMinutes(
                        existingCheckin.getStudyMinutes() + (request.getStudyMinutes() != null ? request.getStudyMinutes() : 0)
                    );
                    updateById(existingCheckin);
                    checkin = existingCheckin;
                    isFirstCheckinToday = false;
                }
            }

            if (isFirstCheckinToday) {
                // 首次打卡时更新连续打卡天数（user_learning_stats 和 user_learning_streak）
                userLearningStatsService.updateStreakDays(userId);
                updateUserLearningStreak(userId, today);

                // 首次打卡奖励经验值
                int rewardExp = 20; // 每次打卡奖励20经验
                studentService.addExperience(userId, rewardExp);
                log.info("首次打卡奖励经验值, userId: {}, exp: {}", userId, rewardExp);
            }
        }

        // 更新学习时长统计
        if (request.getStudyMinutes() != null && request.getStudyMinutes() > 0) {
            userLearningStatsService.addStudyTime(userId, request.getStudyMinutes());
        }

        // [Phase 6 #25] 通过事件异步触发成就检查，缩小事务粒度
        // AchievementServiceImpl 的 @EventListener 会接收并处理
        eventPublisher.publishEvent(new LearningActivityEvent(this, userId, "checkin"));

        // 构建响应（成就在事件中异步处理，此处不再同步返回新成就）
        StudyCheckinResponse response = convertToResponse(checkin);
        response.setCurrentStreak(getCurrentStreak(userId));
        response.setNewAchievementEarned(false);

        return response;
    }

    /**
     * 更新 user_learning_streak 表（使用原子 SQL，防止并发丢失更新）
     */
    private void updateUserLearningStreak(Long userId, LocalDate today) {
        LocalDate yesterday = today.minusDays(1);

        // 尝试原子连续打卡更新（last_checkin_date == 昨天）
        int updated = userLearningStreakMapper.atomicIncrementStreak(userId, today, yesterday);
        if (updated > 0) {
            return; // 连续打卡成功
        }

        // 尝试原子断签重置（last_checkin_date != 今天）
        updated = userLearningStreakMapper.atomicResetStreak(userId, today);
        if (updated > 0) {
            return; // 断签重置成功
        }

        // 记录不存在，创建新记录（首次打卡）
        LambdaQueryWrapper<UserLearningStreak> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLearningStreak::getUserId, userId);
        UserLearningStreak existing = userLearningStreakMapper.selectOne(wrapper);

        if (existing == null) {
            UserLearningStreak streak = new UserLearningStreak();
            streak.setUserId(userId);
            streak.setStreakDays(1);
            streak.setMaxStreakDays(1);
            streak.setLastCheckinDate(today);
            streak.setTotalCheckinDays(1);
            try {
                userLearningStreakMapper.insert(streak);
            } catch (DuplicateKeyException e) {
                // 并发创建，重试原子更新
                log.debug("并发创建 streak 记录，重试原子更新: userId={}", userId);
                userLearningStreakMapper.atomicResetStreak(userId, today);
            }
        }
        // else: last_checkin_date == today（今天已经更新过，跳过）
    }

    @Override
    public boolean isTodayCheckedIn(Long userId) {
        return count(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .eq(StudyCheckin::getCheckinDate, LocalDate.now(CHINA_ZONE))
        ) > 0;
    }

    @Override
    public List<StudyCheckinResponse> getCheckinHistory(Long userId, LocalDate startDate, LocalDate endDate) {
        List<StudyCheckin> checkins = list(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .ge(StudyCheckin::getCheckinDate, startDate)
                .le(StudyCheckin::getCheckinDate, endDate)
                .orderByDesc(StudyCheckin::getCheckinDate)
        );

        // [m4 性能优化] 批量预加载课程信息，消除 N+1 查询
        Map<Long, Course> courseMap = preloadCourseMap(checkins);

        return checkins.stream()
            .map(c -> convertToResponse(c, courseMap))
            .collect(Collectors.toList());
    }

    @Override
    public int getCurrentStreak(Long userId) {
        // [Phase 4 #P3] O(1) 优化：直接从 user_learning_streak 表读取，避免逐日查询 DB
        UserLearningStreak streak = userLearningStreakMapper.selectOne(
            new LambdaQueryWrapper<UserLearningStreak>()
                .eq(UserLearningStreak::getUserId, userId)
        );
        return streak != null ? streak.getStreakDays() : 0;
    }

    @Override
    public int getMaxStreak(Long userId) {
        // [Phase 4 #P4] O(1) 优化：直接从 user_learning_streak 表读取 max_streak_days，避免加载全部打卡记录
        UserLearningStreak streak = userLearningStreakMapper.selectOne(
            new LambdaQueryWrapper<UserLearningStreak>()
                .eq(UserLearningStreak::getUserId, userId)
        );
        return streak != null ? streak.getMaxStreakDays() : 0;
    }

    @Override
    public List<LocalDate> getMonthlyCheckinDates(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        List<StudyCheckin> checkins = list(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .ge(StudyCheckin::getCheckinDate, startDate)
                .le(StudyCheckin::getCheckinDate, endDate)
        );
        
        return checkins.stream()
            .map(StudyCheckin::getCheckinDate)
            .collect(Collectors.toList());
    }

    @Override
    public StudyCheckinResponse getTodayCheckin(Long userId) {
        StudyCheckin checkin = getOne(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .eq(StudyCheckin::getCheckinDate, LocalDate.now(CHINA_ZONE))
        );
        
        return checkin != null ? convertToResponse(checkin) : null;
    }

    /**
     * 转换为响应DTO（单条，兼容无预加载场景）
     */
    private StudyCheckinResponse convertToResponse(StudyCheckin checkin) {
        return convertToResponse(checkin, null);
    }

    /**
     * 转换为响应DTO（支持预加载的课程Map，消除 N+1）
     */
    private StudyCheckinResponse convertToResponse(StudyCheckin checkin, Map<Long, Course> courseMap) {
        StudyCheckinResponse response = new StudyCheckinResponse();
        response.setId(checkin.getId());
        response.setUserId(checkin.getUserId());
        response.setCheckinDate(checkin.getCheckinDate());
        response.setStudyMinutes(checkin.getStudyMinutes());
        response.setStudyDurationFormatted(TimeFormatUtils.formatStudyTime(checkin.getStudyMinutes()));
        response.setStudyContent(checkin.getNote());
        response.setMood("normal"); // 默认心情
        response.setMoodIcon(getMoodIcon("normal"));
        response.setCreatedAt(checkin.getCreatedAt());

        // [Phase 1.5] 使用 ObjectMapper 解析课程ID列表
        String coursesStudied = checkin.getCoursesStudied();
        if (coursesStudied != null && !coursesStudied.isEmpty()) {
            try {
                List<Long> courseIds = objectMapper.readValue(coursesStudied, new TypeReference<List<Long>>() {});
                if (!courseIds.isEmpty()) {
                    Long courseId = courseIds.get(0);
                    response.setCourseId(courseId);
                    // 优先从预加载Map获取，无则回退单条查询
                    Course course = (courseMap != null) ? courseMap.get(courseId) : courseMapper.selectById(courseId);
                    if (course != null) {
                        response.setCourseName(course.getName());
                    }
                }
            } catch (Exception e) {
                log.debug("解析coursesStudied JSON失败: {}", e.getMessage());
            }
        }

        return response;
    }

    /**
     * 从打卡记录中提取所有课程ID并批量加载课程信息
     */
    private Map<Long, Course> preloadCourseMap(List<StudyCheckin> checkins) {
        Set<Long> allCourseIds = new HashSet<>();
        for (StudyCheckin checkin : checkins) {
            String coursesStudied = checkin.getCoursesStudied();
            if (coursesStudied != null && !coursesStudied.isEmpty()) {
                try {
                    List<Long> ids = objectMapper.readValue(coursesStudied, new TypeReference<List<Long>>() {});
                    allCourseIds.addAll(ids);
                } catch (Exception e) {
                    log.debug("预加载解析coursesStudied失败: {}", e.getMessage());
                }
            }
        }
        if (allCourseIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return courseMapper.selectBatchIds(allCourseIds).stream()
            .collect(Collectors.toMap(Course::getId, Function.identity()));
    }

    /**
     * 获取心情图标
     */
    private String getMoodIcon(String mood) {
        if (mood == null) {
            return "😊";
        }
        switch (mood) {
            case "happy":
                return "😄";
            case "normal":
                return "😊";
            case "tired":
                return "😴";
            case "frustrated":
                return "😤";
            default:
                return "😊";
        }
    }
}