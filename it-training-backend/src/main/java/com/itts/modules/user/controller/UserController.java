package com.itts.modules.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.common.response.R;
import com.itts.modules.user.dto.PasswordResetRequest;
import com.itts.modules.user.dto.UserCreateRequest;
import com.itts.modules.user.dto.UserResponse;
import com.itts.modules.user.dto.UserUpdateRequest;
import com.itts.modules.user.service.UserService;
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

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理", description = "用户CRUD操作（仅管理员）")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping
    public R<IPage<UserResponse>> listUsers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "角色过滤") @RequestParam(required = false) String role,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        IPage<UserResponse> result = userService.listUsers(page, size, role, keyword);
        return R.ok(result);
    }

    @Operation(summary = "根据ID获取用户")
    @GetMapping("/{id}")
    public R<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return R.ok(user);
    }

    @Operation(summary = "创建用户")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public R<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse user = userService.createUser(request);
        return R.ok(user);
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public R<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return R.ok(user);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return R.ok();
    }

    @Operation(summary = "重置用户密码")
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody PasswordResetRequest request) {
        userService.resetPassword(id, request);
        return R.ok();
    }

    @Operation(summary = "更新用户状态")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "状态: 0-禁用, 1-启用") @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return R.ok();
    }
}
