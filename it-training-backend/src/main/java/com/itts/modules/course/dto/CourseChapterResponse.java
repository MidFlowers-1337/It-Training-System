package com.itts.modules.course.dto;

import lombok.Data;

/**
 * 课程章节响应DTO
 */
@Data
public class CourseChapterResponse {

    /**
     * 章节ID
     */
    private Long id;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 章节标题
     */
    private String title;

    /**
     * 章节描述
     */
    private String description;

    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 视频时长（秒）
     */
    private Integer duration;

    /**
     * 章节顺序
     */
    private Integer sortOrder;

    /**
     * 是否免费试看
     */
    private Boolean isFree;

    /**
     * 是否已完成（学员视角）
     */
    private Boolean completed;
}
