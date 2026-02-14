package com.itts.modules.achievement.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 学习活动事件
 * <p>
 * 用于解耦学习模块内部的循环依赖（UserLearningStatsService -> AchievementService）。
 * 当学习统计数据更新后，发布此事件，由 AchievementService 异步监听并检查成就。
 */
@Getter
public class LearningActivityEvent extends ApplicationEvent {

    private final Long userId;

    /**
     * 活动类型：checkin / progress_update / study_time
     */
    private final String activityType;

    public LearningActivityEvent(Object source, Long userId, String activityType) {
        super(source);
        this.userId = userId;
        this.activityType = activityType;
    }
}
