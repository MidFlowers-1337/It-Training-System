package com.itts.modules.enrollment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报名记录实体
 */
@Data
@TableName("enrollment")
public class Enrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学员ID
     */
    private Long userId;

    /**
     * 班期ID
     */
    private Long sessionId;

    /**
     * 状态: 0-已报名, 1-已取消
     */
    private Integer status;

    /**
     * 报名时间
     */
    private LocalDateTime enrolledAt;

    /**
     * 取消时间
     */
    private LocalDateTime canceledAt;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    // ========== 关联字段 ==========

    /**
     * 用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 真实姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String realName;

    /**
     * 班期编码（非数据库字段）
     */
    @TableField(exist = false)
    private String sessionCode;

    /**
     * 课程名称（非数据库字段）
     */
    @TableField(exist = false)
    private String courseName;

    /**
     * 讲师姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String instructorName;

    /**
     * 学员邮箱（非数据库字段）
     */
    @TableField(exist = false)
    private String studentEmail;

    /**
     * 学员手机号（非数据库字段）
     */
    @TableField(exist = false)
    private String studentPhone;
}
