package com.itts.modules.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.modules.user.dto.PasswordResetRequest;
import com.itts.modules.user.dto.UserCreateRequest;
import com.itts.modules.user.dto.UserResponse;
import com.itts.modules.user.dto.UserUpdateRequest;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 分页查询用户列表
     * @param page 页码
     * @param size 每页大小
     * @param role 角色过滤
     * @param keyword 搜索关键词
     * @return 用户分页列表
     */
    IPage<UserResponse> listUsers(int page, int size, String role, String keyword);

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户信息
     */
    UserResponse getUserById(Long id);

    /**
     * 创建用户
     * @param request 创建请求
     * @return 用户信息
     */
    UserResponse createUser(UserCreateRequest request);

    /**
     * 更新用户
     * @param id 用户ID
     * @param request 更新请求
     * @return 用户信息
     */
    UserResponse updateUser(Long id, UserUpdateRequest request);

    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 重置用户密码
     * @param id 用户ID
     * @param request 密码重置请求
     */
    void resetPassword(Long id, PasswordResetRequest request);

    /**
     * 启用/禁用用户
     * @param id 用户ID
     * @param status 状态（0-禁用，1-启用）
     */
    void updateStatus(Long id, Integer status);
}
