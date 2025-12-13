package com.itts.modules.auth.service;

import com.itts.modules.auth.dto.LoginRequest;
import com.itts.modules.auth.dto.RegisterRequest;
import com.itts.modules.auth.dto.TokenResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     * @param request 登录请求
     * @return Token响应
     */
    TokenResponse login(LoginRequest request);

    /**
     * 用户注册
     * @param request 注册请求
     * @return Token响应
     */
    TokenResponse register(RegisterRequest request);

    /**
     * 获取当前登录用户信息
     * @return Token响应（包含用户信息）
     */
    TokenResponse getCurrentUser();
}
