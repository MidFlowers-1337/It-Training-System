package com.itts.modules.course.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程响应 DTO
 */
@Data
public class CourseResponse {

    /**
     * 课程ID
     */
    private Long id;

    /**
     * 课程编码
     */
    private String code;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 课程分类
     */
    private String category;

    /**
     * 课程分类名称
     */
    private String categoryName;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 难度等级: 1-入门, 2-初级, 3-中级, 4-高级
     */
    private Integer difficulty;

    /**
     * 难度名称
     */
    private String difficultyName;

    /**
     * 课时数
     */
    private Integer durationHours;

    /**
     * 技能标签
     */
    private String tags;

    /**
     * 状态: 0-草稿, 1-已发布
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
