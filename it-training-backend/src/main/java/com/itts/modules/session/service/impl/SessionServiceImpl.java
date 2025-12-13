package com.itts.modules.session.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import com.itts.modules.session.service.SessionService;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
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
    public List<SessionResponse> listEnrollableSessions(Long courseId) {
        LambdaQueryWrapper<ClassSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassSession::getDeleted, 0)
                .eq(ClassSession::getStatus, SessionStatus.ENROLLING.getCode()); // 报名中

        if (courseId != null) {
            wrapper.eq(ClassSession::getCourseId, courseId);
        }

        wrapper.orderByAsc(ClassSession::getStartDate);

        List<ClassSession> sessions = classSessionMapper.selectList(wrapper);

        // 填充关联信息
        return sessions.stream()
                .map(this::fillAssociations)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SessionResponse getSessionById(Long id) {
        ClassSession session = classSessionMapper.selectById(id);
        if (session == null || session.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }
        return convertToResponse(fillAssociations(session));
    }

    @Override
    @Transactional
    public SessionResponse createSession(SessionCreateRequest request) {
        log.info("创建班期: {}", request.getSessionCode());

        // 检查班期编码是否已存在
        int count = classSessionMapper.countBySessionCode(request.getSessionCode());
        if (count > 0) {
            throw new BusinessException(ErrorCode.SESSION_CODE_EXISTS);
        }

        // 验证课程存在
        Course course = courseMapper.selectById(request.getCourseId());
        if (course == null || course.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }

        // 验证讲师存在
        SysUser instructor = sysUserMapper.selectById(request.getInstructorId());
        if (instructor == null || instructor.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        ClassSession session = new ClassSession();
        BeanUtils.copyProperties(request, session);
        session.setCurrentEnrollment(0); // 初始报名人数为0
        session.setStatus(SessionStatus.NOT_OPEN.getCode()); // 默认未开放
        session.setDeleted(0);

        classSessionMapper.insert(session);

        log.info("班期创建成功: {}, ID: {}", request.getSessionCode(), session.getId());

        session.setCourseName(course.getName());
        session.setInstructorName(instructor.getRealName());

        return convertToResponse(session);
    }

    @Override
    @Transactional
    public SessionResponse updateSession(Long id, SessionUpdateRequest request) {
        log.info("更新班期: {}", id);

        ClassSession session = classSessionMapper.selectById(id);
        if (session == null || session.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }

        // 更新字段（只更新非空字段）
        if (request.getInstructorId() != null) {
            // 验证讲师存在
            SysUser instructor = sysUserMapper.selectById(request.getInstructorId());
            if (instructor == null || instructor.getDeleted() == 1) {
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
    @Transactional
    public void deleteSession(Long id) {
        log.info("删除班期: {}", id);

        ClassSession session = classSessionMapper.selectById(id);
        if (session == null || session.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }

        // 检查是否有报名
        int enrollmentCount = classSessionMapper.countEnrollmentsBySessionId(id);
        if (enrollmentCount > 0) {
            throw new BusinessException(ErrorCode.SESSION_HAS_ENROLLMENT);
        }

        // 软删除
        session.setDeleted(1);
        classSessionMapper.updateById(session);

        log.info("班期删除成功: {}", id);
    }

    @Override
    @Transactional
    public void openEnrollment(Long id) {
        log.info("开放班期报名: {}", id);

        ClassSession session = classSessionMapper.selectById(id);
        if (session == null || session.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }

        session.setStatus(SessionStatus.ENROLLING.getCode());
        classSessionMapper.updateById(session);

        log.info("班期报名已开放: {}", id);
    }

    @Override
    @Transactional
    public void closeEnrollment(Long id) {
        log.info("关闭班期报名: {}", id);

        ClassSession session = classSessionMapper.selectById(id);
        if (session == null || session.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }

        session.setStatus(SessionStatus.NOT_OPEN.getCode());
        classSessionMapper.updateById(session);

        log.info("班期报名已关闭: {}", id);
    }

    @Override
    public List<SessionResponse> getMySessionsAsInstructor() {
        Long instructorId = getCurrentUserId();

        LambdaQueryWrapper<ClassSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassSession::getInstructorId, instructorId)
                .eq(ClassSession::getDeleted, 0)
                .orderByDesc(ClassSession::getStartDate);

        List<ClassSession> sessions = classSessionMapper.selectList(wrapper);

        return sessions.stream()
                .map(this::fillAssociations)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
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

        return user.getId();
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
        SessionResponse response = new SessionResponse();
        BeanUtils.copyProperties(session, response);

        // 计算剩余名额
        int remaining = session.getMaxCapacity() - (session.getCurrentEnrollment() == null ? 0 : session.getCurrentEnrollment());
        response.setRemainingQuota(remaining);

        // 设置状态名称
        SessionStatus status = SessionStatus.fromCode(session.getStatus());
        if (status != null) {
            response.setStatusName(status.getDesc());
        }

        return response;
    }
}
