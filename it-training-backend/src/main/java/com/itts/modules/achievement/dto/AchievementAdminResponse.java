package com.itts.modules.achievement.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 成就管理响应 DTO（管理员视角，包含管理字段）
 */
@Data
public class AchievementAdminResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private String iconUrl;
    private String category;
    private String conditionType;
    private Integer conditionValue;
    private Integer points;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
