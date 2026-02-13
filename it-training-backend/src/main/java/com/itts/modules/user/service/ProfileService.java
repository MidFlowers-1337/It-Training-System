package com.itts.modules.user.service;

import com.itts.modules.user.dto.ChangePasswordRequest;
import com.itts.modules.user.dto.ProfileUpdateRequest;
import com.itts.modules.user.dto.UserResponse;

import java.util.Map;

/**
 * 个人中心服务接口
 */
public interface ProfileService {

    /**
     * 获取当前用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserResponse getCurrentUser(Long userId);

    /**
     * 更新个人资料
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    UserResponse updateProfile(Long userId, ProfileUpdateRequest request);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param request 修改密码请求
     */
    void changePassword(Long userId, ChangePasswordRequest request);

    /**
     * 更新头像
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 头像URL
     */
    String updateAvatar(Long userId, String avatarUrl);

    /**
     * 获取账号安全信息
     * @param userId 用户ID
     * @return 安全信息
     */
    Map<String, Object> getSecurityInfo(Long userId);

    /**
     * 绑定邮箱
     * @param userId 用户ID
     * @param email 邮箱
     * @param code 验证码
     */
    void bindEmail(Long userId, String email, String code);

    /**
     * 绑定手机
     * @param userId 用户ID
     * @param phone 手机号
     * @param code 验证码
     */
    void bindPhone(Long userId, String phone, String code);

    /**
     * 发送邮箱验证码
     * @param email 邮箱
     */
    void sendEmailCode(String email);

    /**
     * 发送手机验证码
     * @param phone 手机号
     */
    void sendPhoneCode(String phone);

    /**
     * 停用账号（软删除）
     * @param userId 用户ID
     * @param password 密码确认
     */
    void disableAccount(Long userId, String password);

    /**
     * 清除学习数据
     * @param userId 用户ID
     * @param password 密码确认
     */
    void clearLearningData(Long userId, String password);
}