package com.itts.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI推荐日志实体
 */
@Data
@TableName("ai_recommend_log")
public class AiRecommendLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 请求用户ID
     */
    private Long userId;

    /**
     * 用户输入的学习目标
     */
    private String inputText;

    /**
     * 推荐结果JSON（包含课程ID列表和顺序）
     */
    private String recommendedCourses;

    /**
     * 推荐理由
     */
    private String reason;

    /**
     * 使用的AI模型（或FALLBACK表示降级）
     */
    private String modelUsed;

    /**
     * 响应耗时(毫秒)
     */
    private Integer responseTimeMs;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
