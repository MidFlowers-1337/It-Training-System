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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

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
    private final StringRedisTemplate stringRedisTemplate;

    /** Redis key 前缀：登录失败计数 */
    private static final String LOGIN_FAIL_KEY_PREFIX = "login:fail:";
    /** 最大连续失败次数 */
    private static final int MAX_FAIL_COUNT = 5;
    /** 锁定时间（分钟） */
    private static final int LOCK_MINUTES = 15;

    @Override
    public TokenResponse login(LoginRequest request) {
        log.info("用户登录: {}", request.getUsername());

        // 检查账号是否被锁定
        String failKey = LOGIN_FAIL_KEY_PREFIX + request.getUsername();
        String failCountStr = stringRedisTemplate.opsForValue().get(failKey);
        int failCount = failCountStr != null ? Integer.parseInt(failCountStr) : 0;
        if (failCount >= MAX_FAIL_COUNT) {
            log.warn("账号已锁定，拒绝登录: {}", request.getUsername());
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        // 验证用户名密码
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            // 登录失败，递增失败计数
            long newCount = stringRedisTemplate.opsForValue().increment(failKey);
            if (newCount == 1) {
                stringRedisTemplate.expire(failKey, LOCK_MINUTES, TimeUnit.MINUTES);
            }
            log.warn("登录失败 ({}/{}): {}", newCount, MAX_FAIL_COUNT, request.getUsername());
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        // 登录成功，清除失败计数
        stringRedisTemplate.delete(failKey);

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

        // 生成 Access Token + Refresh Token（双 Token 架构）
        String token = jwtTokenProvider.generateToken(authentication, user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername(), user.getId());

        log.info("用户登录成功: {}", request.getUsername());

        return buildTokenResponse(token, refreshToken, user);
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
    @Transactional(rollbackFor = Exception.class)
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

        // 生成 Access Token + Refresh Token（双 Token 架构）
        String token = jwtTokenProvider.generateToken(authentication, user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername(), user.getId());

        return buildTokenResponse(token, refreshToken, user);
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        log.info("刷新Token");

        // 验证 Refresh Token 并获取新 Access Token
        String newAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken);
        if (newAccessToken == null) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Refresh Token无效或已过期");
        }

        // 从 refresh token 解析用户信息
        io.jsonwebtoken.Claims claims = jwtTokenProvider.validateRefreshToken(refreshToken);
        if (claims == null) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }

        String username = claims.getSubject();
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        log.info("Token刷新成功: {}", username);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // 复用原 Refresh Token
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs() / 1000)
                .refreshExpiresIn(jwtTokenProvider.getRefreshExpirationMs() / 1000)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .build();
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
     * 构建Token响应（双 Token 架构）
     */
    private TokenResponse buildTokenResponse(String token, String refreshToken, SysUser user) {
        return TokenResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs() / 1000)
                .refreshExpiresIn(jwtTokenProvider.getRefreshExpirationMs() / 1000)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .build();
    }
}
