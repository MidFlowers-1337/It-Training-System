package com.itts.modules.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户章节进度实体
 */
@Data
@TableName("user_chapter_progress")
public class UserChapterProgress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 章节ID
     */
    private Long chapterId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 观看时长（秒）
     */
    private Integer watchDuration;

    /**
     * 最后观看位置（秒）
     */
    private Integer lastPosition;

    /**
     * 是否完成: 0-未完成, 1-已完成
     */
    private Integer completed;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
