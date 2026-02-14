package com.itts.modules.achievement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.common.response.R;
import com.itts.modules.achievement.dto.AchievementAdminResponse;
import com.itts.modules.achievement.dto.AchievementCreateRequest;
import com.itts.modules.achievement.dto.AchievementUpdateRequest;
import com.itts.modules.achievement.entity.Achievement;
import com.itts.modules.achievement.service.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 成就管理后台控制器（管理员专属）
 */
@Slf4j
@Tag(name = "成就管理后台", description = "管理员管理成就定义")
@RestController
@RequestMapping("/api/v1/admin/achievements")
@RequiredArgsConstructor
public class AchievementAdminController {

    private final AchievementService achievementService;

    @Operation(summary = "分页查询所有成就")
    @GetMapping
    public R<IPage<AchievementAdminResponse>> listAchievements(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "分类过滤") @RequestParam(required = false) String category,
            @Parameter(description = "状态过滤") @RequestParam(required = false) Integer status) {

        Page<Achievement> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<Achievement> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Achievement::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(Achievement::getStatus, status);
        }
        wrapper.orderByAsc(Achievement::getCategory)
                .orderByAsc(Achievement::getConditionValue);

        IPage<Achievement> achievementPage = achievementService.page(pageParam, wrapper);

        return R.ok(achievementPage.convert(this::convertToAdminResponse));
    }

    @Operation(summary = "获取成就详情")
    @GetMapping("/{id}")
    public R<AchievementAdminResponse> getAchievement(@PathVariable Long id) {
        Achievement achievement = achievementService.getById(id);
        if (achievement == null) {
            throw new BusinessException(ErrorCode.ACHIEVEMENT_NOT_FOUND);
        }
        return R.ok(convertToAdminResponse(achievement));
    }

    @Operation(summary = "创建成就")
    @PostMapping
    public R<AchievementAdminResponse> createAchievement(@Valid @RequestBody AchievementCreateRequest request) {
        log.info("创建成就: code={}, name={}", request.getCode(), request.getName());

        // 检查编码是否重复
        long count = achievementService.count(
            new LambdaQueryWrapper<Achievement>().eq(Achievement::getCode, request.getCode())
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.ACHIEVEMENT_CODE_EXISTS);
        }

        Achievement achievement = new Achievement();
        achievement.setCode(request.getCode());
        achievement.setName(request.getName());
        achievement.setDescription(request.getDescription());
        achievement.setIconUrl(request.getIconUrl());
        achievement.setCategory(request.getCategory());
        achievement.setConditionType(request.getConditionType());
        achievement.setConditionValue(request.getConditionValue());
        achievement.setPoints(request.getPoints());
        achievement.setStatus(1); // 默认启用
        achievement.setCreatedAt(LocalDateTime.now());
        achievement.setUpdatedAt(LocalDateTime.now());

        achievementService.save(achievement);

        log.info("成就创建成功: id={}, code={}", achievement.getId(), achievement.getCode());
        return R.ok(convertToAdminResponse(achievement));
    }

    @Operation(summary = "更新成就")
    @PutMapping("/{id}")
    public R<AchievementAdminResponse> updateAchievement(
            @PathVariable Long id,
            @Valid @RequestBody AchievementUpdateRequest request) {
        log.info("更新成就: id={}", id);

        Achievement achievement = achievementService.getById(id);
        if (achievement == null) {
            throw new BusinessException(ErrorCode.ACHIEVEMENT_NOT_FOUND);
        }

        // 只更新非空字段
        if (request.getName() != null) {
            achievement.setName(request.getName());
        }
        if (request.getDescription() != null) {
            achievement.setDescription(request.getDescription());
        }
        if (request.getIconUrl() != null) {
            achievement.setIconUrl(request.getIconUrl());
        }
        if (request.getCategory() != null) {
            achievement.setCategory(request.getCategory());
        }
        if (request.getConditionType() != null) {
            achievement.setConditionType(request.getConditionType());
        }
        if (request.getConditionValue() != null) {
            achievement.setConditionValue(request.getConditionValue());
        }
        if (request.getPoints() != null) {
            achievement.setPoints(request.getPoints());
        }
        if (request.getStatus() != null) {
            achievement.setStatus(request.getStatus());
        }

        achievement.setUpdatedAt(LocalDateTime.now());
        achievementService.updateById(achievement);

        log.info("成就更新成功: id={}", id);
        return R.ok(convertToAdminResponse(achievement));
    }

    @Operation(summary = "删除成就")
    @DeleteMapping("/{id}")
    public R<Void> deleteAchievement(@PathVariable Long id) {
        log.info("删除成就: id={}", id);

        Achievement achievement = achievementService.getById(id);
        if (achievement == null) {
            throw new BusinessException(ErrorCode.ACHIEVEMENT_NOT_FOUND);
        }

        // 软删除：将状态设为禁用
        achievement.setStatus(0);
        achievement.setUpdatedAt(LocalDateTime.now());
        achievementService.updateById(achievement);

        log.info("成就已禁用: id={}", id);
        return R.ok();
    }

    private AchievementAdminResponse convertToAdminResponse(Achievement achievement) {
        AchievementAdminResponse response = new AchievementAdminResponse();
        response.setId(achievement.getId());
        response.setCode(achievement.getCode());
        response.setName(achievement.getName());
        response.setDescription(achievement.getDescription());
        response.setIconUrl(achievement.getIconUrl());
        response.setCategory(achievement.getCategory());
        response.setConditionType(achievement.getConditionType());
        response.setConditionValue(achievement.getConditionValue());
        response.setPoints(achievement.getPoints());
        response.setStatus(achievement.getStatus());
        response.setCreatedAt(achievement.getCreatedAt());
        response.setUpdatedAt(achievement.getUpdatedAt());
        return response;
    }
}
