package com.itts.modules.learning.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.learning.dto.LearningProgressResponse;
import com.itts.modules.learning.dto.UpdateProgressRequest;
import com.itts.modules.learning.service.LearningProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "学习进度", description = "学习进度管理相关接口")
@RestController
@RequestMapping("/api/v1/learning")
@RequiredArgsConstructor
public class ProgressController {

    private final LearningProgressService learningProgressService;

    @Operation(summary = "分页获取所有课程学习进度")
    @GetMapping("/progress")
    public R<IPage<LearningProgressResponse>> getUserProgress(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningProgressService.getUserProgress(userId, page, size));
    }

    @Operation(summary = "获取指定课程学习进度")
    @GetMapping("/progress/course/{courseId}")
    public R<LearningProgressResponse> getCourseProgress(@PathVariable Long courseId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningProgressService.getCourseProgress(userId, courseId));
    }

    @Operation(summary = "更新学习进度")
    @PostMapping("/progress/update")
    public R<LearningProgressResponse> updateProgress(@Valid @RequestBody UpdateProgressRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningProgressService.updateProgress(userId, request));
    }

    @Operation(summary = "标记课程完成")
    @PostMapping("/progress/complete/{courseId}")
    public R<LearningProgressResponse> markCompleted(@PathVariable Long courseId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningProgressService.markCompleted(userId, courseId));
    }

    @Operation(summary = "分页获取进行中的课程")
    @GetMapping("/progress/in-progress")
    public R<IPage<LearningProgressResponse>> getInProgressCourses(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(learningProgressService.getInProgressCourses(userId, page, size));
    }
}
