package com.itts.modules.achievement.controller;

import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.achievement.dto.AchievementResponse;
import com.itts.modules.achievement.service.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "成就系统", description = "学习成就相关接口")
@RestController
@RequestMapping("/api/v1/learning")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @Operation(summary = "获取所有成就（含获得状态）")
    @GetMapping("/achievements")
    public R<List<AchievementResponse>> getAllAchievements() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(achievementService.getAllAchievements(userId));
    }

    @Operation(summary = "获取已获得的成就")
    @GetMapping("/achievements/earned")
    public R<List<AchievementResponse>> getUserAchievements() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(achievementService.getUserAchievements(userId));
    }

    @Operation(summary = "获取最近获得的成就")
    @GetMapping("/achievements/recent")
    public R<List<AchievementResponse>> getRecentAchievements(
            @RequestParam(defaultValue = "5") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(achievementService.getRecentAchievements(userId, limit));
    }

    @Operation(summary = "获取成就详情")
    @GetMapping("/achievements/{achievementId}")
    public R<AchievementResponse> getAchievementDetail(@PathVariable Long achievementId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(achievementService.getAchievementDetail(achievementId, userId));
    }

    @Operation(summary = "获取成就积分")
    @GetMapping("/achievements/points")
    public R<Integer> getAchievementPoints() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(achievementService.getUserAchievementPoints(userId));
    }
}
