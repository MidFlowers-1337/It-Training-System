package com.itts.modules.session.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.enums.SessionStatus;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.session.dto.SessionCreateRequest;
import com.itts.modules.session.dto.SessionResponse;
import com.itts.modules.session.dto.SessionUpdateRequest;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import com.itts.modules.session.service.impl.SessionServiceImpl;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 班期服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("班期服务测试")
class SessionServiceTest {

    @Mock
    private ClassSessionMapper classSessionMapper;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private SysUserMapper sysUserMapper;

    @InjectMocks
    private SessionServiceImpl sessionService;

    private ClassSession testSession;
    private Course testCourse;
    private SysUser testInstructor;
    private SessionCreateRequest createRequest;
    private SessionUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        // 初始化测试课程
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setCode("JAVA-001");
        testCourse.setName("Java 基础入门");
        testCourse.setDeleted(0);

        // 初始化测试讲师
        testInstructor = new SysUser();
        testInstructor.setId(2L);
        testInstructor.setUsername("instructor1");
        testInstructor.setRealName("张讲师");
        testInstructor.setRole("INSTRUCTOR");
        testInstructor.setDeleted(0);

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
        testSession.setStatus(SessionStatus.ENROLLING.getCode());
        testSession.setDeleted(0);

        // 初始化创建请求
        createRequest = new SessionCreateRequest();
        createRequest.setCourseId(1L);
        createRequest.setInstructorId(2L);
        createRequest.setSessionCode("JAVA-001-2025S2");
        createRequest.setStartDate(LocalDate.of(2025, 4, 1));
        createRequest.setEndDate(LocalDate.of(2025, 6, 1));
        createRequest.setMaxCapacity(25);
        createRequest.setLocation("教室A");
        createRequest.setSchedule("每周一、三、五 19:00-21:00");

