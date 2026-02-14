package com.itts.modules.recommend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 推荐反馈请求 DTO
 */
@Data
public class RecommendFeedbackRequest {

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 反馈类型: USEFUL / NOT_USEFUL
     */
    @NotBlank(message = "反馈类型不能为空")
    @Pattern(regexp = "USEFUL|NOT_USEFUL", message = "反馈类型只能为 USEFUL 或 NOT_USEFUL")
    private String feedbackType;
}
