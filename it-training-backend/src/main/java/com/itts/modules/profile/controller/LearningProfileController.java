package com.itts.modules.profile.controller;

import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.profile.dto.UpdatePreferencesRequest;
import com.itts.modules.profile.dto.UserProfileResponse;
import com.itts.modules.profile.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "用户画像", description = "用户画像相关接口")
@RestController
@RequestMapping("/api/v1/learning")
@RequiredArgsConstructor
public class LearningProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "获取用户画像")
    @GetMapping("/profile")
    public R<UserProfileResponse> getUserProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(userProfileService.getUserProfile(userId));
    }

    @Operation(summary = "更新技能标签")
    @PostMapping("/profile/skills")
    public R<Void> updateSkillTags(@RequestBody List<String> skillTags) {
        Long userId = SecurityUtils.getCurrentUserId();
        userProfileService.updateSkillTags(userId, skillTags);
        return R.ok();
    }

    @Operation(summary = "更新学习偏好")
    @PostMapping("/profile/preferences")
    public R<Void> updatePreferences(@Valid @RequestBody UpdatePreferencesRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        userProfileService.updatePreferences(userId,
            request.getPreferredCategories(),
            request.getPreferredDifficulty(),
            request.getDailyStudyGoal());
        return R.ok();
    }

    @Operation(summary = "获取学习能力评估")
    @GetMapping("/profile/ability-assessment")
    public R<Map<String, Object>> getLearningAbilityAssessment() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(userProfileService.getLearningAbilityAssessment(userId));
    }

    @Operation(summary = "分析学习行为")
    @PostMapping("/profile/analyze")
    public R<Void> analyzeLearningBehavior() {
        Long userId = SecurityUtils.getCurrentUserId();
        userProfileService.analyzeLearningBehavior(userId);
        return R.ok();
    }
}
