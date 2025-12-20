package com.itts.modules.stats.controller;

import com.itts.common.response.R;
import com.itts.modules.stats.dto.CourseHotItem;
import com.itts.modules.stats.dto.EnrollmentTrendItem;
import com.itts.modules.stats.dto.StatsOverviewResponse;
import com.itts.modules.stats.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public R<StatsOverviewResponse> getOverview() {
        StatsOverviewResponse overview = statsService.getOverview();
        return R.ok(overview);
    }

    @Operation(summary = "获取课程热度排行")
    @GetMapping("/course-hot")
    public R<List<CourseHotItem>> getCourseHotRanking(
            @Parameter(description = "数量限制，默认10") @RequestParam(defaultValue = "10") int limit) {
        List<CourseHotItem> ranking = statsService.getCourseHotRanking(limit);
        return R.ok(ranking);
    }

    @Operation(summary = "获取报名趋势")
    @GetMapping("/enrollment-trend")
    public R<List<EnrollmentTrendItem>> getEnrollmentTrend(
            @Parameter(description = "天数，默认7天") @RequestParam(defaultValue = "7") int days) {
        List<EnrollmentTrendItem> trend = statsService.getEnrollmentTrend(days);
        return R.ok(trend);
    }
}
