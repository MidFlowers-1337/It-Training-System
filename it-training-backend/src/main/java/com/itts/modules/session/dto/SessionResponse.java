package com.itts.modules.session.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 班期响应 DTO
 */
@Data
public class SessionResponse {

    /**
     * 班期ID
     */
    private Long id;

    /**
     * 所属课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 授课讲师ID
     */
    private Long instructorId;

    /**
     * 讲师姓名
     */
    private String instructorName;

    /**
     * 班期编码
     */
    private String sessionCode;

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
     * 上课地点/线上链接
     */
    private String location;

    /**
     * 最大名额
     */
    private Integer maxCapacity;

    /**
     * 当前报名人数
     */
    private Integer currentEnrollment;

    /**
     * 剩余名额
     */
    private Integer remainingQuota;

    /**
     * 状态: 0-未开放, 1-报名中, 2-已满员, 3-进行中, 4-已结束
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
