package com.itts.modules.enrollment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.modules.enrollment.dto.EnrollmentResponse;

import java.util.List;

/**
 * 报名服务接口
 */
public interface EnrollmentService {

    /**
     * 学员报名
     * @param sessionId 班期ID
     * @return 报名记录
     */
    EnrollmentResponse enroll(Long sessionId);

    /**
     * 取消报名
     * @param enrollmentId 报名ID
     * @param reason 取消原因
     */
    void cancelEnrollment(Long enrollmentId, String reason);

    /**
     * 获取当前用户的报名列表
     * @return 报名列表
     */
    List<EnrollmentResponse> getMyEnrollments();

    /**
     * 分页查询报名列表（管理端）
     * @param page 页码
     * @param size 每页大小
     * @param userId 用户ID过滤
     * @param sessionId 班期ID过滤
     * @param status 状态过滤
     * @return 报名分页列表
     */
    IPage<EnrollmentResponse> listEnrollments(int page, int size, Long userId, Long sessionId, Integer status);

    /**
     * 根据ID获取报名详情
     * @param id 报名ID
     * @return 报名详情
     */
    EnrollmentResponse getEnrollmentById(Long id);
}
