package com.itts.modules.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 密码重置请求 DTO
 */
@Data
public class PasswordResetRequest {

    /**
     * 新密码
     */
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String newPassword;
}
