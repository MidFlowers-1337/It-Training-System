package com.itts.modules.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.modules.user.dto.PasswordResetRequest;
import com.itts.modules.user.dto.UserCreateRequest;
import com.itts.modules.user.dto.UserResponse;
import com.itts.modules.user.dto.UserUpdateRequest;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import com.itts.modules.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户管理服务测试")
class UserServiceTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private SysUser testUser;
    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        // 初始化测试用户
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encoded_password");
        testUser.setRealName("测试用户");
        testUser.setPhone("13800138000");
        testUser.setRole("STUDENT");
        testUser.setStatus(1);
        testUser.setDeleted(0);

        // 初始化创建请求
        createRequest = new UserCreateRequest();
        createRequest.setUsername("newuser");
        createRequest.setPassword("password123");
        createRequest.setRealName("新用户");
        createRequest.setPhone("13900139000");
        createRequest.setRole("STUDENT");

        // 初始化更新请求
        updateRequest = new UserUpdateRequest();
        updateRequest.setRealName("更新后的名字");
        updateRequest.setPhone("13800138888");
    }

    @Test
    @DisplayName("查询用户列表成功")
    void listUsers_Success() {
        // 准备模拟数据
        Page<SysUser> page = new Page<>(1, 10);
        page.setRecords(java.util.List.of(testUser));
        page.setTotal(1);

        when(sysUserMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        // 执行测试
        IPage<UserResponse> result = userService.listUsers(1, 10, null, null);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(testUser.getUsername(), result.getRecords().get(0).getUsername());

        verify(sysUserMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("根据ID查询用户成功")
    void getUserById_Success() {
        // 准备模拟数据
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);

        // 执行测试
        UserResponse result = userService.getUserById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getRealName(), result.getRealName());

        verify(sysUserMapper).selectById(1L);
    }

    @Test
    @DisplayName("根据ID查询用户失败 - 用户不存在")
    void getUserById_NotFound() {
        // 准备模拟数据
        when(sysUserMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.getUserById(999L));
        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("根据ID查询用户失败 - 用户已删除")
    void getUserById_Deleted() {
        // 准备模拟数据
        testUser.setDeleted(1);
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.getUserById(1L));
        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("创建用户成功")
    void createUser_Success() {
        // 准备模拟数据
        when(sysUserMapper.selectByUsername("newuser")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded_new_password");
        when(sysUserMapper.insert(any(SysUser.class))).thenAnswer(invocation -> {
            SysUser user = invocation.getArgument(0);
            user.setId(2L);
            return 1;
        });

        // 执行测试
        UserResponse result = userService.createUser(createRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("新用户", result.getRealName());

        verify(sysUserMapper).selectByUsername("newuser");
        verify(passwordEncoder).encode("password123");
        verify(sysUserMapper).insert(any(SysUser.class));
    }

    @Test
    @DisplayName("创建用户失败 - 用户名已存在")
    void createUser_UsernameExists() {
        // 准备模拟数据
        when(sysUserMapper.selectByUsername("newuser")).thenReturn(testUser);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.createUser(createRequest));
        assertEquals(ErrorCode.USERNAME_EXISTS.getCode(), exception.getCode());

        verify(sysUserMapper, never()).insert(any(SysUser.class));
    }

    @Test
    @DisplayName("更新用户成功")
    void updateUser_Success() {
        // 准备模拟数据
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);
        when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

        // 执行测试
        UserResponse result = userService.updateUser(1L, updateRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals("更新后的名字", result.getRealName());
        assertEquals("13800138888", result.getPhone());

        verify(sysUserMapper).selectById(1L);
        verify(sysUserMapper).updateById(any(SysUser.class));
    }

    @Test
    @DisplayName("更新用户失败 - 用户不存在")
    void updateUser_NotFound() {
        // 准备模拟数据
        when(sysUserMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateUser(999L, updateRequest));
        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());

        verify(sysUserMapper, never()).updateById(any(SysUser.class));
    }

    @Test
    @DisplayName("删除用户成功")
    void deleteUser_Success() {
        // 准备模拟数据
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);
        when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

        // 执行测试
        userService.deleteUser(1L);

        // 验证调用
        verify(sysUserMapper).selectById(1L);
        verify(sysUserMapper).updateById(argThat(user -> user.getDeleted() == 1));
    }

    @Test
    @DisplayName("删除用户失败 - 用户不存在")
    void deleteUser_NotFound() {
        // 准备模拟数据
        when(sysUserMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.deleteUser(999L));
        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("重置密码成功")
    void resetPassword_Success() {
        // 准备模拟数据
        PasswordResetRequest request = new PasswordResetRequest();
        request.setNewPassword("newpassword123");

        when(sysUserMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.encode("newpassword123")).thenReturn("$2a$10$new_encoded");
        when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

        // 执行测试
        userService.resetPassword(1L, request);

        // 验证调用
        verify(sysUserMapper).selectById(1L);
        verify(passwordEncoder).encode("newpassword123");
        verify(sysUserMapper).updateById(any(SysUser.class));
    }

    @Test
    @DisplayName("更新用户状态成功")
    void updateStatus_Success() {
        // 准备模拟数据
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);
        when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

        // 执行测试
        userService.updateStatus(1L, 0);

        // 验证调用
        verify(sysUserMapper).selectById(1L);
        verify(sysUserMapper).updateById(argThat(user -> user.getStatus() == 0));
    }
}
