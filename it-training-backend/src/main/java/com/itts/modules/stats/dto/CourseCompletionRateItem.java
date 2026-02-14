package com.itts.modules.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程完课率统计项 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCompletionRateItem {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 总学习人数
     */
    private Long totalLearners;

    /**
     * 已完成人数
     */
    private Long completedLearners;

    /**
     * 完课率（百分比，保留1位小数）
     */
    private Double completionRate;
}
