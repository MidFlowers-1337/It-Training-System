package com.itts.modules.learning.controller;

import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.learning.dto.AchievementResponse;
import com.itts.modules.learning.dto.CourseRecommendResponse;
import com.itts.modules.learning.dto.LearningDashboardResponse;
import com.itts.modules.learning.dto.LearningPlanRequest;
import com.itts.modules.learning.dto.LearningPlanResponse;
import com.itts.modules.learning.dto.LearningProgressResponse;
import com.itts.modules.learning.dto.LearningReportResponse;
import com.itts.modules.learning.dto.StudyCheckinRequest;
import com.itts.modules.learning.dto.StudyCheckinResponse;
import com.itts.modules.learning.dto.UpdatePreferencesRequest;
import com.itts.modules.learning.dto.UpdateProgressRequest;
import com.itts.modules.learning.dto.UserProfileResponse;
import com.itts.modules.learning.dto.UserStatsResponse;
import com.itts.modules.learning.service.AchievementService;
import com.itts.modules.learning.service.CollaborativeFilteringService;
import com.itts.modules.learning.service.ContentBasedRecommendService;
import com.itts.modules.learning.service.HybridRecommendService;
import com.itts.modules.learning.service.LearningPlanService;
import com.itts.modules.learning.service.LearningProgressService;
import com.itts.modules.learning.service.LearningReportService;
import com.itts.modules.learning.service.StudyCheckinService;
import com.itts.modules.learning.service.UserLearningStatsService;
import com.itts.modules.learning.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 学习管理控制器
 */
@Tag(name = "学习管理", description = "学习进度、打卡、成就、学习计划、学习报告、智能推荐、用户画像等接口")
@RestController
@RequestMapping("/api/v1/learning")
@RequiredArgsConstructor
public class LearningController {

    private final LearningProgressService learningProgressService;
    private final StudyCheckinService studyCheckinService;
    private final AchievementService achievementService;
    private final UserLearningStatsService userLearningStatsService;
    private final LearningPlanService learningPlanService;
    private final LearningReportService learningReportService;
    private final ContentBasedRecommendService contentBasedRecommendService;
    private final CollaborativeFilteringService collaborativeFilteringService;
    private final HybridRecommendService hybridRecommendService;
    private final UserProfileService userProfileService;

    // ==================== 学习仪表盘 ====================

