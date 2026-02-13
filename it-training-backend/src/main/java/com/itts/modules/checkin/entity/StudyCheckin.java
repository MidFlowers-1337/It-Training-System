package com.itts.modules.checkin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习打卡记录实体
 */
@Data
@TableName("study_checkin")
public class StudyCheckin {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 打卡日期
     */
    private LocalDate checkinDate;

    /**
     * 学习时长(分钟)
     */
    private Integer studyMinutes;

    /**
     * 学习的课程ID列表JSON
     */
    private String coursesStudied;

    /**
     * 学习笔记
     */
    private String note;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}