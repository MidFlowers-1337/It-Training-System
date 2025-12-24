package com.itts.modules.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itts.enums.LearningStatus;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.learning.dto.AchievementResponse;
import com.itts.modules.learning.dto.LearningDashboardResponse;
import com.itts.modules.learning.dto.LearningProgressResponse;
import com.itts.modules.learning.dto.UpdateProgressRequest;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.entity.UserLearningStats;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.learning.service.AchievementService;
import com.itts.modules.learning.service.LearningProgressService;
import com.itts.modules.learning.service.StudyCheckinService;
import com.itts.modules.learning.service.UserLearningStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        dashboard.setTotalStudyFormatted(formatStudyTime(stats.getTotalStudyMinutes()));
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
        
        return progressList.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
        
        return progressList.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * 转换为响应DTO
     */
    private LearningProgressResponse convertToResponse(LearningProgress progress) {
        LearningProgressResponse response = new LearningProgressResponse();
        response.setId(progress.getId());
        response.setUserId(progress.getUserId());
        response.setCourseId(progress.getCourseId());
        response.setProgressPercent(progress.getProgressPercent());
        response.setStudyDurationMinutes(progress.getStudyDurationMinutes());
        response.setStudyDurationFormatted(formatStudyTime(progress.getStudyDurationMinutes()));
        response.setLastStudyAt(progress.getLastStudyAt());
        response.setStatus(progress.getStatus());
        response.setCompletedAt(progress.getCompletedAt());
        
        // 获取课程信息
        Course course = courseMapper.selectById(progress.getCourseId());
        if (course != null) {
            response.setCourseName(course.getName());
            response.setCourseCategory(course.getCategory());
            response.setCourseDifficulty(course.getDifficulty());
            response.setCourseCoverImage(course.getCoverImage());
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
     * 获取本周学习数据
     */
    private List<LearningDashboardResponse.DailyStudyItem> getWeeklyStudyData(Long userId) {
        List<LearningDashboardResponse.DailyStudyItem> weeklyData = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        // 获取本周一
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        
        String[] dayNames = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);
            LearningDashboardResponse.DailyStudyItem item = new LearningDashboardResponse.DailyStudyItem();
            item.setDate(date);
            item.setDayOfWeek(dayNames[i]);
            
            // 获取该日期的打卡记录
            var checkin = studyCheckinService.getCheckinHistory(userId, date, date);
            if (!checkin.isEmpty()) {
                item.setStudyMinutes(checkin.get(0).getStudyMinutes());
            } else {
                item.setStudyMinutes(0);
            }
            
            weeklyData.add(item);
        }
        
        return weeklyData;
    }
}