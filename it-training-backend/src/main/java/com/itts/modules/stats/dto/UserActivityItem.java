package com.itts.modules.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学员活跃度统计项 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityItem {

    /**
     * 日期（格式：yyyy-MM-dd）
     */
    private String date;

    /**
     * 活跃用户数
     */
    private Long activeCount;
}
