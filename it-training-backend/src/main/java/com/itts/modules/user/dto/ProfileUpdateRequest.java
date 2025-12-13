package com.itts.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 个人资料更新请求DTO
 */
@Data
public class ProfileUpdateRequest {

    /**
     * 真实姓名
     */
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String realName;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Size(max = 20, message = "手机号长度不能超过20个字符")
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 个人简介
     */
    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    private String bio;
}