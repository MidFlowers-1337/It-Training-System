package com.itts.modules.stats.dto;

import lombok.Data;

/**
 * 统计概览响应 DTO
 */
@Data
public class StatsOverviewResponse {

    /**
     * 课程总数
     */
    private Long courseCount;

    /**
     * 班期总数
     */
    private Long sessionCount;

    /**
     * 学员总数
     */
    private Long studentCount;

    /**
     * 讲师总数
     */
    private Long instructorCount;

    /**
     * 报名总数
     */
    private Long enrollmentCount;

    /**
     * 本月报名数
     */
    private Long monthlyEnrollmentCount;

    /**
     * 进行中的班期数
     */
    private Long activeSessionCount;
}
