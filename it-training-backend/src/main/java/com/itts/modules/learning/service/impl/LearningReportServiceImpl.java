package com.itts.modules.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.learning.dto.LearningReportResponse;
import com.itts.modules.learning.dto.LearningReportResponse.AchievementItem;
import com.itts.modules.learning.dto.LearningReportResponse.CategoryStudyItem;
import com.itts.modules.learning.dto.LearningReportResponse.CourseStudyItem;
import com.itts.modules.learning.dto.LearningReportResponse.DailyStudyItem;
import com.itts.modules.learning.dto.LearningReportResponse.MilestoneItem;
import com.itts.modules.learning.entity.Achievement;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.entity.StudyCheckin;
import com.itts.modules.learning.entity.UserAchievement;
import com.itts.modules.learning.mapper.AchievementMapper;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.learning.mapper.StudyCheckinMapper;
import com.itts.modules.learning.mapper.UserAchievementMapper;
import com.itts.modules.learning.service.LearningReportService;
import com.itts.modules.learning.service.StudyCheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学习报告服务实现
 */
@Service
@RequiredArgsConstructor
public class LearningReportServiceImpl implements LearningReportService {

    private final StudyCheckinMapper checkinMapper;
    private final LearningProgressMapper progressMapper;
    private final UserAchievementMapper userAchievementMapper;
    private final AchievementMapper achievementMapper;
    private final CourseMapper courseMapper;
    private final StudyCheckinService checkinService;

    @Override
    public LearningReportResponse getWeeklyReport(Long userId, LocalDate weekStart) {
        // 确保是周一
        LocalDate start = weekStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = start.plusDays(6);
        
        LearningReportResponse report = buildReport(userId, start, end, "weekly");
        
        // 计算上周数据进行对比
        LocalDate prevStart = start.minusWeeks(1);
        LocalDate prevEnd = end.minusWeeks(1);
        calculateComparison(report, userId, prevStart, prevEnd);
        
        return report;
    }

    @Override
    public LearningReportResponse getMonthlyReport(Long userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());
        
        LearningReportResponse report = buildReport(userId, start, end, "monthly");
        
        // 计算上月数据进行对比
        LocalDate prevStart = start.minusMonths(1);
        LocalDate prevEnd = prevStart.with(TemporalAdjusters.lastDayOfMonth());
        calculateComparison(report, userId, prevStart, prevEnd);
        
