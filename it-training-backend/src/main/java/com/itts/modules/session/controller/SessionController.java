package com.itts.modules.session.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.common.response.R;
import com.itts.modules.session.dto.SessionCreateRequest;
import com.itts.modules.session.dto.SessionResponse;
import com.itts.modules.session.dto.SessionUpdateRequest;
import com.itts.modules.session.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 班期管理控制器
 */
@Tag(name = "班期管理", description = "班期CRUD和报名状态管理")
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @Operation(summary = "分页查询班期列表")
    @GetMapping
    public R<IPage<SessionResponse>> listSessions(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "课程ID过滤") @RequestParam(required = false) Long courseId,
            @Parameter(description = "讲师ID过滤") @RequestParam(required = false) Long instructorId,
            @Parameter(description = "状态过滤") @RequestParam(required = false) Integer status) {
        IPage<SessionResponse> result = sessionService.listSessions(page, size, courseId, instructorId, status);
        return R.ok(result);
    }

    @Operation(summary = "获取可报名的班期列表（学员端）")
    @GetMapping("/enrollable")
    public R<List<SessionResponse>> listEnrollableSessions(
            @Parameter(description = "课程ID过滤") @RequestParam(required = false) Long courseId) {
        List<SessionResponse> sessions = sessionService.listEnrollableSessions(courseId);
        return R.ok(sessions);
    }

    @Operation(summary = "根据ID获取班期")
    @GetMapping("/{id}")
    public R<SessionResponse> getSessionById(@PathVariable Long id) {
        SessionResponse session = sessionService.getSessionById(id);
        return R.ok(session);
    }

    @Operation(summary = "创建班期")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public R<SessionResponse> createSession(@Valid @RequestBody SessionCreateRequest request) {
        SessionResponse session = sessionService.createSession(request);
        return R.ok(session);
    }

    @Operation(summary = "更新班期")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public R<SessionResponse> updateSession(@PathVariable Long id, @Valid @RequestBody SessionUpdateRequest request) {
        SessionResponse session = sessionService.updateSession(id, request);
        return R.ok(session);
    }

    @Operation(summary = "删除班期")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return R.ok();
    }

    @Operation(summary = "开放报名")
    @PatchMapping("/{id}/open")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> openEnrollment(@PathVariable Long id) {
        sessionService.openEnrollment(id);
        return R.ok();
    }

    @Operation(summary = "关闭报名")
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> closeEnrollment(@PathVariable Long id) {
        sessionService.closeEnrollment(id);
        return R.ok();
    }

    @Operation(summary = "获取当前讲师的班期列表（讲师端）")
    @GetMapping("/my")
    public R<List<SessionResponse>> getMySessionsAsInstructor() {
        List<SessionResponse> sessions = sessionService.getMySessionsAsInstructor();
        return R.ok(sessions);
    }
}
