package com.itts.modules.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 课程章节更新请求 DTO
 */
@Data
public class CourseChapterUpdateRequest {

    /**
     * 章节标题
     */
    @Size(max = 200, message = "章节标题不能超过200个字符")
    private String title;

    /**
     * 章节描述
     */
    @Size(max = 2000, message = "章节描述不能超过2000个字符")
    private String description;

    /**
     * 视频URL
     */
    @Size(max = 500, message = "视频URL不能超过500个字符")
    private String videoUrl;

    /**
     * 视频时长（秒）
     */
    @Min(value = 0, message = "视频时长不能为负数")
    private Integer duration;

    /**
     * 是否免费试看: true-是, false-否
     */
    private Boolean isFree;

    /**
     * 状态: 0-未发布, 1-已发布
     */
    private Integer status;
}
