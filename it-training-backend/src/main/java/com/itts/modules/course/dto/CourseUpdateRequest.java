package com.itts.modules.course.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 课程更新请求 DTO
 */
@Data
public class CourseUpdateRequest {

    /**
     * 课程名称
     */
    @Size(max = 100, message = "课程名称不能超过100个字符")
    private String name;

    /**
     * 课程分类: BACKEND, FRONTEND, DATABASE, CLOUD, AI, OTHER
     */
    private String category;

    /**
     * 课程描述
     */
    @Size(max = 2000, message = "课程描述不能超过2000个字符")
    private String description;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 难度等级: 1-入门, 2-初级, 3-中级, 4-高级
     */
    @Min(value = 1, message = "难度等级最小为1")
    @Max(value = 4, message = "难度等级最大为4")
    private Integer difficulty;

    /**
     * 课时数
     */
    @Min(value = 1, message = "课时数最小为1")
    private Integer durationHours;

    /**
     * 技能标签（逗号分隔）
     */
    @Size(max = 500, message = "技能标签不能超过500个字符")
    private String tags;
}
