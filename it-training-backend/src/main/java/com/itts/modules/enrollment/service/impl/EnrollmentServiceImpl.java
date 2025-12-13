package com.itts.modules.enrollment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.enums.EnrollmentStatus;
import com.itts.enums.SessionStatus;
import com.itts.modules.enrollment.dto.EnrollmentResponse;
import com.itts.modules.enrollment.entity.Enrollment;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import com.itts.modules.enrollment.service.EnrollmentService;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报名服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentMapper enrollmentMapper;
    private final ClassSessionMapper classSessionMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    @Transactional
    public EnrollmentResponse enroll(Long sessionId) {
        // 获取当前用户
        Long userId = getCurrentUserId();
        log.info("用户报名: userId={}, sessionId={}", userId, sessionId);

        // 检查班期是否存在
        ClassSession session = classSessionMapper.selectById(sessionId);
        if (session == null || session.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }

        // 检查班期状态是否允许报名
        if (session.getStatus() != SessionStatus.ENROLLING.getCode()) {
            throw new BusinessException(ErrorCode.SESSION_NOT_ENROLLABLE);
        }

        // 检查是否已报名（状态为已报名）
        int existCount = enrollmentMapper.countByUserAndSession(userId, sessionId);
        if (existCount > 0) {
            throw new BusinessException(ErrorCode.ENROLLMENT_DUPLICATE);
        }

        // 原子增加报名人数（并发控制）
        int updated = classSessionMapper.incrementEnrollment(sessionId);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.ENROLLMENT_QUOTA_FULL);
        }

        // 创建报名记录
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setSessionId(sessionId);
        enrollment.setStatus(EnrollmentStatus.ENROLLED.getCode());
        enrollment.setEnrolledAt(LocalDateTime.now());

        enrollmentMapper.insert(enrollment);

        log.info("报名成功: enrollmentId={}", enrollment.getId());

        return convertToResponse(enrollment);
    }

    @Override
    @Transactional
    public void cancelEnrollment(Long enrollmentId, String reason) {
        Long userId = getCurrentUserId();
        log.info("取消报名: userId={}, enrollmentId={}", userId, enrollmentId);

        // 查询报名记录
        Enrollment enrollment = enrollmentMapper.selectById(enrollmentId);
        if (enrollment == null) {
            throw new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }

        // 检查是否是自己的报名记录
        if (!enrollment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 检查状态是否允许取消
        if (enrollment.getStatus() != EnrollmentStatus.ENROLLED.getCode()) {
            throw new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }

        // 检查班期状态（进行中或已结束不允许取消）
        ClassSession session = classSessionMapper.selectById(enrollment.getSessionId());
        if (session != null && (session.getStatus() == SessionStatus.IN_PROGRESS.getCode()
                || session.getStatus() == SessionStatus.ENDED.getCode())) {
            throw new BusinessException(ErrorCode.ENROLLMENT_CANNOT_CANCEL);
        }

        // 更新报名状态
        enrollment.setStatus(EnrollmentStatus.CANCELED.getCode());
        enrollment.setCanceledAt(LocalDateTime.now());
        enrollment.setCancelReason(reason);
        enrollmentMapper.updateById(enrollment);

        // 减少班期报名人数
        classSessionMapper.decrementEnrollment(enrollment.getSessionId());

        log.info("取消报名成功: enrollmentId={}", enrollmentId);
    }

    @Override
    public List<EnrollmentResponse> getMyEnrollments() {
        Long userId = getCurrentUserId();

        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enrollment::getUserId, userId)
                .orderByDesc(Enrollment::getEnrolledAt);

        List<Enrollment> enrollments = enrollmentMapper.selectList(wrapper);

        return enrollments.stream()
                .map(this::fillAndConvert)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<EnrollmentResponse> listEnrollments(int page, int size, Long userId, Long sessionId, Integer status) {
        Page<Enrollment> pageParam = new Page<>(page, size);

        // 使用XML中定义的关联查询
        IPage<Enrollment> enrollmentPage = enrollmentMapper.selectPageWithDetails(pageParam, userId, sessionId, status);

        return enrollmentPage.convert(this::convertToResponse);
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentMapper.selectById(id);
        if (enrollment == null) {
            throw new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }
        return fillAndConvert(enrollment);
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        String username = SecurityUtils.getCurrentUsername();
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user.getId();
    }

    /**
     * 填充关联信息并转换
     */
    private EnrollmentResponse fillAndConvert(Enrollment enrollment) {
        EnrollmentResponse response = new EnrollmentResponse();
        BeanUtils.copyProperties(enrollment, response);

        // 填充用户信息
        SysUser user = sysUserMapper.selectById(enrollment.getUserId());
        if (user != null) {
            response.setUserName(user.getUsername());
            response.setRealName(user.getRealName());
        }

        // 填充班期和课程信息
        ClassSession session = classSessionMapper.selectById(enrollment.getSessionId());
        if (session != null) {
            response.setSessionCode(session.getSessionCode());
            response.setCourseName(session.getCourseName());
            response.setInstructorName(session.getInstructorName());
            response.setStartDate(session.getStartDate());
            response.setEndDate(session.getEndDate());
            response.setSchedule(session.getSchedule());

            // 如果班期没有关联信息，再查询一次
            if (session.getCourseName() == null) {
                // 这里简化处理，实际可以再查询课程表
            }
        }

        // 设置状态名称
        response.setStatusName(enrollment.getStatus() == 0 ? "已报名" : "已取消");

        return response;
    }

    /**
     * 转换为响应对象（用于已填充关联信息的情况）
     */
    private EnrollmentResponse convertToResponse(Enrollment enrollment) {
        EnrollmentResponse response = new EnrollmentResponse();
        BeanUtils.copyProperties(enrollment, response);

        // 设置状态名称
        response.setStatusName(enrollment.getStatus() == 0 ? "已报名" : "已取消");

        // 设置学员信息别名（用于讲师端）
        response.setStudentName(enrollment.getRealName());
        response.setStudentUsername(enrollment.getUserName());

        return response;
    }
}
