package com.itts.modules.notification.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知响应 DTO
 */
@Data
public class NotificationResponse {

    private Long id;
    private String title;
    private String content;
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
