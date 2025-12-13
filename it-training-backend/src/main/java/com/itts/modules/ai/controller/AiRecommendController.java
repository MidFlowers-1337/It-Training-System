package com.itts.modules.ai.controller;

import com.itts.common.response.R;
import com.itts.modules.ai.dto.AiRecommendRequest;
import com.itts.modules.ai.dto.AiRecommendResponse;
import com.itts.modules.ai.service.AiRecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI推荐控制器
 */
@Tag(name = "AI智能推荐", description = "基于用户学习目标的智能课程推荐")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiRecommendController {

    private final AiRecommendService aiRecommendService;

    @Operation(summary = "获取课程推荐", description = "根据用户输入的学习目标，智能推荐适合的课程")
    @PostMapping("/recommend")
    public R<AiRecommendResponse> getRecommendation(@Valid @RequestBody AiRecommendRequest request) {
        AiRecommendResponse response = aiRecommendService.getRecommendation(request.getLearningGoal());
        return R.ok(response);
    }
}
