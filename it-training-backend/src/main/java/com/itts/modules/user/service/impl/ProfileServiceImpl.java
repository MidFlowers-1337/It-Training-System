package com.itts.modules.user.service.impl;

import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.modules.user.dto.ChangePasswordRequest;
import com.itts.modules.user.dto.ProfileUpdateRequest;
import com.itts.modules.user.dto.UserResponse;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import com.itts.modules.user.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 个人中心服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getCurrentUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return convertToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 更新字段
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        // bio 字段需要在 SysUser 实体中添加

        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户 {} 更新了个人资料", userId);
        return convertToResponse(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        // 验证两次密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "两次输入的密码不一致");
        }

        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 验证当前密码
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "当前密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户 {} 修改了密码", userId);
    }

    @Override
    @Transactional
    public String updateAvatar(Long userId, String avatarUrl) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        user.setAvatar(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户 {} 更新了头像", userId);
        return avatarUrl;
    }

    @Override
    public Map<String, Object> getSecurityInfo(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Map<String, Object> securityInfo = new HashMap<>();
        securityInfo.put("hasPassword", true);
        securityInfo.put("emailBound", user.getEmail() != null && !user.getEmail().isEmpty());
        securityInfo.put("email", maskEmail(user.getEmail()));
        securityInfo.put("phoneBound", user.getPhone() != null && !user.getPhone().isEmpty());
        securityInfo.put("phone", maskPhone(user.getPhone()));
        securityInfo.put("lastLoginTime", user.getUpdatedAt());
        securityInfo.put("accountCreatedAt", user.getCreatedAt());

        return securityInfo;
    }

    @Override
    @Transactional
    public void bindEmail(Long userId, String email, String code) {
        // 简化实现：实际应该验证验证码
        // TODO: 实现验证码验证逻辑
        
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        user.setEmail(email);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户 {} 绑定了邮箱 {}", userId, maskEmail(email));
    }

    @Override
    @Transactional
    public void bindPhone(Long userId, String phone, String code) {
        // 简化实现：实际应该验证验证码
        // TODO: 实现验证码验证逻辑
        
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        user.setPhone(phone);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户 {} 绑定了手机 {}", userId, maskPhone(phone));
    }

    @Override
    public void sendEmailCode(String email) {
        // 简化实现：实际应该发送邮件
        // TODO: 实现邮件发送逻辑
        log.info("发送邮箱验证码到 {}", maskEmail(email));
    }

    @Override
    public void sendPhoneCode(String phone) {
        // 简化实现：实际应该发送短信
        // TODO: 实现短信发送逻辑
        log.info("发送手机验证码到 {}", maskPhone(phone));
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId, String password) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "密码错误");
        }

        // 软删除：设置状态为禁用
        user.setStatus(0);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户 {} 注销了账号", userId);
    }

    /**
     * 转换为响应对象
     */
    private UserResponse convertToResponse(SysUser user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    /**
     * 脱敏邮箱
     */
    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        return email.substring(0, 1) + "***" + email.substring(atIndex);
    }

    /**
     * 脱敏手机号
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}