    @Operation(summary = "获取学习仪表盘")
    @GetMapping("/dashboard")
    public R<LearningDashboardResponse> getDashboard() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningProgressService.getDashboard(userId));
    }

    @Operation(summary = "获取用户学习统计")
    @GetMapping("/stats")
    public R<UserStatsResponse> getUserStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(userLearningStatsService.getUserStats(userId));
    }

    // ==================== 学习进度 ====================

    @Operation(summary = "获取所有课程学习进度")
    @GetMapping("/progress")
    public R<List<LearningProgressResponse>> getUserProgress() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningProgressService.getUserProgress(userId));
    }

    @Operation(summary = "获取指定课程学习进度")
    @GetMapping("/progress/course/{courseId}")
    public R<LearningProgressResponse> getCourseProgress(@PathVariable Long courseId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningProgressService.getCourseProgress(userId, courseId));
    }

    @Operation(summary = "更新学习进度")
    @PostMapping("/progress/update")
    public R<LearningProgressResponse> updateProgress(@Valid @RequestBody UpdateProgressRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningProgressService.updateProgress(userId, request));
    }

    @Operation(summary = "标记课程完成")
    @PostMapping("/progress/complete/{courseId}")
    public R<LearningProgressResponse> markCompleted(@PathVariable Long courseId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningProgressService.markCompleted(userId, courseId));
    }

    @Operation(summary = "获取进行中的课程")
    @GetMapping("/progress/in-progress")
    public R<List<LearningProgressResponse>> getInProgressCourses() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningProgressService.getInProgressCourses(userId));
    }

    // ==================== 学习打卡 ====================

    @Operation(summary = "学习打卡")
    @PostMapping("/checkin")
    public R<StudyCheckinResponse> checkin(@Valid @RequestBody StudyCheckinRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(studyCheckinService.checkin(userId, request));
    }

    @Operation(summary = "检查今日是否已打卡")
    @GetMapping("/checkin/today")
    public R<Boolean> isTodayCheckedIn() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(studyCheckinService.isTodayCheckedIn(userId));
    }

    @Operation(summary = "获取今日打卡详情")
    @GetMapping("/checkin/today/detail")
    public R<StudyCheckinResponse> getTodayCheckin() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(studyCheckinService.getTodayCheckin(userId));
    }

    @Operation(summary = "获取打卡历史")
    @GetMapping("/checkin/history")
    public R<List<StudyCheckinResponse>> getCheckinHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(studyCheckinService.getCheckinHistory(userId, startDate, endDate));
    }

    @Operation(summary = "获取当前连续打卡天数")
    @GetMapping("/checkin/streak")
    public R<Integer> getCurrentStreak() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(studyCheckinService.getCurrentStreak(userId));
    }

    @Operation(summary = "获取月度打卡日历")
    @GetMapping("/checkin/calendar/{year}/{month}")
    public R<List<LocalDate>> getMonthlyCheckinDates(
            @PathVariable int year,
            @PathVariable int month) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(studyCheckinService.getMonthlyCheckinDates(userId, year, month));
    }

    // ==================== 成就系统 ====================

    @Operation(summary = "获取所有成就（含获得状态）")
    @GetMapping("/achievements")
    public R<List<AchievementResponse>> getAllAchievements() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(achievementService.getAllAchievements(userId));
    }

    @Operation(summary = "获取已获得的成就")
    @GetMapping("/achievements/earned")
    public R<List<AchievementResponse>> getUserAchievements() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(achievementService.getUserAchievements(userId));
    }

    @Operation(summary = "获取最近获得的成就")
    @GetMapping("/achievements/recent")
    public R<List<AchievementResponse>> getRecentAchievements(
            @RequestParam(defaultValue = "5") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(achievementService.getRecentAchievements(userId, limit));
    }

    @Operation(summary = "获取成就详情")
    @GetMapping("/achievements/{achievementId}")
    public R<AchievementResponse> getAchievementDetail(@PathVariable Long achievementId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(achievementService.getAchievementDetail(achievementId, userId));
    }

    @Operation(summary = "获取成就积分")
    @GetMapping("/achievements/points")
    public R<Integer> getAchievementPoints() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(achievementService.getUserAchievementPoints(userId));
    }

    // ==================== 学习计划 ====================

    @Operation(summary = "创建学习计划")
    @PostMapping("/plans")
    public R<LearningPlanResponse> createPlan(@Valid @RequestBody LearningPlanRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.createPlan(userId, request));
    }

    @Operation(summary = "获取所有学习计划")
    @GetMapping("/plans")
    public R<List<LearningPlanResponse>> getUserPlans() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.getUserPlans(userId));
    }

    @Operation(summary = "获取当前进行中的学习计划")
    @GetMapping("/plans/active")
    public R<LearningPlanResponse> getActivePlan() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.getActivePlan(userId));
    }

    @Operation(summary = "获取学习计划详情")
    @GetMapping("/plans/{planId}")
    public R<LearningPlanResponse> getPlanDetail(@PathVariable Long planId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.getPlanDetail(userId, planId));
    }

    @Operation(summary = "更新学习计划")
    @PutMapping("/plans/{planId}")
    public R<LearningPlanResponse> updatePlan(
            @PathVariable Long planId,
            @Valid @RequestBody LearningPlanRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.updatePlan(userId, planId, request));
    }

    @Operation(summary = "暂停学习计划")
    @PostMapping("/plans/{planId}/pause")
    public R<LearningPlanResponse> pausePlan(@PathVariable Long planId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.pausePlan(userId, planId));
    }

    @Operation(summary = "恢复学习计划")
    @PostMapping("/plans/{planId}/resume")
    public R<LearningPlanResponse> resumePlan(@PathVariable Long planId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.resumePlan(userId, planId));
    }

    @Operation(summary = "取消学习计划")
    @PostMapping("/plans/{planId}/cancel")
    public R<LearningPlanResponse> cancelPlan(@PathVariable Long planId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.cancelPlan(userId, planId));
    }

    @Operation(summary = "完成学习计划")
    @PostMapping("/plans/{planId}/complete")
    public R<LearningPlanResponse> completePlan(@PathVariable Long planId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.completePlan(userId, planId));
    }

    // ==================== 学习报告 ====================

    @Operation(summary = "获取周报")
    @GetMapping("/reports/weekly")
    public R<LearningReportResponse> getWeeklyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningReportService.getWeeklyReport(userId, weekStart));
    }

    @Operation(summary = "获取月报")
    @GetMapping("/reports/monthly/{year}/{month}")
    public R<LearningReportResponse> getMonthlyReport(
            @PathVariable int year,
            @PathVariable int month) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningReportService.getMonthlyReport(userId, year, month));
    }

    @Operation(summary = "获取年报")
    @GetMapping("/reports/yearly/{year}")
    public R<LearningReportResponse> getYearlyReport(@PathVariable int year) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningReportService.getYearlyReport(userId, year));
    }

    @Operation(summary = "获取自定义时间段报告")
    @GetMapping("/reports/custom")
    public R<LearningReportResponse> getCustomReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningReportService.getCustomReport(userId, startDate, endDate));
    }

    // ==================== 智能推荐 ====================

    @Operation(summary = "基于学习历史推荐课程")
    @GetMapping("/recommend/history")
    public R<List<CourseRecommendResponse>> recommendByHistory(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(contentBasedRecommendService.recommendByUserHistory(userId, limit));
    }

    @Operation(summary = "推荐相似课程")
    @GetMapping("/recommend/similar/{courseId}")
    public R<List<CourseRecommendResponse>> recommendSimilarCourses(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "5") int limit) {
        return R.ok(contentBasedRecommendService.recommendSimilarCourses(courseId, limit));
    }

    @Operation(summary = "基于技能标签推荐课程")
    @GetMapping("/recommend/skills")
    public R<List<CourseRecommendResponse>> recommendBySkills(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(contentBasedRecommendService.recommendByUserSkills(userId, limit));
    }

    @Operation(summary = "基于用户偏好推荐课程")
    @GetMapping("/recommend/preference")
    public R<List<CourseRecommendResponse>> recommendByPreference(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(contentBasedRecommendService.recommendByUserPreference(userId, limit));
    }

    // ==================== 协同过滤推荐 ====================

    @Operation(summary = "基于用户的协同过滤推荐")
    @GetMapping("/recommend/user-based")
    public R<List<CourseRecommendResponse>> recommendByUserBased(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(collaborativeFilteringService.recommendByUserBased(userId, limit));
    }

    @Operation(summary = "基于物品的协同过滤推荐")
    @GetMapping("/recommend/item-based")
    public R<List<CourseRecommendResponse>> recommendByItemBased(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(collaborativeFilteringService.recommendByItemBased(userId, limit));
    }

    @Operation(summary = "获取相似用户")
    @GetMapping("/recommend/similar-users")
    public R<List<Long>> getSimilarUsers(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(collaborativeFilteringService.getSimilarUsers(userId, limit));
    }

    // ==================== 混合推荐 ====================

    @Operation(summary = "获取混合推荐课程")
    @GetMapping("/recommend/hybrid")
    public R<List<CourseRecommendResponse>> getHybridRecommendations(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(hybridRecommendService.getHybridRecommendations(userId, limit));
    }

    @Operation(summary = "获取首页推荐课程")
    @GetMapping("/recommend/home")
    public R<List<CourseRecommendResponse>> getHomePageRecommendations() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(hybridRecommendService.getHomePageRecommendations(userId));
    }

    @Operation(summary = "获取猜你喜欢课程")
    @GetMapping("/recommend/you-may-like")
    public R<List<CourseRecommendResponse>> getYouMayLike(
            @RequestParam(defaultValue = "6") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(hybridRecommendService.getYouMayLike(userId, limit));
    }

    @Operation(summary = "获取热门课程")
    @GetMapping("/recommend/popular")
    public R<List<CourseRecommendResponse>> getPopularCourses(
            @RequestParam(defaultValue = "10") int limit) {
        return R.ok(hybridRecommendService.getPopularCourses(limit));
    }

    @Operation(summary = "获取新上架课程")
    @GetMapping("/recommend/new")
    public R<List<CourseRecommendResponse>> getNewCourses(
            @RequestParam(defaultValue = "10") int limit) {
        return R.ok(hybridRecommendService.getNewCourses(limit));
    }

    // ==================== 用户画像 ====================

    @Operation(summary = "获取用户画像")
    @GetMapping("/profile")
    public R<UserProfileResponse> getUserProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(userProfileService.getUserProfile(userId));
    }

    @Operation(summary = "更新技能标签")
    @PostMapping("/profile/skills")
    public R<Void> updateSkillTags(@RequestBody List<String> skillTags) {
        Long userId = SecurityUtils.getCurrentUserId();
        userProfileService.updateSkillTags(userId, skillTags);
        return R.ok();
    }

    @Operation(summary = "更新学习偏好")
    @PostMapping("/profile/preferences")
    public R<Void> updatePreferences(@RequestBody UpdatePreferencesRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        userProfileService.updatePreferences(userId,
            request.getPreferredCategories(),
            request.getPreferredDifficulty(),
            request.getDailyStudyGoal());
        return R.ok();
    }

    @Operation(summary = "获取学习能力评估")
    @GetMapping("/profile/ability-assessment")
    public R<Map<String, Object>> getLearningAbilityAssessment() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(userProfileService.getLearningAbilityAssessment(userId));
    }

    @Operation(summary = "分析学习行为")
    @PostMapping("/profile/analyze")
    public R<Void> analyzeLearningBehavior() {
        Long userId = SecurityUtils.getCurrentUserId();
        userProfileService.analyzeLearningBehavior(userId);
        return R.ok();
    }
}