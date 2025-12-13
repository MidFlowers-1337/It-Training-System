package com.itts.modules.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户成就实体
 */
@Data
@TableName("user_achievement")
public class UserAchievement {

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
     * 成就ID
     */
    private Long achievementId;

    /**
     * 获得时间
     */
    private LocalDateTime earnedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}