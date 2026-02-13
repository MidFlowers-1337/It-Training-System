package com.itts.modules.plan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.plan.dto.LearningPlanRequest;
import com.itts.modules.plan.dto.LearningPlanResponse;
import com.itts.modules.plan.service.LearningPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "学习计划", description = "学习计划管理相关接口")
@RestController
@RequestMapping("/api/v1/learning")
@RequiredArgsConstructor
public class LearningPlanController {

    private final LearningPlanService learningPlanService;

    @Operation(summary = "创建学习计划")
    @PostMapping("/plans")
    public R<LearningPlanResponse> createPlan(@Valid @RequestBody LearningPlanRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.createPlan(userId, request));
    }

    @Operation(summary = "分页获取所有学习计划")
    @GetMapping("/plans")
    public R<IPage<LearningPlanResponse>> getUserPlans(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.getUserPlans(userId, page, size));
    }

    @Operation(summary = "获取当前进行中的学习计划")
    @GetMapping("/plans/active")
    public R<LearningPlanResponse> getActivePlan() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.getActivePlan(userId));
    }

    @Operation(summary = "获取学习计划详情")
    @GetMapping("/plans/{planId}")
    public R<LearningPlanResponse> getPlanDetail(@PathVariable Long planId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.getPlanDetail(userId, planId));
    }

    @Operation(summary = "更新学习计划")
    @PutMapping("/plans/{planId}")
    public R<LearningPlanResponse> updatePlan(
            @PathVariable Long planId,
            @Valid @RequestBody LearningPlanRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.updatePlan(userId, planId, request));
    }

    @Operation(summary = "暂停学习计划")
    @PostMapping("/plans/{planId}/pause")
    public R<LearningPlanResponse> pausePlan(@PathVariable Long planId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.pausePlan(userId, planId));
    }

    @Operation(summary = "恢复学习计划")
    @PostMapping("/plans/{planId}/resume")
    public R<LearningPlanResponse> resumePlan(@PathVariable Long planId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.resumePlan(userId, planId));
    }

    @Operation(summary = "取消学习计划")
    @PostMapping("/plans/{planId}/cancel")
    public R<LearningPlanResponse> cancelPlan(@PathVariable Long planId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.cancelPlan(userId, planId));
    }

    @Operation(summary = "完成学习计划")
    @PostMapping("/plans/{planId}/complete")
    public R<LearningPlanResponse> completePlan(@PathVariable Long planId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningPlanService.completePlan(userId, planId));
    }
}