        // 初始化更新请求
        updateRequest = new SessionUpdateRequest();
        updateRequest.setMaxCapacity(35);
        updateRequest.setLocation("教室B");
    }

    @Test
    @DisplayName("根据ID查询班期成功")
    void getSessionById_Success() {
        // 准备模拟数据
        when(classSessionMapper.selectById(1L)).thenReturn(testSession);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);
        when(sysUserMapper.selectById(2L)).thenReturn(testInstructor);

        // 执行测试
        SessionResponse result = sessionService.getSessionById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(testSession.getSessionCode(), result.getSessionCode());
        assertEquals(testCourse.getName(), result.getCourseName());
        assertEquals(testInstructor.getRealName(), result.getInstructorName());
        assertEquals(20, result.getRemainingQuota()); // 30 - 10

        verify(classSessionMapper).selectById(1L);
    }

    @Test
    @DisplayName("根据ID查询班期失败 - 班期不存在")
    void getSessionById_NotFound() {
        // 准备模拟数据
        when(classSessionMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> sessionService.getSessionById(999L));
        assertEquals(ErrorCode.SESSION_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("根据ID查询班期失败 - 班期已删除")
    void getSessionById_Deleted() {
        // 准备模拟数据
        testSession.setDeleted(1);
        when(classSessionMapper.selectById(1L)).thenReturn(testSession);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> sessionService.getSessionById(1L));
        assertEquals(ErrorCode.SESSION_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("创建班期成功")
    void createSession_Success() {
        // 准备模拟数据
        when(classSessionMapper.countBySessionCode("JAVA-001-2025S2")).thenReturn(0);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);
        when(sysUserMapper.selectById(2L)).thenReturn(testInstructor);
        when(classSessionMapper.insert(any(ClassSession.class))).thenAnswer(invocation -> {
            ClassSession session = invocation.getArgument(0);
            session.setId(2L);
            return 1;
        });

        // 执行测试
        SessionResponse result = sessionService.createSession(createRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals("JAVA-001-2025S2", result.getSessionCode());
        assertEquals(testCourse.getName(), result.getCourseName());
        assertEquals(testInstructor.getRealName(), result.getInstructorName());

        verify(classSessionMapper).countBySessionCode("JAVA-001-2025S2");
        verify(courseMapper).selectById(1L);
        verify(sysUserMapper).selectById(2L);
        verify(classSessionMapper).insert(any(ClassSession.class));
    }

    @Test
    @DisplayName("创建班期失败 - 班期编码已存在")
    void createSession_CodeExists() {
        // 准备模拟数据
        when(classSessionMapper.countBySessionCode("JAVA-001-2025S2")).thenReturn(1);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> sessionService.createSession(createRequest));
        assertEquals(ErrorCode.SESSION_CODE_EXISTS.getCode(), exception.getCode());

        verify(classSessionMapper, never()).insert(any(ClassSession.class));
    }

    @Test
    @DisplayName("创建班期失败 - 课程不存在")
    void createSession_CourseNotFound() {
        // 准备模拟数据
        when(classSessionMapper.countBySessionCode("JAVA-001-2025S2")).thenReturn(0);
        when(courseMapper.selectById(1L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> sessionService.createSession(createRequest));
        assertEquals(ErrorCode.COURSE_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("创建班期失败 - 讲师不存在")
    void createSession_InstructorNotFound() {
        // 准备模拟数据
        when(classSessionMapper.countBySessionCode("JAVA-001-2025S2")).thenReturn(0);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);
        when(sysUserMapper.selectById(2L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> sessionService.createSession(createRequest));
        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("更新班期成功")
    void updateSession_Success() {
        // 准备模拟数据
        when(classSessionMapper.selectById(1L)).thenReturn(testSession);
        when(classSessionMapper.updateById(any(ClassSession.class))).thenReturn(1);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);
        when(sysUserMapper.selectById(2L)).thenReturn(testInstructor);

        // 执行测试
        SessionResponse result = sessionService.updateSession(1L, updateRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals(35, result.getMaxCapacity());
        assertEquals("教室B", result.getLocation());

        verify(classSessionMapper).selectById(1L);
        verify(classSessionMapper).updateById(any(ClassSession.class));
    }

    @Test
    @DisplayName("更新班期失败 - 班期不存在")
    void updateSession_NotFound() {
        // 准备模拟数据
        when(classSessionMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> sessionService.updateSession(999L, updateRequest));
        assertEquals(ErrorCode.SESSION_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("更新班期失败 - 最大名额小于当前报名人数")
    void updateSession_CapacityTooSmall() {
        // 准备模拟数据
        updateRequest.setMaxCapacity(5); // 小于当前报名人数 10
        when(classSessionMapper.selectById(1L)).thenReturn(testSession);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> sessionService.updateSession(1L, updateRequest));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("更新班期讲师")
    void updateSession_ChangeInstructor() {
        // 准备模拟数据
        SysUser newInstructor = new SysUser();
        newInstructor.setId(3L);
        newInstructor.setRealName("李讲师");
        newInstructor.setDeleted(0);

        updateRequest.setInstructorId(3L);

        when(classSessionMapper.selectById(1L)).thenReturn(testSession);
        when(sysUserMapper.selectById(3L)).thenReturn(newInstructor);
        when(classSessionMapper.updateById(any(ClassSession.class))).thenReturn(1);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);

        // 执行测试
        SessionResponse result = sessionService.updateSession(1L, updateRequest);

        // 验证结果
        assertNotNull(result);
        // selectById(3L) 可能被调用多次（更新验证 + 填充关联），使用 atLeastOnce
        verify(sysUserMapper, atLeastOnce()).selectById(3L);
    }

    @Test
    @DisplayName("删除班期成功")
    void deleteSession_Success() {
        // 准备模拟数据
        when(classSessionMapper.selectById(1L)).thenReturn(testSession);
        when(classSessionMapper.countEnrollmentsBySessionId(1L)).thenReturn(0);
        when(classSessionMapper.updateById(any(ClassSession.class))).thenReturn(1);

        // 执行测试
        sessionService.deleteSession(1L);

        // 验证调用
        verify(classSessionMapper).selectById(1L);
        verify(classSessionMapper).countEnrollmentsBySessionId(1L);
        verify(classSessionMapper).updateById(argThat(session -> session.getDeleted() == 1));
    }

    @Test
    @DisplayName("删除班期失败 - 班期不存在")
    void deleteSession_NotFound() {
        // 准备模拟数据
        when(classSessionMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> sessionService.deleteSession(999L));
        assertEquals(ErrorCode.SESSION_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("删除班期失败 - 存在报名记录")
    void deleteSession_HasEnrollment() {
        // 准备模拟数据
        when(classSessionMapper.selectById(1L)).thenReturn(testSession);
        when(classSessionMapper.countEnrollmentsBySessionId(1L)).thenReturn(5);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> sessionService.deleteSession(1L));
        assertEquals(ErrorCode.SESSION_HAS_ENROLLMENT.getCode(), exception.getCode());

        verify(classSessionMapper, never()).updateById(any(ClassSession.class));
    }

    @Test
    @DisplayName("开放班期报名")
    void openEnrollment_Success() {
        // 准备模拟数据
        testSession.setStatus(SessionStatus.NOT_OPEN.getCode());
        when(classSessionMapper.selectById(1L)).thenReturn(testSession);
        when(classSessionMapper.updateById(any(ClassSession.class))).thenReturn(1);

        // 执行测试
        sessionService.openEnrollment(1L);

        // 验证调用
        verify(classSessionMapper).selectById(1L);
        verify(classSessionMapper).updateById(argThat(session ->
                session.getStatus() == SessionStatus.ENROLLING.getCode()));
    }

    @Test
    @DisplayName("开放班期报名失败 - 班期不存在")
    void openEnrollment_NotFound() {
        // 准备模拟数据
        when(classSessionMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> sessionService.openEnrollment(999L));
        assertEquals(ErrorCode.SESSION_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("关闭班期报名")
    void closeEnrollment_Success() {
        // 准备模拟数据
        when(classSessionMapper.selectById(1L)).thenReturn(testSession);
        when(classSessionMapper.updateById(any(ClassSession.class))).thenReturn(1);

        // 执行测试
        sessionService.closeEnrollment(1L);

        // 验证调用
        verify(classSessionMapper).selectById(1L);
        verify(classSessionMapper).updateById(argThat(session ->
                session.getStatus() == SessionStatus.NOT_OPEN.getCode()));
    }

    @Test
    @DisplayName("关闭班期报名失败 - 班期不存在")
    void closeEnrollment_NotFound() {
        // 准备模拟数据
        when(classSessionMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> sessionService.closeEnrollment(999L));
        assertEquals(ErrorCode.SESSION_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("查询可报名班期列表")
    void listEnrollableSessions_Success() {
        // 准备模拟数据
        List<ClassSession> sessions = List.of(testSession);
        when(classSessionMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(sessions);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);
        when(sysUserMapper.selectById(2L)).thenReturn(testInstructor);

        // 执行测试
        List<SessionResponse> result = sessionService.listEnrollableSessions(null);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSession.getSessionCode(), result.get(0).getSessionCode());
    }

    @Test
    @DisplayName("查询可报名班期列表 - 按课程筛选")
    void listEnrollableSessions_ByCourse() {
        // 准备模拟数据
        List<ClassSession> sessions = List.of(testSession);
        when(classSessionMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(sessions);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);
        when(sysUserMapper.selectById(2L)).thenReturn(testInstructor);

        // 执行测试
        List<SessionResponse> result = sessionService.listEnrollableSessions(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("讲师查询自己负责的班期")
    void getMySessionsAsInstructor_Success() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("instructor1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(sysUserMapper.selectByUsername("instructor1")).thenReturn(testInstructor);
            when(classSessionMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testSession));
            when(courseMapper.selectById(1L)).thenReturn(testCourse);
            when(sysUserMapper.selectById(2L)).thenReturn(testInstructor);

            // 执行测试
            List<SessionResponse> result = sessionService.getMySessionsAsInstructor();

            // 验证结果
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testSession.getSessionCode(), result.get(0).getSessionCode());
        }
    }

    @Test
    @DisplayName("计算剩余名额正确")
    void calculateRemainingQuota() {
        // 准备模拟数据
        testSession.setMaxCapacity(30);
        testSession.setCurrentEnrollment(25);

        when(classSessionMapper.selectById(1L)).thenReturn(testSession);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);
        when(sysUserMapper.selectById(2L)).thenReturn(testInstructor);

        // 执行测试
        SessionResponse result = sessionService.getSessionById(1L);

        // 验证结果
        assertEquals(5, result.getRemainingQuota()); // 30 - 25 = 5
    }

    @Test
    @DisplayName("状态名称显示正确")
    void statusNameDisplay() {
        // 准备模拟数据
        testSession.setStatus(SessionStatus.IN_PROGRESS.getCode());

        when(classSessionMapper.selectById(1L)).thenReturn(testSession);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);
        when(sysUserMapper.selectById(2L)).thenReturn(testInstructor);

        // 执行测试
        SessionResponse result = sessionService.getSessionById(1L);

        // 验证结果
        assertEquals("进行中", result.getStatusName());
    }
}
