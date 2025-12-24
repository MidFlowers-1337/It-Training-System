package com.itts.common;

import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;

/**
 * 服务层测试基类
 * 提供公共的 Mock 方法，消除重复代码
 */
public abstract class BaseServiceTest {

    @Mock
    protected SysUserMapper sysUserMapper;

    /**
     * SecurityContextHolder 的静态 Mock
     * 需要在测试方法内使用，并在 @AfterEach 中关闭
     */
    protected MockedStatic<SecurityContextHolder> securityContextMock;

    /**
     * 模拟当前登录用户
     * 调用此方法后，SecurityContextHolder.getContext() 会返回模拟的安全上下文
     *
     * @param username 用户名
     * @param user 用户对象（将由 sysUserMapper.selectByUsername 返回）
     */
    protected void mockCurrentUser(String username, SysUser user) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(username);

        securityContextMock = mockStatic(SecurityContextHolder.class);
        securityContextMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);

        if (user != null) {
            when(sysUserMapper.selectByUsername(username)).thenReturn(user);
        }
    }

    /**
     * 模拟未认证用户（匿名访问）
     */
    protected void mockAnonymousUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        securityContextMock = mockStatic(SecurityContextHolder.class);
        securityContextMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    /**
     * 创建测试用户
     *
     * @param id 用户ID
     * @param username 用户名
     * @param role 角色（ADMIN, INSTRUCTOR, STUDENT）
     * @return 测试用户对象
     */
    protected SysUser createTestUser(Long id, String username, String role) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setRealName("测试" + username);
        user.setRole(role);
        user.setStatus(1);
        return user;
    }

    /**
     * 创建学生用户
     */
    protected SysUser createStudentUser(Long id, String username) {
        return createTestUser(id, username, "STUDENT");
    }

    /**
     * 创建管理员用户
     */
    protected SysUser createAdminUser(Long id, String username) {
        return createTestUser(id, username, "ADMIN");
    }

    /**
     * 创建讲师用户
     */
    protected SysUser createInstructorUser(Long id, String username) {
        return createTestUser(id, username, "INSTRUCTOR");
    }

    /**
     * 关闭静态 Mock
     * 子类需要在 @AfterEach 中调用此方法或调用 super.tearDown()
     */
    @AfterEach
    protected void tearDown() {
        if (securityContextMock != null) {
            securityContextMock.close();
            securityContextMock = null;
        }
    }
}
