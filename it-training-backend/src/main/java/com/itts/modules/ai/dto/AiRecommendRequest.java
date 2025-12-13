package com.itts.modules.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * AI推荐请求 DTO
 */
@Data
public class AiRecommendRequest {

    /**
     * 用户输入的学习目标/需求描述
     */
    @NotBlank(message = "请输入您的学习目标")
    @Size(min = 5, max = 500, message = "学习目标描述需在5-500字之间")
    private String learningGoal;
}
