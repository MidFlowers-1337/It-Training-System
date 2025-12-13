package com.itts.modules.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 报名趋势项 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentTrendItem {

    /**
     * 日期（格式：yyyy-MM-dd）
     */
    private String date;

    /**
     * 报名数量
     */
    private Long count;
}
