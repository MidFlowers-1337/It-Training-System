package com.itts.modules.user.controller;

import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.user.dto.ChangePasswordRequest;
import com.itts.modules.user.dto.ProfileUpdateRequest;
import com.itts.modules.user.dto.UserResponse;
import com.itts.modules.user.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public R<String> uploadAvatar(@RequestParam String avatarUrl) {
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
    public R<Void> bindEmail(@RequestParam String email, @RequestParam String code) {
        Long userId = SecurityUtils.getCurrentUserId();
        profileService.bindEmail(userId, email, code);
        return R.ok();
    }

    @Operation(summary = "绑定手机")
    @PostMapping("/bind-phone")
    public R<Void> bindPhone(@RequestParam String phone, @RequestParam String code) {
        Long userId = SecurityUtils.getCurrentUserId();
        profileService.bindPhone(userId, phone, code);
        return R.ok();
    }

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/send-email-code")
    public R<Void> sendEmailCode(@RequestParam String email) {
        profileService.sendEmailCode(email);
        return R.ok();
    }

    @Operation(summary = "发送手机验证码")
    @PostMapping("/send-phone-code")
    public R<Void> sendPhoneCode(@RequestParam String phone) {
        profileService.sendPhoneCode(phone);
        return R.ok();
    }

    @Operation(summary = "注销账号")
    @DeleteMapping
    public R<Void> deleteAccount(@RequestParam String password) {
        Long userId = SecurityUtils.getCurrentUserId();
        profileService.deleteAccount(userId, password);
        return R.ok();
    }

    @Operation(summary = "清除学习数据")
    @PostMapping("/clear-data")
    public R<Void> clearLearningData(@RequestParam String password) {
        Long userId = SecurityUtils.getCurrentUserId();
        profileService.clearLearningData(userId, password);
        return R.ok();
    }
}