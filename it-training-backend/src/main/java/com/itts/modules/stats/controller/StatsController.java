package com.itts.modules.stats.controller;

import com.itts.common.response.R;
import com.itts.modules.stats.dto.CategoryDistributionItem;
import com.itts.modules.stats.dto.CourseCompletionRateItem;
import com.itts.modules.stats.dto.CourseHotItem;
import com.itts.modules.stats.dto.EnrollmentTrendItem;
import com.itts.modules.stats.dto.StatsOverviewResponse;
import com.itts.modules.stats.dto.UserActivityItem;
import com.itts.modules.stats.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 统计看板控制器
 */
@Tag(name = "统计看板", description = "数据统计和报表")
@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "获取统计概览")
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public R<StatsOverviewResponse> getOverview() {
        StatsOverviewResponse overview = statsService.getOverview();
        return R.ok(overview);
    }

    @Operation(summary = "获取课程热度排行")
    @GetMapping("/course-hot")
    @PreAuthorize("hasRole('ADMIN')")
    public R<List<CourseHotItem>> getCourseHotRanking(
            @Parameter(description = "数量限制，默认10") @RequestParam(defaultValue = "10") int limit) {
        List<CourseHotItem> ranking = statsService.getCourseHotRanking(limit);
        return R.ok(ranking);
    }

    @Operation(summary = "获取报名趋势")
    @GetMapping("/enrollment-trend")
    @PreAuthorize("hasRole('ADMIN')")
    public R<List<EnrollmentTrendItem>> getEnrollmentTrend(
            @Parameter(description = "天数，默认7天") @RequestParam(defaultValue = "7") int days) {
        List<EnrollmentTrendItem> trend = statsService.getEnrollmentTrend(days);
        return R.ok(trend);
    }

    @Operation(summary = "获取学员活跃度统计")
    @GetMapping("/user-activity")
    @PreAuthorize("hasRole('ADMIN')")
    public R<List<UserActivityItem>> getUserActivity(
            @Parameter(description = "天数，默认30天") @RequestParam(defaultValue = "30") int days) {
        List<UserActivityItem> activity = statsService.getUserActivity(days);
        return R.ok(activity);
    }

    @Operation(summary = "获取课程完课率统计")
    @GetMapping("/completion-rate")
    @PreAuthorize("hasRole('ADMIN')")
    public R<List<CourseCompletionRateItem>> getCompletionRate() {
        List<CourseCompletionRateItem> rates = statsService.getCompletionRate();
        return R.ok(rates);
    }

    @Operation(summary = "获取分类维度报名分布")
    @GetMapping("/category-distribution")
    @PreAuthorize("hasRole('ADMIN')")
    public R<List<CategoryDistributionItem>> getCategoryDistribution() {
        List<CategoryDistributionItem> distribution = statsService.getCategoryDistribution();
        return R.ok(distribution);
    }

    @Operation(summary = "导出报名记录（CSV）")
    @GetMapping("/export/enrollments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody> exportEnrollments() {
        org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody body = outputStream -> {
            statsService.exportEnrollmentsCSV(outputStream);
        };
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=enrollments.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(body);
    }
}
