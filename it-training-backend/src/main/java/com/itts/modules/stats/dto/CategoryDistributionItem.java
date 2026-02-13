package com.itts.modules.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类维度报名分布统计项 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDistributionItem {

    /**
     * 课程分类编码
     */
    private String category;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 报名人数
     */
    private Long enrollmentCount;
}
