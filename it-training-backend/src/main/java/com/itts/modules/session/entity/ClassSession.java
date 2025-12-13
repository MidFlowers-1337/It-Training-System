package com.itts.modules.session.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itts.modules.course.entity.Course;
import com.itts.modules.user.entity.SysUser;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 班期实体
 */
@Data
@TableName("class_session")
public class ClassSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属课程ID
     */
    private Long courseId;

    /**
     * 授课讲师ID
     */
    private Long instructorId;

    /**
     * 班期编码（如: JAVA-001-2024S1）
     */
    private String sessionCode;

    /**
     * 开班日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 上课时间描述（如: 每周六 9:00-12:00）
     */
    private String schedule;

    /**
     * 上课地点/线上链接
     */
    private String location;

    /**
     * 最大名额
     */
    private Integer maxCapacity;

    /**
     * 当前报名人数
     */
    private Integer currentEnrollment;

    /**
     * 状态: 0-未开放, 1-报名中, 2-已满员, 3-进行中, 4-已结束
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

    // ========== 关联字段 ==========

    /**
     * 课程信息（非数据库字段）
     */
    @TableField(exist = false)
    private Course course;

    /**
     * 讲师信息（非数据库字段）
     */
    @TableField(exist = false)
    private SysUser instructor;

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
     * 剩余名额
     */
    public Integer getRemainingQuota() {
        return maxCapacity - (currentEnrollment == null ? 0 : currentEnrollment);
    }
}
