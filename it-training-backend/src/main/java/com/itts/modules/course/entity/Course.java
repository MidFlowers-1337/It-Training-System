package com.itts.modules.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程实体
 */
@Data
@TableName("course")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程编码（如: JAVA-001）
     */
    private String code;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 分类: BACKEND-后端开发, FRONTEND-前端开发, DATABASE-数据库, CLOUD-云计算, AI-人工智能, OTHER-其他
     */
    private String category;

    /**
     * 课程简介
     */
    private String description;

    /**
     * 封面图URL
     */
    private String coverImage;

    /**
     * 难度: 1-入门, 2-初级, 3-中级, 4-高级
     */
    private Integer difficulty;

    /**
     * 课时数（小时）
     */
    private Integer durationHours;

    /**
     * 先修要求（课程编码，逗号分隔）
     */
    private String prerequisites;

    /**
     * 标签（逗号分隔）
     */
    private String tags;

    /**
     * 状态: 0-下架, 1-上架
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
}
