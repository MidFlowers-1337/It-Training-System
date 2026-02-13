package com.itts.modules.session.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itts.common.config.RedisCacheConfig;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.enums.DeleteFlag;
import com.itts.enums.SessionStatus;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.session.dto.SessionCreateRequest;
import com.itts.modules.session.dto.SessionResponse;
import com.itts.modules.session.dto.SessionUpdateRequest;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import com.itts.modules.session.service.SessionService;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import com.itts.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 班期服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final ClassSessionMapper classSessionMapper;
    private final CourseMapper courseMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public IPage<SessionResponse> listSessions(int page, int size, Long courseId, Long instructorId, Integer status) {
        Page<ClassSession> pageParam = new Page<>(page, size);

        // 使用XML中定义的关联查询
        IPage<ClassSession> sessionPage = classSessionMapper.selectPageWithDetails(pageParam, courseId, instructorId, status);

        // 转换为响应对象
        return sessionPage.convert(this::convertToResponse);
    }

    @Override
    @Cacheable(value = RedisCacheConfig.CACHE_SESSION_LIST, key = "'enrollable_' + (#courseId ?: 'all')")
    public List<SessionResponse> listEnrollableSessions(Long courseId) {
        LambdaQueryWrapper<ClassSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassSession::getDeleted, DeleteFlag.NOT_DELETED)
                .eq(ClassSession::getStatus, SessionStatus.ENROLLING.getCode()); // 报名中

        if (courseId != null) {
            wrapper.eq(ClassSession::getCourseId, courseId);
        }

        wrapper.orderByAsc(ClassSession::getStartDate);

        List<ClassSession> sessions = classSessionMapper.selectList(wrapper);

        if (sessions.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量填充关联信息，避免 N+1 查询
        batchFillAssociations(sessions);

        return sessions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = RedisCacheConfig.CACHE_SESSIONS, key = "#id")
    public SessionResponse getSessionById(Long id) {
        ClassSession session = classSessionMapper.selectById(id);
        if (session == null || DeleteFlag.isDeleted(session.getDeleted())) {
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }
        return convertToResponse(fillAssociations(session));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisCacheConfig.CACHE_SESSION_LIST, allEntries = true)
    public SessionResponse createSession(SessionCreateRequest request) {
        log.info("创建班期: {}", request.getSessionCode());

        // 检查班期编码是否已存在
        int count = classSessionMapper.countBySessionCode(request.getSessionCode());
        if (count > 0) {
            throw new BusinessException(ErrorCode.SESSION_CODE_EXISTS);
        }

        // 验证课程存在
        Course course = courseMapper.selectById(request.getCourseId());
        if (course == null || DeleteFlag.isDeleted(course.getDeleted())) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }

        // 验证讲师存在
        SysUser instructor = sysUserMapper.selectById(request.getInstructorId());
        if (instructor == null || DeleteFlag.isDeleted(instructor.getDeleted())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        ClassSession session = new ClassSession();
        session.setCourseId(request.getCourseId());
        session.setInstructorId(request.getInstructorId());
        session.setSessionCode(request.getSessionCode());
        session.setStartDate(request.getStartDate());
        session.setEndDate(request.getEndDate());
        session.setSchedule(request.getSchedule());
        session.setLocation(request.getLocation());
        session.setMaxCapacity(request.getMaxCapacity());
        session.setCurrentEnrollment(0); // 初始报名人数为0
        session.setStatus(SessionStatus.ENROLLING.getCode()); // 默认为报名中，方便快速开放
        session.setDeleted(DeleteFlag.NOT_DELETED);

        classSessionMapper.insert(session);

        log.info("班期创建成功: {}, ID: {}", request.getSessionCode(), session.getId());

        session.setCourseName(course.getName());
        session.setInstructorName(instructor.getRealName());

        return convertToResponse(session);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CACHE_SESSIONS, key = "#id"),
            @CacheEvict(value = RedisCacheConfig.CACHE_SESSION_LIST, allEntries = true)
    })
    public SessionResponse updateSession(Long id, SessionUpdateRequest request) {
        log.info("更新班期: {}", id);

        ClassSession session = classSessionMapper.selectById(id);
        if (session == null || DeleteFlag.isDeleted(session.getDeleted())) {
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }

        // 更新字段（只更新非空字段）
        if (request.getInstructorId() != null) {
            // 验证讲师存在
            SysUser instructor = sysUserMapper.selectById(request.getInstructorId());
            if (instructor == null || DeleteFlag.isDeleted(instructor.getDeleted())) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            session.setInstructorId(request.getInstructorId());
        }
        if (request.getStartDate() != null) {
            session.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            session.setEndDate(request.getEndDate());
        }
        if (request.getSchedule() != null) {
            session.setSchedule(request.getSchedule());
        }
        if (request.getLocation() != null) {
            session.setLocation(request.getLocation());
        }
        if (request.getMaxCapacity() != null) {
            // 检查新名额是否小于当前报名人数
            if (request.getMaxCapacity() < session.getCurrentEnrollment()) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "最大名额不能小于当前报名人数");
            }
            session.setMaxCapacity(request.getMaxCapacity());
        }
        if (request.getStatus() != null) {
            session.setStatus(request.getStatus());
        }

        classSessionMapper.updateById(session);

        log.info("班期更新成功: {}", id);
        return convertToResponse(fillAssociations(session));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CACHE_SESSIONS, key = "#id"),
            @CacheEvict(value = RedisCacheConfig.CACHE_SESSION_LIST, allEntries = true)
    })
    public void deleteSession(Long id) {
        log.info("删除班期: {}", id);

        ClassSession session = classSessionMapper.selectById(id);
        if (session == null || DeleteFlag.isDeleted(session.getDeleted())) {
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }

        // 检查是否有报名
        int enrollmentCount = classSessionMapper.countEnrollmentsBySessionId(id);
        if (enrollmentCount > 0) {
            throw new BusinessException(ErrorCode.SESSION_HAS_ENROLLMENT);
        }

        // 软删除
        session.setDeleted(DeleteFlag.DELETED);
        classSessionMapper.updateById(session);

        log.info("班期删除成功: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CACHE_SESSIONS, key = "#id"),
            @CacheEvict(value = RedisCacheConfig.CACHE_SESSION_LIST, allEntries = true)
    })
    public void openEnrollment(Long id) {
        log.info("开放班期报名: {}", id);

        ClassSession session = classSessionMapper.selectById(id);
        if (session == null || DeleteFlag.isDeleted(session.getDeleted())) {
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }

        session.setStatus(SessionStatus.ENROLLING.getCode());
        classSessionMapper.updateById(session);

        log.info("班期报名已开放: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CACHE_SESSIONS, key = "#id"),
            @CacheEvict(value = RedisCacheConfig.CACHE_SESSION_LIST, allEntries = true)
    })
    public void closeEnrollment(Long id) {
        log.info("关闭班期报名: {}", id);

        ClassSession session = classSessionMapper.selectById(id);
        if (session == null || DeleteFlag.isDeleted(session.getDeleted())) {
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }

        session.setStatus(SessionStatus.NOT_OPEN.getCode());
        classSessionMapper.updateById(session);

        log.info("班期报名已关闭: {}", id);
    }

    @Override
    public List<SessionResponse> getMySessionsAsInstructor() {
        Long instructorId = SecurityUtils.getCurrentUserId();

        LambdaQueryWrapper<ClassSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassSession::getInstructorId, instructorId)
                .eq(ClassSession::getDeleted, 0)
                .orderByDesc(ClassSession::getStartDate);

        List<ClassSession> sessions = classSessionMapper.selectList(wrapper);

        if (sessions.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量填充关联信息，避免 N+1 查询
        batchFillAssociations(sessions);

        return sessions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 批量填充关联信息（课程名称、讲师姓名）
     * 收集所有 courseId 和 instructorId，一次性批量查询，避免 N+1 问题
     */
    private void batchFillAssociations(List<ClassSession> sessions) {
        // 收集需要查询的 courseId（排除已有名称的）
        Set<Long> courseIds = sessions.stream()
                .filter(s -> s.getCourseId() != null && s.getCourseName() == null)
                .map(ClassSession::getCourseId)
                .collect(Collectors.toSet());

        // 收集需要查询的 instructorId（排除已有名称的）
        Set<Long> instructorIds = sessions.stream()
                .filter(s -> s.getInstructorId() != null && s.getInstructorName() == null)
                .map(ClassSession::getInstructorId)
                .collect(Collectors.toSet());

        // 批量查询课程
        Map<Long, Course> courseMap = Collections.emptyMap();
        if (!courseIds.isEmpty()) {
            courseMap = courseMapper.selectBatchIds(courseIds).stream()
                    .collect(Collectors.toMap(Course::getId, Function.identity()));
        }

        // 批量查询讲师
        Map<Long, SysUser> instructorMap = Collections.emptyMap();
        if (!instructorIds.isEmpty()) {
            instructorMap = sysUserMapper.selectBatchIds(instructorIds).stream()
                    .collect(Collectors.toMap(SysUser::getId, Function.identity()));
        }

        // 填充
        for (ClassSession session : sessions) {
            if (session.getCourseId() != null && session.getCourseName() == null) {
                Course course = courseMap.get(session.getCourseId());
                if (course != null) {
                    session.setCourseName(course.getName());
                }
            }
            if (session.getInstructorId() != null && session.getInstructorName() == null) {
                SysUser instructor = instructorMap.get(session.getInstructorId());
                if (instructor != null) {
                    session.setInstructorName(instructor.getRealName());
                }
            }
        }
    }

    /**
     * 填充关联信息
     */
    private ClassSession fillAssociations(ClassSession session) {
        // 填充课程名称
        if (session.getCourseId() != null && session.getCourseName() == null) {
            Course course = courseMapper.selectById(session.getCourseId());
            if (course != null) {
                session.setCourseName(course.getName());
            }
        }

        // 填充讲师姓名
        if (session.getInstructorId() != null && session.getInstructorName() == null) {
            SysUser instructor = sysUserMapper.selectById(session.getInstructorId());
            if (instructor != null) {
                session.setInstructorName(instructor.getRealName());
            }
        }

        return session;
    }

    /**
     * 转换为响应对象
     */
    private SessionResponse convertToResponse(ClassSession session) {
        // 计算剩余名额
        int remaining = session.getMaxCapacity() - (session.getCurrentEnrollment() == null ? 0 : session.getCurrentEnrollment());

        // 获取状态名称
        SessionStatus status = SessionStatus.fromCode(session.getStatus());

        return SessionResponse.builder()
                .id(session.getId())
                .courseId(session.getCourseId())
                .courseName(session.getCourseName())
                .instructorId(session.getInstructorId())
                .instructorName(session.getInstructorName())
                .sessionCode(session.getSessionCode())
                .startDate(session.getStartDate())
                .endDate(session.getEndDate())
                .schedule(session.getSchedule())
                .location(session.getLocation())
                .maxCapacity(session.getMaxCapacity())
                .currentEnrollment(session.getCurrentEnrollment())
                .remainingQuota(remaining)
                .status(session.getStatus())
                .statusName(status != null ? status.getDesc() : null)
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }
}
