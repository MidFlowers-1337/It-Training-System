package com.itts.modules.enrollment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.common.response.R;
import com.itts.modules.enrollment.dto.CancelEnrollmentRequest;
import com.itts.modules.enrollment.dto.EnrollRequest;
import com.itts.modules.enrollment.dto.EnrollmentResponse;
import com.itts.modules.enrollment.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 报名管理控制器
 */
@Tag(name = "报名管理", description = "学员报名、取消报名、报名列表查询")
@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "学员报名")
    @PostMapping
    public R<EnrollmentResponse> enroll(@Valid @RequestBody EnrollRequest request) {
        EnrollmentResponse enrollment = enrollmentService.enroll(request.getSessionId());
        return R.ok(enrollment);
    }

    @Operation(summary = "取消报名")
    @PatchMapping("/{id}/cancel")
    public R<Void> cancelEnrollment(
            @PathVariable Long id,
            @RequestBody(required = false) CancelEnrollmentRequest request) {
        String reason = request != null ? request.getReason() : null;
        enrollmentService.cancelEnrollment(id, reason);
        return R.ok();
    }

    @Operation(summary = "获取我的报名列表")
    @GetMapping("/my")
    public R<List<EnrollmentResponse>> getMyEnrollments() {
        List<EnrollmentResponse> enrollments = enrollmentService.getMyEnrollments();
        return R.ok(enrollments);
    }

    @Operation(summary = "分页查询报名列表（管理端）")
    @GetMapping
    public R<IPage<EnrollmentResponse>> listEnrollments(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "用户ID过滤") @RequestParam(required = false) Long userId,
            @Parameter(description = "班期ID过滤") @RequestParam(required = false) Long sessionId,
            @Parameter(description = "状态过滤") @RequestParam(required = false) Integer status) {
        IPage<EnrollmentResponse> result = enrollmentService.listEnrollments(page, size, userId, sessionId, status);
        return R.ok(result);
    }

    @Operation(summary = "根据ID获取报名详情")
    @GetMapping("/{id}")
    public R<EnrollmentResponse> getEnrollmentById(@PathVariable Long id) {
        EnrollmentResponse enrollment = enrollmentService.getEnrollmentById(id);
        return R.ok(enrollment);
    }
}
