package com.itts.modules.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 课程评价请求 DTO
 */
@Data
public class CourseReviewRequest {

    /**
     * 评分(1-5)
     */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1")
    @Max(value = 5, message = "评分最高为5")
    private Integer rating;

    /**
     * 评价内容
     */
    @Size(max = 1000, message = "评价内容最长1000字")
    private String comment;
}
