package com.itts.modules.enrollment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.enums.DeleteFlag;
import com.itts.enums.EnrollmentStatus;
import com.itts.enums.SessionStatus;
import com.itts.modules.enrollment.dto.EnrollmentResponse;
import com.itts.modules.enrollment.entity.Enrollment;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import com.itts.modules.enrollment.service.EnrollmentService;
import com.itts.modules.notification.service.SystemNotificationService;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DuplicateKeyException;

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
    private final SystemNotificationService systemNotificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EnrollmentResponse enroll(Long sessionId) {
        // 获取当前用户
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("用户报名: userId={}, sessionId={}", userId, sessionId);

        // 检查班期是否存在
        ClassSession session = classSessionMapper.selectById(sessionId);
        if (session == null || DeleteFlag.isDeleted(session.getDeleted())) {
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

        try {
            enrollmentMapper.insert(enrollment);
        } catch (DuplicateKeyException e) {
            // 并发插入导致的唯一约束冲突，事务回滚会自动撤销 incrementEnrollment 的变更
            throw new BusinessException(ErrorCode.ENROLLMENT_DUPLICATE);
        }

        log.info("报名成功: enrollmentId={}", enrollment.getId());

        // [Phase 6 #15] 发送报名成功通知
        try {
            String courseName = session.getCourseName() != null ? session.getCourseName() : "课程";
            systemNotificationService.sendNotification(
                userId,
                "报名成功",
                "您已成功报名「" + courseName + "」，祝学习愉快！",
                "ENROLLMENT"
            );
        } catch (Exception e) {
            log.warn("发送报名通知失败，不影响主流程: {}", e.getMessage());
        }

        return convertToResponse(enrollment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelEnrollment(Long enrollmentId, String reason) {
        Long userId = SecurityUtils.getCurrentUserId();
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
        Long userId = SecurityUtils.getCurrentUserId();

        // 使用关联查询避免 N+1 问题
        List<Enrollment> enrollments = enrollmentMapper.selectUserEnrollmentsWithDetails(userId);

        return enrollments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<EnrollmentResponse> getMyEnrollments(int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();

        Page<Enrollment> pageParam = new Page<>(page, size);
        IPage<Enrollment> enrollmentPage = enrollmentMapper.selectUserEnrollmentsPage(pageParam, userId);

        return enrollmentPage.convert(this::convertToResponse);
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
     * 填充关联信息并转换
     */
    private EnrollmentResponse fillAndConvert(Enrollment enrollment) {
        EnrollmentResponse.EnrollmentResponseBuilder builder = EnrollmentResponse.builder()
                .id(enrollment.getId())
                .userId(enrollment.getUserId())
                .sessionId(enrollment.getSessionId())
                .status(enrollment.getStatus())
                .enrolledAt(enrollment.getEnrolledAt())
                .canceledAt(enrollment.getCanceledAt())
                .cancelReason(enrollment.getCancelReason());

        // 填充用户信息
        SysUser user = sysUserMapper.selectById(enrollment.getUserId());
        if (user != null) {
            builder.userName(user.getUsername())
                   .realName(user.getRealName());
        }

        // 填充班期和课程信息
        ClassSession session = classSessionMapper.selectById(enrollment.getSessionId());
        if (session != null) {
            builder.sessionCode(session.getSessionCode())
                   .courseName(session.getCourseName())
                   .instructorName(session.getInstructorName())
                   .startDate(session.getStartDate())
                   .endDate(session.getEndDate())
                   .schedule(session.getSchedule());
        }

        // 设置状态名称
        EnrollmentStatus enrollStatus = EnrollmentStatus.fromCode(enrollment.getStatus());
        builder.statusName(enrollStatus != null ? enrollStatus.getDesc() : "未知");

        return builder.build();
    }

    /**
     * 转换为响应对象（用于已填充关联信息的情况）
     */
    private EnrollmentResponse convertToResponse(Enrollment enrollment) {
        // 设置状态名称
        EnrollmentStatus status = EnrollmentStatus.fromCode(enrollment.getStatus());

        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .userId(enrollment.getUserId())
                .sessionId(enrollment.getSessionId())
                .status(enrollment.getStatus())
                .statusName(status != null ? status.getDesc() : "未知")
                .enrolledAt(enrollment.getEnrolledAt())
                .canceledAt(enrollment.getCanceledAt())
                .cancelReason(enrollment.getCancelReason())
                .userName(enrollment.getUserName())
                .realName(enrollment.getRealName())
                .sessionCode(enrollment.getSessionCode())
                .courseName(enrollment.getCourseName())
                .instructorName(enrollment.getInstructorName())
                .startDate(enrollment.getStartDate())
                .endDate(enrollment.getEndDate())
                .schedule(enrollment.getSchedule())
                .studentEmail(enrollment.getStudentEmail())
                .studentPhone(enrollment.getStudentPhone())
                .studentName(enrollment.getRealName())
                .studentUsername(enrollment.getUserName())
                .build();
    }
}
