package com.itts.modules.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（登录账号）
     */
    private String username;

    /**
     * 密码（BCrypt加密）
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 角色: ADMIN-管理员, INSTRUCTOR-讲师, STUDENT-学员
     */
    private String role;

    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 软删除: 0-正常, 1-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 是否为管理员
     */
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    /**
     * 是否为讲师
     */
    public boolean isInstructor() {
        return "INSTRUCTOR".equals(this.role);
    }

    /**
     * 是否为学员
     */
    public boolean isStudent() {
        return "STUDENT".equals(this.role);
    }
}
