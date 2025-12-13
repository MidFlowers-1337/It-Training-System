package com.itts.modules.enrollment.service;

import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.enums.EnrollmentStatus;
import com.itts.enums.SessionStatus;
import com.itts.modules.enrollment.dto.EnrollmentResponse;
import com.itts.modules.enrollment.entity.Enrollment;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import com.itts.modules.enrollment.service.impl.EnrollmentServiceImpl;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 报名服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("报名服务测试")
class EnrollmentServiceTest {

    @Mock
    private EnrollmentMapper enrollmentMapper;

    @Mock
    private ClassSessionMapper classSessionMapper;

    @Mock
    private SysUserMapper sysUserMapper;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private SysUser testUser;
    private ClassSession testSession;
    private Enrollment testEnrollment;

    @BeforeEach
    void setUp() {
        // 初始化测试用户
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("student1");
        testUser.setRealName("王小明");
        testUser.setRole("STUDENT");

        // 初始化测试班期
        testSession = new ClassSession();
        testSession.setId(1L);
        testSession.setCourseId(1L);
        testSession.setInstructorId(2L);
        testSession.setSessionCode("JAVA-001-2025S1");
        testSession.setStartDate(LocalDate.of(2025, 1, 15));
        testSession.setEndDate(LocalDate.of(2025, 3, 15));
        testSession.setMaxCapacity(30);
        testSession.setCurrentEnrollment(10);
        testSession.setStatus(SessionStatus.ENROLLING.getCode()); // 报名中
        testSession.setDeleted(0);

        // 初始化测试报名记录
        testEnrollment = new Enrollment();
        testEnrollment.setId(1L);
        testEnrollment.setUserId(1L);
        testEnrollment.setSessionId(1L);
        testEnrollment.setStatus(EnrollmentStatus.ENROLLED.getCode());
    }

    /**
     * 模拟当前登录用户
     */
    private void mockCurrentUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
        }
    }

    @Test
    @DisplayName("报名成功")
    void enroll_Success() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // 准备模拟数据
            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
            when(classSessionMapper.selectById(1L)).thenReturn(testSession);
            when(enrollmentMapper.countByUserAndSession(1L, 1L)).thenReturn(0);
            when(classSessionMapper.incrementEnrollment(1L)).thenReturn(1);
            when(enrollmentMapper.insert(any(Enrollment.class))).thenAnswer(invocation -> {
                Enrollment e = invocation.getArgument(0);
                e.setId(1L);
                return 1;
            });

            // 执行测试
            EnrollmentResponse result = enrollmentService.enroll(1L);

            // 验证结果
            assertNotNull(result);
            assertEquals(EnrollmentStatus.ENROLLED.getCode(), result.getStatus());

            // 验证调用
            verify(classSessionMapper).selectById(1L);
            verify(enrollmentMapper).countByUserAndSession(1L, 1L);
            verify(classSessionMapper).incrementEnrollment(1L);
            verify(enrollmentMapper).insert(any(Enrollment.class));
        }
    }

    @Test
    @DisplayName("报名失败 - 班期不存在")
    void enroll_SessionNotFound() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
            when(classSessionMapper.selectById(999L)).thenReturn(null);

            // 执行测试并验证异常
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> enrollmentService.enroll(999L));
            assertEquals(ErrorCode.SESSION_NOT_FOUND.getCode(), exception.getCode());
        }
    }

    @Test
    @DisplayName("报名失败 - 班期未开放报名")
    void enroll_SessionNotEnrollable() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // 设置班期为未开放状态
            testSession.setStatus(SessionStatus.NOT_OPEN.getCode());

            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
            when(classSessionMapper.selectById(1L)).thenReturn(testSession);

            // 执行测试并验证异常
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> enrollmentService.enroll(1L));
            assertEquals(ErrorCode.SESSION_NOT_ENROLLABLE.getCode(), exception.getCode());
        }
    }

    @Test
    @DisplayName("报名失败 - 重复报名")
    void enroll_Duplicate() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
            when(classSessionMapper.selectById(1L)).thenReturn(testSession);
            when(enrollmentMapper.countByUserAndSession(1L, 1L)).thenReturn(1); // 已有报名记录

            // 执行测试并验证异常
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> enrollmentService.enroll(1L));
            assertEquals(ErrorCode.ENROLLMENT_DUPLICATE.getCode(), exception.getCode());
        }
    }

    @Test
    @DisplayName("报名失败 - 名额已满")
    void enroll_QuotaFull() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
            when(classSessionMapper.selectById(1L)).thenReturn(testSession);
            when(enrollmentMapper.countByUserAndSession(1L, 1L)).thenReturn(0);
            when(classSessionMapper.incrementEnrollment(1L)).thenReturn(0); // 名额增加失败

            // 执行测试并验证异常
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> enrollmentService.enroll(1L));
            assertEquals(ErrorCode.ENROLLMENT_QUOTA_FULL.getCode(), exception.getCode());
        }
    }

    @Test
    @DisplayName("取消报名成功")
    void cancelEnrollment_Success() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
            when(enrollmentMapper.selectById(1L)).thenReturn(testEnrollment);
            when(classSessionMapper.selectById(1L)).thenReturn(testSession);
            when(enrollmentMapper.updateById(any(Enrollment.class))).thenReturn(1);
            when(classSessionMapper.decrementEnrollment(1L)).thenReturn(1);

            // 执行测试
            enrollmentService.cancelEnrollment(1L, "个人原因");

            // 验证调用
            verify(enrollmentMapper).selectById(1L);
            verify(enrollmentMapper).updateById(argThat(e ->
                e.getStatus() == EnrollmentStatus.CANCELED.getCode() &&
                "个人原因".equals(e.getCancelReason())
            ));
            verify(classSessionMapper).decrementEnrollment(1L);
        }
    }

    @Test
    @DisplayName("取消报名失败 - 报名记录不存在")
    void cancelEnrollment_NotFound() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
            when(enrollmentMapper.selectById(999L)).thenReturn(null);

            // 执行测试并验证异常
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> enrollmentService.cancelEnrollment(999L, "个人原因"));
            assertEquals(ErrorCode.ENROLLMENT_NOT_FOUND.getCode(), exception.getCode());
        }
    }

    @Test
    @DisplayName("取消报名失败 - 非本人报名记录")
    void cancelEnrollment_Forbidden() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // 设置报名记录属于其他用户
            testEnrollment.setUserId(999L);

            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
            when(enrollmentMapper.selectById(1L)).thenReturn(testEnrollment);

            // 执行测试并验证异常
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> enrollmentService.cancelEnrollment(1L, "个人原因"));
            assertEquals(ErrorCode.FORBIDDEN.getCode(), exception.getCode());
        }
    }

    @Test
    @DisplayName("取消报名失败 - 班期已开始")
    void cancelEnrollment_SessionInProgress() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // 设置班期为进行中
            testSession.setStatus(SessionStatus.IN_PROGRESS.getCode());

            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
            when(enrollmentMapper.selectById(1L)).thenReturn(testEnrollment);
            when(classSessionMapper.selectById(1L)).thenReturn(testSession);

            // 执行测试并验证异常
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> enrollmentService.cancelEnrollment(1L, "个人原因"));
            assertEquals(ErrorCode.ENROLLMENT_CANNOT_CANCEL.getCode(), exception.getCode());
        }
    }
}
