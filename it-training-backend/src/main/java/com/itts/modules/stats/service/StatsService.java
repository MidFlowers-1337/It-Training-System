package com.itts.modules.stats.service;

import com.itts.modules.stats.dto.CourseHotItem;
import com.itts.modules.stats.dto.EnrollmentTrendItem;
import com.itts.modules.stats.dto.StatsOverviewResponse;

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
}
