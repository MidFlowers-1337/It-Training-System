package com.itts.modules.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 课程章节创建请求 DTO
 */
@Data
public class CourseChapterCreateRequest {

    /**
     * 章节标题
     */
    @NotBlank(message = "章节标题不能为空")
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
     * 章节顺序（如不指定则自动追加到末尾）
     */
    @Min(value = 1, message = "章节顺序最小为1")
    private Integer sortOrder;

    /**
     * 是否免费试看: true-是, false-否
     */
    private Boolean isFree;
}
