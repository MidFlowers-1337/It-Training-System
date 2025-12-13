package com.itts.modules.enrollment.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 报名响应 DTO
 */
@Data
public class EnrollmentResponse {

    /**
     * 报名ID
     */
    private Long id;

    /**
     * 学员ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 班期ID
     */
    private Long sessionId;

    /**
     * 班期编码
     */
    private String sessionCode;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 讲师姓名
     */
    private String instructorName;

    /**
     * 开班日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 上课时间描述
     */
    private String schedule;

    /**
     * 状态: 0-已报名, 1-已取消
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 报名时间
     */
    private LocalDateTime enrolledAt;

    /**
     * 取消时间
     */
    private LocalDateTime canceledAt;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 学员邮箱
     */
    private String studentEmail;

    /**
     * 学员手机号
     */
    private String studentPhone;

    /**
     * 学员姓名（别名，用于讲师端）
     */
    private String studentName;

    /**
     * 学员用户名（别名）
     */
    private String studentUsername;
}
