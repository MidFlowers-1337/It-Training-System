package com.itts.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户更新请求 DTO
 */
@Data
public class UserUpdateRequest {

    /**
     * 真实姓名
     */
    @Size(max = 50, message = "真实姓名不能超过50个字符")
    private String realName;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 角色: ADMIN, INSTRUCTOR, STUDENT
     */
    @Pattern(regexp = "^(ADMIN|INSTRUCTOR|STUDENT)$", message = "角色必须是ADMIN、INSTRUCTOR或STUDENT")
    private String role;

    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;
}