        return report;
    }

    @Override
    public LearningReportResponse getYearlyReport(Long userId, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        
        LearningReportResponse report = buildReport(userId, start, end, "yearly");
        
        // 计算去年数据进行对比
        LocalDate prevStart = start.minusYears(1);
        LocalDate prevEnd = end.minusYears(1);
        calculateComparison(report, userId, prevStart, prevEnd);
        
        return report;
    }

    @Override
    public LearningReportResponse getCustomReport(Long userId, LocalDate startDate, LocalDate endDate) {
        return buildReport(userId, startDate, endDate, "custom");
    }

    /**
     * 构建报告
     */
    private LearningReportResponse buildReport(Long userId, LocalDate start, LocalDate end, String periodType) {
        LearningReportResponse report = new LearningReportResponse();
        report.setPeriodType(periodType);
        report.setStartDate(start);
        report.setEndDate(end);

        // 获取打卡记录
        List<StudyCheckin> checkins = checkinMapper.selectList(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .between(StudyCheckin::getCheckinDate, start, end)
                .orderByAsc(StudyCheckin::getCheckinDate)
        );

        // 计算学习概览
        int totalMinutes = checkins.stream()
            .mapToInt(c -> c.getStudyMinutes() != null ? c.getStudyMinutes() : 0)
            .sum();
        report.setTotalStudyMinutes(totalMinutes);
        report.setStudyDays(checkins.size());
        
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        report.setAvgDailyMinutes(daysBetween > 0 ? (int)(totalMinutes / daysBetween) : 0);

        // 获取连续打卡天数
        report.setStreakDays(checkinService.getCurrentStreak(userId));

        // 获取完成的课程
        List<LearningProgress> completedProgress = progressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .eq(LearningProgress::getStatus, "completed")
                .between(LearningProgress::getCompletedAt, start.atStartOfDay(), end.plusDays(1).atStartOfDay())
        );
        report.setCompletedCourses(completedProgress.size());

        // 获取获得的成就
        List<UserAchievement> newAchievements = userAchievementMapper.selectList(
            new LambdaQueryWrapper<UserAchievement>()
                .eq(UserAchievement::getUserId, userId)
                .between(UserAchievement::getEarnedAt, start.atStartOfDay(), end.plusDays(1).atStartOfDay())
        );
        report.setEarnedAchievements(newAchievements.size());

        // 构建每日学习趋势
        List<DailyStudyItem> dailyTrend = new ArrayList<>();
        Map<LocalDate, StudyCheckin> checkinMap = checkins.stream()
            .collect(Collectors.toMap(StudyCheckin::getCheckinDate, c -> c, (a, b) -> a));
        
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            DailyStudyItem item = new DailyStudyItem();
            item.setDate(date);
            StudyCheckin checkin = checkinMap.get(date);
            if (checkin != null) {
                item.setMinutes(checkin.getStudyMinutes() != null ? checkin.getStudyMinutes() : 0);
                item.setCheckedIn(true);
            } else {
                item.setMinutes(0);
                item.setCheckedIn(false);
            }
            dailyTrend.add(item);
        }
        report.setDailyStudyTrend(dailyTrend);

        // 构建课程类别分布
        List<LearningProgress> allProgress = progressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
        );
        
        Map<String, CategoryStudyItem> categoryMap = new HashMap<>();
        for (LearningProgress progress : allProgress) {
            Course course = courseMapper.selectById(progress.getCourseId());
            if (course != null) {
                String category = course.getCategory();
                CategoryStudyItem item = categoryMap.computeIfAbsent(category, k -> {
                    CategoryStudyItem newItem = new CategoryStudyItem();
                    newItem.setCategory(k);
                    newItem.setCategoryName(getCategoryName(k));
                    newItem.setMinutes(0);
                    newItem.setCourseCount(0);
                    return newItem;
                });
                item.setMinutes(item.getMinutes() + (progress.getStudyDurationMinutes() != null ? progress.getStudyDurationMinutes() : 0));
                item.setCourseCount(item.getCourseCount() + 1);
            }
        }
        
        // 计算百分比
        int totalCategoryMinutes = categoryMap.values().stream()
            .mapToInt(CategoryStudyItem::getMinutes)
            .sum();
        for (CategoryStudyItem item : categoryMap.values()) {
            item.setPercent(totalCategoryMinutes > 0 ? item.getMinutes() * 100 / totalCategoryMinutes : 0);
        }
        report.setCategoryDistribution(new ArrayList<>(categoryMap.values()));

        // 构建进行中的课程列表
        List<LearningProgress> inProgress = progressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .eq(LearningProgress::getStatus, "in_progress")
                .orderByDesc(LearningProgress::getLastStudyAt)
        );
        
        List<CourseStudyItem> inProgressCourses = new ArrayList<>();
        for (LearningProgress progress : inProgress) {
            Course course = courseMapper.selectById(progress.getCourseId());
            if (course != null) {
                CourseStudyItem item = new CourseStudyItem();
                item.setCourseId(progress.getCourseId());
                item.setCourseName(course.getName());
                item.setCategory(course.getCategory());
                item.setProgressPercent(progress.getProgressPercent());
                item.setStudyMinutes(progress.getStudyDurationMinutes());
                item.setLastStudyDate(progress.getLastStudyAt() != null ? 
                    progress.getLastStudyAt().toLocalDate() : null);
                inProgressCourses.add(item);
            }
        }
        report.setInProgressCourses(inProgressCourses);

        // 构建已完成课程列表
        List<CourseStudyItem> completedCourseList = new ArrayList<>();
        for (LearningProgress progress : completedProgress) {
            Course course = courseMapper.selectById(progress.getCourseId());
            if (course != null) {
                CourseStudyItem item = new CourseStudyItem();
                item.setCourseId(progress.getCourseId());
                item.setCourseName(course.getName());
                item.setCategory(course.getCategory());
                item.setProgressPercent(100);
                item.setStudyMinutes(progress.getStudyDurationMinutes());
                item.setLastStudyDate(progress.getCompletedAt() != null ? 
                    progress.getCompletedAt().toLocalDate() : null);
                completedCourseList.add(item);
            }
        }
        report.setCompletedCourseList(completedCourseList);

        // 构建新获得的成就
        List<AchievementItem> achievementItems = new ArrayList<>();
        for (UserAchievement ua : newAchievements) {
            Achievement achievement = achievementMapper.selectById(ua.getAchievementId());
            if (achievement != null) {
                AchievementItem item = new AchievementItem();
                item.setId(achievement.getId());
                item.setName(achievement.getName());
                item.setDescription(achievement.getDescription());
                item.setIconUrl(achievement.getIconUrl());
                item.setPoints(achievement.getPoints());
                item.setEarnedDate(ua.getEarnedAt().toLocalDate());
                achievementItems.add(item);
            }
        }
        report.setNewAchievements(achievementItems);

        // 生成学习建议
        report.setSuggestions(generateSuggestions(report));

        // 生成里程碑
        report.setMilestones(generateMilestones(userId, start, end));

        return report;
    }

    /**
     * 计算与上期对比
     */
    private void calculateComparison(LearningReportResponse report, Long userId, 
                                     LocalDate prevStart, LocalDate prevEnd) {
        // 获取上期打卡记录
        List<StudyCheckin> prevCheckins = checkinMapper.selectList(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .between(StudyCheckin::getCheckinDate, prevStart, prevEnd)
        );

        int prevTotalMinutes = prevCheckins.stream()
            .mapToInt(c -> c.getStudyMinutes() != null ? c.getStudyMinutes() : 0)
            .sum();
        int prevStudyDays = prevCheckins.size();

        // 获取上期完成的课程
        long prevCompletedCourses = progressMapper.selectCount(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .eq(LearningProgress::getStatus, "completed")
                .between(LearningProgress::getCompletedAt, prevStart.atStartOfDay(), prevEnd.plusDays(1).atStartOfDay())
        );

        // 计算变化
        if (prevTotalMinutes > 0) {
            report.setStudyTimeChangePercent(
                (report.getTotalStudyMinutes() - prevTotalMinutes) * 100 / prevTotalMinutes
            );
        } else {
            report.setStudyTimeChangePercent(report.getTotalStudyMinutes() > 0 ? 100 : 0);
        }

        report.setStudyDaysChange(report.getStudyDays() - prevStudyDays);
        report.setCompletedCoursesChange(report.getCompletedCourses() - (int) prevCompletedCourses);
    }

    /**
     * 生成学习建议
     */
    private List<String> generateSuggestions(LearningReportResponse report) {
        List<String> suggestions = new ArrayList<>();

        // 基于学习时长
        if (report.getAvgDailyMinutes() < 30) {
            suggestions.add("建议每天至少学习30分钟，保持学习的连续性");
        } else if (report.getAvgDailyMinutes() > 120) {
            suggestions.add("学习很努力！但也要注意劳逸结合，避免疲劳学习");
        }

        // 基于打卡情况
        if (report.getStudyDays() < 3 && "weekly".equals(report.getPeriodType())) {
            suggestions.add("本周学习天数较少，建议制定固定的学习计划");
        }

        // 基于课程进度
        if (report.getInProgressCourses() != null && report.getInProgressCourses().size() > 5) {
            suggestions.add("同时学习的课程较多，建议集中精力完成1-2门课程后再开始新课程");
        }

        // 基于完成情况
        if (report.getCompletedCourses() == 0 && report.getInProgressCourses() != null 
            && !report.getInProgressCourses().isEmpty()) {
            suggestions.add("有课程正在学习中，继续加油，争取早日完成！");
        }

        // 基于连续打卡
        if (report.getStreakDays() >= 7) {
            suggestions.add("连续打卡" + report.getStreakDays() + "天，保持这个好习惯！");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("继续保持良好的学习习惯，你做得很棒！");
        }

        return suggestions;
    }

    /**
     * 生成里程碑
     */
    private List<MilestoneItem> generateMilestones(Long userId, LocalDate start, LocalDate end) {
        List<MilestoneItem> milestones = new ArrayList<>();

        // 检查是否有首次打卡
        StudyCheckin firstCheckin = checkinMapper.selectOne(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .orderByAsc(StudyCheckin::getCheckinDate)
                .last("LIMIT 1")
        );
        
        if (firstCheckin != null && !firstCheckin.getCheckinDate().isBefore(start) 
            && !firstCheckin.getCheckinDate().isAfter(end)) {
            MilestoneItem item = new MilestoneItem();
            item.setType("first_checkin");
            item.setTitle("首次打卡");
            item.setDescription("开启了学习之旅");
            item.setAchievedDate(firstCheckin.getCheckinDate());
            milestones.add(item);
        }

        // 检查是否有首次完成课程
        LearningProgress firstCompleted = progressMapper.selectOne(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .eq(LearningProgress::getStatus, "completed")
                .between(LearningProgress::getCompletedAt, start.atStartOfDay(), end.plusDays(1).atStartOfDay())
                .orderByAsc(LearningProgress::getCompletedAt)
                .last("LIMIT 1")
        );

        if (firstCompleted != null) {
            Course course = courseMapper.selectById(firstCompleted.getCourseId());
            MilestoneItem item = new MilestoneItem();
            item.setType("course_completed");
            item.setTitle("完成课程");
            item.setDescription("完成了《" + (course != null ? course.getName() : "课程") + "》的学习");
            item.setAchievedDate(firstCompleted.getCompletedAt().toLocalDate());
            milestones.add(item);
        }

        return milestones;
    }

    /**
     * 获取类别名称
     */
    private String getCategoryName(String category) {
        Map<String, String> categoryNames = new HashMap<>();
        categoryNames.put("PROGRAMMING", "编程开发");
        categoryNames.put("DATABASE", "数据库");
        categoryNames.put("FRONTEND", "前端开发");
        categoryNames.put("BACKEND", "后端开发");
        categoryNames.put("DEVOPS", "运维部署");
        categoryNames.put("AI_ML", "人工智能");
        categoryNames.put("CLOUD", "云计算");
        categoryNames.put("SECURITY", "网络安全");
        categoryNames.put("OTHER", "其他");
        return categoryNames.getOrDefault(category, category);
    }
}