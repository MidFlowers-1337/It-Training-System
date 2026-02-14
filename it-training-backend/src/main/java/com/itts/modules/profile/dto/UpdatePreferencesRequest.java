package com.itts.modules.profile.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(max = 10, message = "偏好类别数量不能超过10个")
    private List<String> preferredCategories;

    /**
     * 偏好难度
     */
    @Pattern(regexp = "^(BEGINNER|ELEMENTARY|INTERMEDIATE|ADVANCED)$",
            message = "偏好难度必须是 BEGINNER、ELEMENTARY、INTERMEDIATE 或 ADVANCED")
    private String preferredDifficulty;

    /**
     * 每日学习目标（分钟）
     */
    @Min(value = 1, message = "每日学习目标至少1分钟")
    @Max(value = 1440, message = "每日学习目标不能超过1440分钟")
    private Integer dailyStudyGoal;
}