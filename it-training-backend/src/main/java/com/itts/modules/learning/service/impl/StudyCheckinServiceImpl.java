package com.itts.modules.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.learning.dto.AchievementResponse;
import com.itts.modules.learning.dto.StudyCheckinRequest;
import com.itts.modules.learning.dto.StudyCheckinResponse;
import com.itts.modules.learning.entity.StudyCheckin;
import com.itts.modules.learning.mapper.StudyCheckinMapper;
import com.itts.modules.learning.service.AchievementService;
import com.itts.modules.learning.service.StudyCheckinService;
import com.itts.modules.learning.service.UserLearningStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学习打卡服务实现
 */
@Service
@RequiredArgsConstructor
public class StudyCheckinServiceImpl extends ServiceImpl<StudyCheckinMapper, StudyCheckin>
        implements StudyCheckinService {

    private final CourseMapper courseMapper;
    private final UserLearningStatsService userLearningStatsService;
    private final AchievementService achievementService;

    @Override
    @Transactional
    public StudyCheckinResponse checkin(Long userId, StudyCheckinRequest request) {
        LocalDate today = LocalDate.now();
        
        // 检查今日是否已打卡
        StudyCheckin existingCheckin = getOne(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .eq(StudyCheckin::getCheckinDate, today)
        );
        
        StudyCheckin checkin;
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
                // 将课程ID添加到JSON数组
                String coursesStudied = existingCheckin.getCoursesStudied();
                if (coursesStudied == null || coursesStudied.isEmpty()) {
                    existingCheckin.setCoursesStudied("[" + request.getCourseId() + "]");
                } else if (!coursesStudied.contains(request.getCourseId().toString())) {
                    existingCheckin.setCoursesStudied(
                        coursesStudied.substring(0, coursesStudied.length() - 1) + "," + request.getCourseId() + "]"
                    );
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
            save(checkin);
            
            // 更新连续打卡天数
            userLearningStatsService.updateStreakDays(userId);
        }
        
        // 更新学习时长统计
        if (request.getStudyMinutes() != null && request.getStudyMinutes() > 0) {
            userLearningStatsService.addStudyTime(userId, request.getStudyMinutes());
        }
        
        // 检查并授予成就
        List<AchievementResponse> newAchievements = achievementService.checkAndGrantAchievements(userId);
        
        // 构建响应
        StudyCheckinResponse response = convertToResponse(checkin);
        response.setCurrentStreak(getCurrentStreak(userId));
        
        if (!newAchievements.isEmpty()) {
            response.setNewAchievementEarned(true);
            response.setNewAchievement(newAchievements.get(0));
        } else {
            response.setNewAchievementEarned(false);
        }
        
        return response;
    }

    @Override
    public boolean isTodayCheckedIn(Long userId) {
        return count(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .eq(StudyCheckin::getCheckinDate, LocalDate.now())
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
        
        return checkins.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public int getCurrentStreak(Long userId) {
        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate checkDate = today;
        
        while (true) {
            boolean hasCheckin = count(
                new LambdaQueryWrapper<StudyCheckin>()
                    .eq(StudyCheckin::getUserId, userId)
                    .eq(StudyCheckin::getCheckinDate, checkDate)
            ) > 0;
            
            if (hasCheckin) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else {
                // 如果今天还没打卡，检查昨天
                if (checkDate.equals(today)) {
                    checkDate = checkDate.minusDays(1);
                    continue;
                }
                break;
            }
        }
        
        return streak;
    }

    @Override
    public int getMaxStreak(Long userId) {
        // 获取所有打卡记录
        List<StudyCheckin> checkins = list(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .orderByAsc(StudyCheckin::getCheckinDate)
        );
        
        if (checkins.isEmpty()) {
            return 0;
        }
        
        int maxStreak = 1;
        int currentStreak = 1;
        
        for (int i = 1; i < checkins.size(); i++) {
            LocalDate prevDate = checkins.get(i - 1).getCheckinDate();
            LocalDate currDate = checkins.get(i).getCheckinDate();
            
            if (prevDate.plusDays(1).equals(currDate)) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
        }
        
        return maxStreak;
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
                .eq(StudyCheckin::getCheckinDate, LocalDate.now())
        );
        
        return checkin != null ? convertToResponse(checkin) : null;
    }

    /**
     * 转换为响应DTO
     */
    private StudyCheckinResponse convertToResponse(StudyCheckin checkin) {
        StudyCheckinResponse response = new StudyCheckinResponse();
        response.setId(checkin.getId());
        response.setUserId(checkin.getUserId());
        response.setCheckinDate(checkin.getCheckinDate());
        response.setStudyMinutes(checkin.getStudyMinutes());
        response.setStudyDurationFormatted(formatStudyTime(checkin.getStudyMinutes()));
        response.setStudyContent(checkin.getNote());
        response.setMood("normal"); // 默认心情
        response.setMoodIcon(getMoodIcon("normal"));
        response.setCreatedAt(checkin.getCreatedAt());
        
        // 解析课程ID列表并获取第一个课程名称
        String coursesStudied = checkin.getCoursesStudied();
        if (coursesStudied != null && !coursesStudied.isEmpty()) {
            try {
                // 简单解析JSON数组 [1,2,3]
                String cleaned = coursesStudied.replaceAll("[\\[\\]\\s]", "");
                if (!cleaned.isEmpty()) {
                    String[] ids = cleaned.split(",");
                    if (ids.length > 0) {
                        Long courseId = Long.parseLong(ids[0].trim());
                        response.setCourseId(courseId);
                        Course course = courseMapper.selectById(courseId);
                        if (course != null) {
                            response.setCourseName(course.getName());
                        }
                    }
                }
            } catch (Exception e) {
                // 忽略解析错误
            }
        }
        
        return response;
    }

    /**
     * 格式化学习时长
     */
    private String formatStudyTime(int minutes) {
        if (minutes < 60) {
            return minutes + "分钟";
        }
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        if (remainingMinutes == 0) {
            return hours + "小时";
        }
        return hours + "小时" + remainingMinutes + "分钟";
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