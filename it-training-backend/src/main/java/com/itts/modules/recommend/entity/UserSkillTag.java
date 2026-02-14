package com.itts.modules.recommend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户技能标签实体
 */
@Data
@TableName("user_skill_tag")
public class UserSkillTag {

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
     * 技能名称
     */
    private String skillName;

    /**
     * 熟练度(0-100)
     */
    private Integer proficiencyLevel;

    /**
     * 来源: self_report-自评, course_complete-课程完成, assessment-测评
     */
    private String source;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}