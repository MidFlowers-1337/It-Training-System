package com.itts.modules.enrollment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报名请求 DTO
 */
@Data
public class EnrollRequest {

    /**
     * 班期ID
     */
    @NotNull(message = "班期ID不能为空")
    private Long sessionId;
}
