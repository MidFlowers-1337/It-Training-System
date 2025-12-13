package com.itts.modules.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程热度项 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseHotItem {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程分类
     */
    private String category;

    /**
     * 报名人数
     */
    private Long enrollmentCount;
}
