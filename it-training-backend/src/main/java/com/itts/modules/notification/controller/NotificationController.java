package com.itts.modules.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.notification.dto.NotificationResponse;
import com.itts.modules.notification.service.SystemNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知控制器
 */
@Tag(name = "通知中心", description = "用户通知相关接口")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final SystemNotificationService systemNotificationService;

    @Operation(summary = "获取通知列表（分页）")
    @GetMapping
    public R<IPage<NotificationResponse>> getNotifications(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<NotificationResponse> notifications = systemNotificationService.getUserNotifications(userId, page, size);
        return R.ok(notifications);
    }

    @Operation(summary = "获取未读通知数")
    @GetMapping("/unread-count")
    public R<Long> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        long count = systemNotificationService.getUnreadCount(userId);
        return R.ok(count);
    }

    @Operation(summary = "标记通知为已读")
    @PatchMapping("/{id}/read")
    public R<Void> markAsRead(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        systemNotificationService.markAsRead(userId, id);
        return R.ok();
    }

    @Operation(summary = "全部标记为已读")
    @PatchMapping("/read-all")
    public R<Void> markAllAsRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        systemNotificationService.markAllAsRead(userId);
        return R.ok();
    }
}
