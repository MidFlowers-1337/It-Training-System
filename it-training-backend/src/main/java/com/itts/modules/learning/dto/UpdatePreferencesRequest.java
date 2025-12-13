package com.itts.modules.learning.dto;

import lombok.Data;

import java.util.List;

/**
 * 更新学习偏好请求DTO
 */
@Data
public class UpdatePreferencesRequest {

    /**
     * 偏好类别列表
     */
    private List<String> preferredCategories;

    /**
     * 偏好难度
     */
    private String preferredDifficulty;

    /**
     * 每日学习目标（分钟）
     */
    private Integer dailyStudyGoal;
}