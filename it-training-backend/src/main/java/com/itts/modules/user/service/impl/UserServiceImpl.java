package com.itts.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itts.enums.UserStatus;
import com.itts.enums.DeleteFlag;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.common.security.JwtTokenProvider;
import com.itts.modules.user.dto.PasswordResetRequest;
import com.itts.modules.user.dto.UserCreateRequest;
import com.itts.modules.user.dto.UserResponse;
import com.itts.modules.user.dto.UserUpdateRequest;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import com.itts.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public IPage<UserResponse> listUsers(int page, int size, String role, String keyword) {
        Page<SysUser> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeleted, DeleteFlag.NOT_DELETED);

        // 角色过滤
        if (StringUtils.hasText(role)) {
            wrapper.eq(SysUser::getRole, role);
        }

        // 关键词搜索（用户名、真实姓名、手机号）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getRealName, keyword)
                    .or()
                    .like(SysUser::getPhone, keyword)
            );
        }

        wrapper.orderByDesc(SysUser::getCreatedAt);

        IPage<SysUser> userPage = sysUserMapper.selectPage(pageParam, wrapper);

        // 转换为响应对象
        return userPage.convert(this::convertToResponse);
    }

    @Override
    public UserResponse getUserById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null || DeleteFlag.isDeleted(user.getDeleted())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return convertToResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponse createUser(UserCreateRequest request) {
        log.info("创建用户: {}", request.getUsername());

        // 检查用户名是否已存在
        SysUser existingUser = sysUserMapper.selectByUsername(request.getUsername());
        if (existingUser != null) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.ENABLED.getCode()); // 默认启用
        user.setDeleted(DeleteFlag.NOT_DELETED);

        sysUserMapper.insert(user);

        log.info("用户创建成功: {}, ID: {}", request.getUsername(), user.getId());
        return convertToResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.info("更新用户: {}", id);

        SysUser user = sysUserMapper.selectById(id);
        if (user == null || DeleteFlag.isDeleted(user.getDeleted())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 更新字段（只更新非空字段）
        if (StringUtils.hasText(request.getRealName())) {
            user.setRealName(request.getRealName());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }
        if (StringUtils.hasText(request.getRole())) {
            user.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        sysUserMapper.updateById(user);

        log.info("用户更新成功: {}", id);
        return convertToResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        log.info("删除用户: {}", id);

        SysUser user = sysUserMapper.selectById(id);
        if (user == null || DeleteFlag.isDeleted(user.getDeleted())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 软删除
        user.setDeleted(DeleteFlag.DELETED);
        sysUserMapper.updateById(user);

        log.info("用户删除成功: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id, PasswordResetRequest request) {
        log.info("重置用户密码: {}", id);

        SysUser user = sysUserMapper.selectById(id);
        if (user == null || DeleteFlag.isDeleted(user.getDeleted())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        sysUserMapper.updateById(user);

        // 使目标用户的Token失效，强制重新登录
        jwtTokenProvider.invalidateToken(user.getUsername());

        log.info("用户密码重置成功: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        log.info("更新用户状态: {}, status: {}", id, status);

        SysUser user = sysUserMapper.selectById(id);
        if (user == null || DeleteFlag.isDeleted(user.getDeleted())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        user.setStatus(status);
        sysUserMapper.updateById(user);

        log.info("用户状态更新成功: {}", id);
    }

    /**
     * 转换为响应对象
     */
    private UserResponse convertToResponse(SysUser user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
