package com.itts.modules.report.controller;

import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.report.dto.LearningReportResponse;
import com.itts.modules.report.service.LearningReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "学习报告", description = "学习报告相关接口")
@RestController
@RequestMapping("/api/v1/learning")
@RequiredArgsConstructor
public class LearningReportController {

    private final LearningReportService learningReportService;

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
}
