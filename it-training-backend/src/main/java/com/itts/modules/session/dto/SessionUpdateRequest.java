package com.itts.modules.session.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 班期更新请求 DTO
 */
@Data
public class SessionUpdateRequest {

    /**
     * 授课讲师ID
     */
    private Long instructorId;

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
    @Size(max = 200, message = "上课时间描述不能超过200个字符")
    private String schedule;

    /**
     * 上课地点/线上链接
     */
    @Size(max = 500, message = "上课地点不能超过500个字符")
    private String location;

    /**
     * 最大名额
     */
    @Min(value = 1, message = "最大名额最少为1")
    private Integer maxCapacity;

    /**
     * 状态: 0-未开放, 1-报名中, 2-已满员, 3-进行中, 4-已结束
     */
    @Min(value = 0, message = "状态值最小为0")
    @Max(value = 4, message = "状态值最大为4")
    private Integer status;
}
