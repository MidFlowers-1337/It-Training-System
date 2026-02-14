package com.itts.modules.user.controller;

import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.user.dto.ChangePasswordRequest;
import com.itts.modules.user.dto.PasswordConfirmRequest;
import com.itts.modules.user.dto.ProfileUpdateRequest;
import com.itts.modules.user.dto.UserResponse;
import com.itts.modules.user.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 个人中心控制器
 */
@Tag(name = "个人中心", description = "用户个人资料和账号设置")
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Validated
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "获取当前用户信息")
    @GetMapping
    public R<UserResponse> getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(profileService.getCurrentUser(userId));
    }

    @Operation(summary = "更新个人资料")
    @PutMapping
    public R<UserResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(profileService.updateProfile(userId, request));
    }

    @Operation(summary = "修改密码")
    @PostMapping("/password")
    public R<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        profileService.changePassword(userId, request);
        return R.ok();
    }

    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public R<String> uploadAvatar(@RequestParam @NotBlank(message = "头像URL不能为空") String avatarUrl) {
        Long userId = SecurityUtils.getCurrentUserId();
        String url = profileService.updateAvatar(userId, avatarUrl);
        return R.ok(url);
    }

    @Operation(summary = "获取账号安全信息")
    @GetMapping("/security")
    public R<Object> getSecurityInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(profileService.getSecurityInfo(userId));
    }

    @Operation(summary = "绑定邮箱")
    @PostMapping("/bind-email")
    public R<Void> bindEmail(
            @RequestParam @Email(message = "邮箱格式不正确") String email,
            @RequestParam @NotBlank(message = "验证码不能为空") String code) {
        Long userId = SecurityUtils.getCurrentUserId();
        profileService.bindEmail(userId, email, code);
        return R.ok();
    }

    @Operation(summary = "绑定手机")
    @PostMapping("/bind-phone")
    public R<Void> bindPhone(
            @RequestParam @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone,
            @RequestParam @NotBlank(message = "验证码不能为空") String code) {
        Long userId = SecurityUtils.getCurrentUserId();
        profileService.bindPhone(userId, phone, code);
        return R.ok();
    }

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/send-email-code")
    public R<Void> sendEmailCode(@RequestParam @Email(message = "邮箱格式不正确") String email) {
        profileService.sendEmailCode(email);
        return R.ok();
    }

    @Operation(summary = "发送手机验证码")
    @PostMapping("/send-phone-code")
    public R<Void> sendPhoneCode(
            @RequestParam @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone) {
        profileService.sendPhoneCode(phone);
        return R.ok();
    }

    @Operation(summary = "停用账号", description = "软删除：将账号状态设为禁用，非物理删除")
    @DeleteMapping
    public R<Void> disableAccount(@Valid @RequestBody PasswordConfirmRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        profileService.disableAccount(userId, request.getPassword());
        return R.ok();
    }

    @Operation(summary = "清除学习数据")
    @PostMapping("/clear-data")
    public R<Void> clearLearningData(@Valid @RequestBody PasswordConfirmRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        profileService.clearLearningData(userId, request.getPassword());
        return R.ok();
    }
}