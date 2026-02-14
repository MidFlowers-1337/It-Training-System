package com.itts.modules.stats.dto;

import lombok.Data;

@Data
public class PublicStatsResponse {
    /** 课程总数 */
    private Long courseCount;
    /** 学员总数 */
    private Long studentCount;
    /** 讲师总数 */
    private Long instructorCount;
    /** 平均完课率（百分比） */
    private Double completionRate;
}
