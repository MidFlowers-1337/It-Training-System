package com.itts.modules.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程相似度实体
 */
@Data
@TableName("course_similarity")
public class CourseSimilarity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程A ID
     */
    private Long courseIdA;

    /**
     * 课程B ID
     */
    private Long courseIdB;

    /**
     * 相似度分数(0-1)
     */
    private BigDecimal similarityScore;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}