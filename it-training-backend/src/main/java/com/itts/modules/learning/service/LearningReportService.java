package com.itts.modules.learning.service;

import com.itts.modules.learning.dto.LearningReportResponse;

import java.time.LocalDate;

/**
 * 学习报告服务接口
 */
public interface LearningReportService {

    /**
     * 获取周报
     * @param userId 用户ID
     * @param weekStart 周开始日期（周一）
     * @return 周报
     */
    LearningReportResponse getWeeklyReport(Long userId, LocalDate weekStart);

    /**
     * 获取月报
     * @param userId 用户ID
     * @param year 年份
     * @param month 月份
     * @return 月报
     */
    LearningReportResponse getMonthlyReport(Long userId, int year, int month);

    /**
     * 获取年报
     * @param userId 用户ID
     * @param year 年份
     * @return 年报
     */
    LearningReportResponse getYearlyReport(Long userId, int year);

    /**
     * 获取自定义时间段报告
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 报告
     */
    LearningReportResponse getCustomReport(Long userId, LocalDate startDate, LocalDate endDate);
}