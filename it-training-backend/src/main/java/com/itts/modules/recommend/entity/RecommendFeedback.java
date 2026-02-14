package com.itts.modules.recommend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 推荐反馈实体
 */
@Data
@TableName("recommend_feedback")
public class RecommendFeedback implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 反馈类型: USEFUL / NOT_USEFUL
     */
    private String feedbackType;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
