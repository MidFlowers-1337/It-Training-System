package com.itts.common.util;

import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 安全工具类
 * <p>
 * 提供获取当前登录用户信息的公共方法
 */
@Component
public final class SecurityUtils {

    private static SysUserMapper userMapper;

    @Autowired
    public void setUserMapper(SysUserMapper userMapper) {
        SecurityUtils.userMapper = userMapper;
    }

    private SecurityUtils() {
        // 工具类禁止实例化
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名
     * @throws BusinessException 未登录时抛出 UNAUTHORIZED 异常
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        // 检查是否是匿名用户
        if ("anonymousUser".equals(principal.toString())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        return principal.toString();
    }

    /**
     * 获取当前登录用户名（可选）
     * <p>
     * 适用于允许匿名访问的场景
     *
     * @return Optional 包装的用户名，未登录时返回 empty
     */
    public static Optional<String> getCurrentUsernameOptional() {
        try {
            return Optional.of(getCurrentUsername());
        } catch (BusinessException e) {
            return Optional.empty();
        }
    }

    /**
     * 检查当前用户是否已认证
     *
     * @return true 如果已认证，false 如果未认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        return !"anonymousUser".equals(principal.toString());
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     * @throws BusinessException 未登录或用户不存在时抛出异常
     */
    public static Long getCurrentUserId() {
        String username = getCurrentUsername();
        if (userMapper == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "SecurityUtils 未正确初始化");
        }
        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user.getId();
    }

    /**
     * 获取当前登录用户ID（可选）
     * <p>
     * 适用于允许匿名访问的场景
     *
     * @return Optional 包装的用户ID，未登录时返回 empty
     */
    public static Optional<Long> getCurrentUserIdOptional() {
        try {
            return Optional.of(getCurrentUserId());
        } catch (BusinessException e) {
            return Optional.empty();
        }
    }
}
