package com.itts.modules.auth.service.impl;

import com.itts.enums.RoleEnum;
import com.itts.enums.UserStatus;
import com.itts.enums.DeleteFlag;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.common.security.JwtTokenProvider;
import com.itts.modules.auth.dto.LoginRequest;
import com.itts.modules.auth.dto.RegisterRequest;
import com.itts.modules.auth.dto.TokenResponse;
import com.itts.modules.auth.service.AuthService;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResponse login(LoginRequest request) {
        log.info("用户登录: {}", request.getUsername());

        // 验证用户名密码
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 设置认证信息到上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 查询用户信息
        SysUser user = sysUserMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 检查用户状态
        if (user.getStatus() != UserStatus.ENABLED.getCode()) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        // 生成Token
        String token = jwtTokenProvider.generateToken(authentication);

        log.info("用户登录成功: {}", request.getUsername());

        return buildTokenResponse(token, user);
    }

    @Override
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }

            // 使Token失效
            jwtTokenProvider.invalidateToken(username);

            // 清除安全上下文
            SecurityContextHolder.clearContext();

            log.info("用户登出成功: {}", username);
        }
    }

    @Override
    @Transactional
    public TokenResponse register(RegisterRequest request) {
        log.info("用户注册: {}", request.getUsername());

        // 验证两次密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        // 检查用户名是否已存在
        SysUser existingUser = sysUserMapper.selectByUsername(request.getUsername());
        if (existingUser != null) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        // 创建新用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(RoleEnum.STUDENT.name()); // 默认注册为学员
        user.setStatus(UserStatus.ENABLED.getCode()); // 默认启用
        user.setDeleted(DeleteFlag.NOT_DELETED);

        sysUserMapper.insert(user);

        log.info("用户注册成功: {}, ID: {}", request.getUsername(), user.getId());

        // 自动登录
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成Token
        String token = jwtTokenProvider.generateToken(authentication);

        return buildTokenResponse(token, user);
    }

    @Override
    public TokenResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return TokenResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .build();
    }

    /**
     * 构建Token响应
     */
    private TokenResponse buildTokenResponse(String token, SysUser user) {
        return TokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs() / 1000)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .build();
    }
}
