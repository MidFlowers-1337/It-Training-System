package com.itts.modules.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.modules.notification.dto.NotificationResponse;

/**
 * 系统通知服务接口
 */
public interface SystemNotificationService {

    /**
     * 发送通知
     *
     * @param userId  用户ID
     * @param title   标题
     * @param content 内容
     * @param type    类型: SYSTEM/ACHIEVEMENT/ENROLLMENT/COURSE
     */
    void sendNotification(Long userId, String title, String content, String type);

    /**
     * 获取用户通知列表（分页）
     *
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 通知分页
     */
    IPage<NotificationResponse> getUserNotifications(Long userId, int page, int size);

    /**
     * 获取未读通知数
     *
     * @param userId 用户ID
     * @return 未读数
     */
    long getUnreadCount(Long userId);

    /**
     * 标记单条通知为已读
     *
     * @param userId         用户ID
     * @param notificationId 通知ID
     */
    void markAsRead(Long userId, Long notificationId);

    /**
     * 标记所有通知为已读
     *
     * @param userId 用户ID
     */
    void markAllAsRead(Long userId);
}
