package com.itts.modules.session.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 班期创建请求 DTO
 */
@Data
public class SessionCreateRequest {

    /**
     * 所属课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 授课讲师ID
     */
    @NotNull(message = "讲师ID不能为空")
    private Long instructorId;

    /**
     * 班期编码（如: JAVA-001-2024S1）
     */
    @NotBlank(message = "班期编码不能为空")
    @Size(max = 50, message = "班期编码不能超过50个字符")
    private String sessionCode;

    /**
     * 开班日期
     */
    @NotNull(message = "开班日期不能为空")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    /**
     * 上课时间描述（如: 每周六 9:00-12:00）
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
    @NotNull(message = "最大名额不能为空")
    @Min(value = 1, message = "最大名额最少为1")
    private Integer maxCapacity;
}
