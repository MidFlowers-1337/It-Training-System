package com.itts.modules.stats.service;

import com.itts.modules.stats.dto.CategoryDistributionItem;
import com.itts.modules.stats.dto.CourseCompletionRateItem;
import com.itts.modules.stats.dto.CourseHotItem;
import com.itts.modules.stats.dto.EnrollmentTrendItem;
import com.itts.modules.stats.dto.PublicStatsResponse;
import com.itts.modules.stats.dto.StatsOverviewResponse;
import com.itts.modules.stats.dto.UserActivityItem;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 统计服务接口
 */
public interface StatsService {

    /**
     * 获取统计概览数据
     */
    StatsOverviewResponse getOverview();

    /**
     * 获取课程热度排行 TOP N
     * @param limit 数量限制
     */
    List<CourseHotItem> getCourseHotRanking(int limit);

    /**
     * 获取报名趋势数据
     * @param days 天数（7天或30天）
     */
    List<EnrollmentTrendItem> getEnrollmentTrend(int days);

    /**
     * 获取公开平台统计数据（Landing Page）
     */
    PublicStatsResponse getPublicStats();

    /**
     * 获取学员活跃度统计（最近N天每天的活跃用户数）
     * @param days 天数（默认30天）
     */
    List<UserActivityItem> getUserActivity(int days);

    /**
     * 获取按课程维度的完课率统计
     */
    List<CourseCompletionRateItem> getCompletionRate();

    /**
     * 获取按分类维度的报名分布统计
     */
    List<CategoryDistributionItem> getCategoryDistribution();

    /**
     * 导出报名记录为 CSV 字符串（含课程名、学员名、班期编号）
     * @return CSV 格式字符串（UTF-8 BOM + 表头 + 数据行）
     * @deprecated 数据量大时有 OOM 风险，建议使用 {@link #exportEnrollmentsCSV(OutputStream)}
     */
    @Deprecated
    String exportEnrollmentsCSV();

    /**
     * 流式导出报名记录为 CSV（逐行写入 OutputStream，避免全量内存驻留）
     *
     * @param out 输出流（由 Controller 的 StreamingResponseBody 提供）
     * @throws IOException 写入异常
     */
    void exportEnrollmentsCSV(OutputStream out) throws IOException;
}
