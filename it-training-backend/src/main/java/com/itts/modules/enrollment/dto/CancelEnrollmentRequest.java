package com.itts.modules.enrollment.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 取消报名请求 DTO
 */
@Data
public class CancelEnrollmentRequest {

    /**
     * 取消原因
     */
    @Size(max = 200, message = "取消原因不能超过200个字符")
    private String reason;
}
