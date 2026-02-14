package com.itts.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码确认请求（用于注销账号、清除数据等敏感操作）
 */
@Data
public class PasswordConfirmRequest {

    @NotBlank(message = "密码不能为空")
    private String password;
}
