package com.itts.modules.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itts.common.util.TimeFormatUtils;
import com.itts.enums.LearningStatus;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.achievement.dto.AchievementResponse;
import com.itts.modules.learning.dto.LearningDashboardResponse;
import com.itts.modules.learning.dto.LearningProgressResponse;
import com.itts.modules.checkin.dto.StudyCheckinResponse;
import com.itts.modules.learning.dto.UpdateProgressRequest;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.entity.UserLearningStats;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.achievement.service.AchievementService;
import com.itts.modules.learning.service.LearningProgressService;
import com.itts.modules.checkin.service.StudyCheckinService;
import com.itts.modules.learning.service.UserLearningStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 学习进度服务实现
 */
@Service
@RequiredArgsConstructor
public class LearningProgressServiceImpl extends ServiceImpl<LearningProgressMapper, LearningProgress>
        implements LearningProgressService {

    private final CourseMapper courseMapper;
    private final UserLearningStatsService userLearningStatsService;
    private final StudyCheckinService studyCheckinService;
    private final AchievementService achievementService;

    @Override
    public LearningDashboardResponse getDashboard(Long userId) {
        LearningDashboardResponse dashboard = new LearningDashboardResponse();
        
        // 获取用户统计数据
        UserLearningStats stats = userLearningStatsService.getOrCreateStats(userId);
        
        dashboard.setTotalStudyMinutes(stats.getTotalStudyMinutes());
        dashboard.setTotalStudyFormatted(TimeFormatUtils.formatStudyTime(stats.getTotalStudyMinutes()));
        dashboard.setTotalCoursesEnrolled(stats.getTotalCoursesEnrolled());
        dashboard.setTotalCoursesCompleted(stats.getTotalCoursesCompleted());
        dashboard.setCurrentStreakDays(stats.getCurrentStreakDays());
        dashboard.setMaxStreakDays(stats.getMaxStreakDays());
        dashboard.setTotalCheckinDays(stats.getTotalCheckinDays());
        dashboard.setTotalAchievementPoints(stats.getTotalAchievementPoints());
        dashboard.setLastStudyDate(stats.getLastStudyDate());
        
        // 今日是否已打卡
        dashboard.setTodayCheckedIn(studyCheckinService.isTodayCheckedIn(userId));
        
        // 进行中的课程
        dashboard.setInProgressCourses(getInProgressCourses(userId));
        
        // 最近获得的成就
        dashboard.setRecentAchievements(achievementService.getRecentAchievements(userId, 5));
        
        // 本周学习数据
        dashboard.setWeeklyStudyData(getWeeklyStudyData(userId));
        
        return dashboard;
    }

    @Override
    public List<LearningProgressResponse> getUserProgress(Long userId) {
        List<LearningProgress> progressList = list(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .orderByDesc(LearningProgress::getLastStudyAt)
        );

        // 批量查询所有课程信息，避免 N+1 问题
        List<Long> courseIds = progressList.stream()
            .map(LearningProgress::getCourseId)
            .distinct()
            .collect(Collectors.toList());
        Map<Long, Course> courseMap = courseIds.isEmpty() ? Collections.emptyMap() :
            courseMapper.selectBatchIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));

        return progressList.stream()
            .map(p -> convertToResponse(p, courseMap.get(p.getCourseId())))
            .collect(Collectors.toList());
    }

    @Override
    public IPage<LearningProgressResponse> getUserProgress(Long userId, int page, int size) {
        Page<LearningProgress> pageParam = new Page<>(page, size);

        IPage<LearningProgress> progressPage = page(pageParam,
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .orderByDesc(LearningProgress::getLastStudyAt)
        );

        // 批量查询当前页的课程信息，避免 N+1 问题
        List<Long> courseIds = progressPage.getRecords().stream()
            .map(LearningProgress::getCourseId)
            .distinct()
            .collect(Collectors.toList());
        Map<Long, Course> courseMap = courseIds.isEmpty() ? Collections.emptyMap() :
            courseMapper.selectBatchIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));

        return progressPage.convert(p -> convertToResponse(p, courseMap.get(p.getCourseId())));
    }

    @Override
    public LearningProgressResponse getCourseProgress(Long userId, Long courseId) {
        LearningProgress progress = getOne(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .eq(LearningProgress::getCourseId, courseId)
        );
        
        return progress != null ? convertToResponse(progress) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LearningProgressResponse updateProgress(Long userId, UpdateProgressRequest request) {
        LearningProgress progress = getOne(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .eq(LearningProgress::getCourseId, request.getCourseId())
        );
        
        if (progress == null) {
            // 如果不存在，创建新的进度记录
            progress = initProgress(userId, request.getCourseId());
        }
        
        // 更新进度
        if (request.getProgressPercent() != null) {
            progress.setProgressPercent(request.getProgressPercent());
        }
        
        if (request.getStudyMinutes() != null && request.getStudyMinutes() > 0) {
            progress.setStudyDurationMinutes(
                progress.getStudyDurationMinutes() + request.getStudyMinutes()
            );
            // 更新用户总学习时长
            userLearningStatsService.addStudyTime(userId, request.getStudyMinutes());
        }
        
        progress.setLastStudyAt(LocalDateTime.now());
        
        // 检查是否完成
        if (progress.getProgressPercent() >= 100 && !LearningStatus.COMPLETED.getCode().equals(progress.getStatus())) {
            progress.setStatus(LearningStatus.COMPLETED.getCode());
            progress.setCompletedAt(LocalDateTime.now());
            // 更新完成课程数
            userLearningStatsService.incrementCompletedCourses(userId);
        } else if (progress.getProgressPercent() > 0 && LearningStatus.NOT_STARTED.getCode().equals(progress.getStatus())) {
            progress.setStatus(LearningStatus.IN_PROGRESS.getCode());
        }
        
        updateById(progress);
        
        // 检查成就
        achievementService.checkAndGrantAchievements(userId);
        
        return convertToResponse(progress);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LearningProgress initProgress(Long userId, Long courseId) {
        // 检查是否已存在
        LearningProgress existing = getOne(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .eq(LearningProgress::getCourseId, courseId)
        );
        
        if (existing != null) {
            return existing;
        }
        
        LearningProgress progress = new LearningProgress();
        progress.setUserId(userId);
        progress.setCourseId(courseId);
        progress.setProgressPercent(0);
        progress.setStudyDurationMinutes(0);
        progress.setStatus(LearningStatus.NOT_STARTED.getCode());
        save(progress);
        
        return progress;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LearningProgressResponse markCompleted(Long userId, Long courseId) {
        LearningProgress progress = getOne(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .eq(LearningProgress::getCourseId, courseId)
        );
        
        if (progress == null) {
            progress = initProgress(userId, courseId);
        }
        
        if (!LearningStatus.COMPLETED.getCode().equals(progress.getStatus())) {
            progress.setProgressPercent(100);
            progress.setStatus(LearningStatus.COMPLETED.getCode());
            progress.setCompletedAt(LocalDateTime.now());
            progress.setLastStudyAt(LocalDateTime.now());
            updateById(progress);
            
            // 更新完成课程数
            userLearningStatsService.incrementCompletedCourses(userId);
            
            // 检查成就
            achievementService.checkAndGrantAchievements(userId);
        }
        
        return convertToResponse(progress);
    }

    @Override
    public List<LearningProgressResponse> getInProgressCourses(Long userId) {
        List<LearningProgress> progressList = list(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .in(LearningProgress::getStatus, LearningStatus.NOT_STARTED.getCode(), LearningStatus.IN_PROGRESS.getCode())
                .orderByDesc(LearningProgress::getLastStudyAt)
        );

        // 批量查询所有课程信息，避免 N+1 问题
        List<Long> courseIds = progressList.stream()
            .map(LearningProgress::getCourseId)
            .distinct()
            .collect(Collectors.toList());
        Map<Long, Course> courseMap = courseIds.isEmpty() ? Collections.emptyMap() :
            courseMapper.selectBatchIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));

        return progressList.stream()
            .map(p -> convertToResponse(p, courseMap.get(p.getCourseId())))
            .collect(Collectors.toList());
    }

    @Override
    public IPage<LearningProgressResponse> getInProgressCourses(Long userId, int page, int size) {
        Page<LearningProgress> pageParam = new Page<>(page, size);

        IPage<LearningProgress> progressPage = page(pageParam,
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .in(LearningProgress::getStatus, LearningStatus.NOT_STARTED.getCode(), LearningStatus.IN_PROGRESS.getCode())
                .orderByDesc(LearningProgress::getLastStudyAt)
        );

        // 批量查询当前页的课程信息，避免 N+1 问题
        List<Long> courseIds = progressPage.getRecords().stream()
            .map(LearningProgress::getCourseId)
            .distinct()
            .collect(Collectors.toList());
        Map<Long, Course> courseMap = courseIds.isEmpty() ? Collections.emptyMap() :
            courseMapper.selectBatchIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));

        return progressPage.convert(p -> convertToResponse(p, courseMap.get(p.getCourseId())));
    }

    /**
     * 转换为响应DTO（单条查询场景，内部查库获取课程信息）
     */
    private LearningProgressResponse convertToResponse(LearningProgress progress) {
        Course course = courseMapper.selectById(progress.getCourseId());
        return convertToResponse(progress, course);
    }

    /**
     * 转换为响应DTO（批量查询场景，接收已查询的课程对象）
     */
    private LearningProgressResponse convertToResponse(LearningProgress progress, Course course) {
        LearningProgressResponse response = new LearningProgressResponse();
        response.setId(progress.getId());
        response.setUserId(progress.getUserId());
        response.setCourseId(progress.getCourseId());
        response.setProgressPercent(progress.getProgressPercent());
        response.setStudyDurationMinutes(progress.getStudyDurationMinutes());
        response.setStudyDurationFormatted(TimeFormatUtils.formatStudyTime(progress.getStudyDurationMinutes()));
        response.setLastStudyAt(progress.getLastStudyAt());
        response.setStatus(progress.getStatus());
        response.setCompletedAt(progress.getCompletedAt());

        // 填充课程信息
        if (course != null) {
            response.setCourseName(course.getName());
            response.setCourseCategory(course.getCategory());
            response.setCourseDifficulty(course.getDifficulty());
            response.setCourseCoverImage(course.getCoverImage());
        }

        return response;
    }

    /**
     * 获取本周学习数据
     * [Phase 4 #P6] 优化：一次查询整周打卡记录，避免循环 7 次查询
     */
    private List<LearningDashboardResponse.DailyStudyItem> getWeeklyStudyData(Long userId) {
        List<LearningDashboardResponse.DailyStudyItem> weeklyData = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 获取本周一
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        String[] dayNames = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

        // 一次查询整周所有打卡记录
        List<StudyCheckinResponse> weekCheckins = studyCheckinService.getCheckinHistory(userId, monday, sunday);
        Map<LocalDate, StudyCheckinResponse> checkinMap = weekCheckins.stream()
            .collect(Collectors.toMap(StudyCheckinResponse::getCheckinDate, c -> c, (a, b) -> a));

        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);
            LearningDashboardResponse.DailyStudyItem item = new LearningDashboardResponse.DailyStudyItem();
            item.setDate(date);
            item.setDayOfWeek(dayNames[i]);

            StudyCheckinResponse checkin = checkinMap.get(date);
            item.setStudyMinutes(checkin != null ? checkin.getStudyMinutes() : 0);

            weeklyData.add(item);
        }

        return weeklyData;
    }
}