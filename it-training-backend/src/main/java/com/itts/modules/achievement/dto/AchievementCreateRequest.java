package com.itts.modules.achievement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 成就创建请求 DTO
 */
@Data
public class AchievementCreateRequest {

    /**
     * 成就编码
     */
    @NotBlank(message = "成就编码不能为空")
    @Size(max = 50, message = "成就编码不能超过50个字符")
    private String code;

    /**
     * 成就名称
     */
    @NotBlank(message = "成就名称不能为空")
    @Size(max = 100, message = "成就名称不能超过100个字符")
    private String name;

    /**
     * 成就描述
     */
    @Size(max = 500, message = "成就描述不能超过500个字符")
    private String description;

    /**
     * 图标URL
     */
    @Size(max = 500, message = "图标URL不能超过500个字符")
    private String iconUrl;

    /**
     * 分类: general-通用, streak-连续, course-课程, skill-技能
     */
    @NotBlank(message = "分类不能为空")
    @Pattern(regexp = "^(general|streak|course|skill)$", message = "分类必须是 general、streak、course 或 skill")
    private String category;

    /**
     * 条件类型: streak_days, courses_completed, hours_studied, checkin_days
     */
    @NotBlank(message = "条件类型不能为空")
    @Pattern(regexp = "^(streak_days|courses_completed|hours_studied|checkin_days)$",
            message = "条件类型必须是 streak_days、courses_completed、hours_studied 或 checkin_days")
    private String conditionType;

    /**
     * 条件值
     */
    @NotNull(message = "条件值不能为空")
    @Min(value = 1, message = "条件值最小为1")
    private Integer conditionValue;

    /**
     * 成就积分
     */
    @NotNull(message = "积分不能为空")
    @Min(value = 0, message = "积分不能为负数")
    private Integer points;
}
