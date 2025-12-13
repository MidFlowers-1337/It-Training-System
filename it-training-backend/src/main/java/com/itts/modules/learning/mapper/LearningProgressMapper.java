package com.itts.modules.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.learning.entity.LearningProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学习进度 Mapper
 */
@Mapper
public interface LearningProgressMapper extends BaseMapper<LearningProgress> {

    /**
     * 根据用户ID查询学习进度列表
     */
    @Select("SELECT * FROM learning_progress WHERE user_id = #{userId} ORDER BY updated_at DESC")
    List<LearningProgress> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据报名ID查询学习进度
     */
    @Select("SELECT * FROM learning_progress WHERE enrollment_id = #{enrollmentId}")
    LearningProgress selectByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    /**
     * 统计用户已完成课程数
     */
    @Select("SELECT COUNT(*) FROM learning_progress WHERE user_id = #{userId} AND status = 'completed'")
    int countCompletedByUserId(@Param("userId") Long userId);

    /**
     * 统计用户总学习时长（分钟）
     */
    @Select("SELECT COALESCE(SUM(study_duration_minutes), 0) FROM learning_progress WHERE user_id = #{userId}")
    int sumStudyMinutesByUserId(@Param("userId") Long userId);
}