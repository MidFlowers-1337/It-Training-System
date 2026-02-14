package com.itts.modules.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.learning.entity.LearningProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    /**
     * [M4 性能优化] 按课程聚合完课率统计（替代全量加载）
     */
    @Select("SELECT lp.course_id AS courseId, c.name AS courseName, " +
            "COUNT(*) AS total, " +
            "SUM(CASE WHEN lp.status = 'completed' THEN 1 ELSE 0 END) AS completed " +
            "FROM learning_progress lp " +
            "LEFT JOIN course c ON lp.course_id = c.id " +
            "GROUP BY lp.course_id, c.name " +
            "ORDER BY completed / COUNT(*) DESC")
    List<Map<String, Object>> selectCompletionRateGroupByCourse();

    /**
     * [M6 性能优化] 按日期聚合活跃用户数（替代全量加载 progress）
     */
    @Select("SELECT DATE(updated_at) AS date, COUNT(DISTINCT user_id) AS activeCount " +
            "FROM learning_progress " +
            "WHERE updated_at >= #{startDateTime} " +
            "GROUP BY DATE(updated_at)")
    List<Map<String, Object>> countActiveUsersByDate(@Param("startDateTime") LocalDateTime startDateTime);
}