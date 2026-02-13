package com.itts.modules.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.learning.entity.UserLearningStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;

/**
 * 用户学习统计 Mapper
 */
@Mapper
public interface UserLearningStatsMapper extends BaseMapper<UserLearningStats> {

    /**
     * 根据用户ID查询学习统计
     */
    @Select("SELECT * FROM user_learning_stats WHERE user_id = #{userId}")
    UserLearningStats selectByUserId(@Param("userId") Long userId);

    /**
     * 原子增加学习时长并更新最后学习日期
     */
    @Update("UPDATE user_learning_stats SET total_study_minutes = total_study_minutes + #{minutes}, " +
            "last_study_date = #{lastStudyDate} WHERE user_id = #{userId}")
    int atomicAddStudyMinutes(@Param("userId") Long userId,
                              @Param("minutes") int minutes,
                              @Param("lastStudyDate") LocalDate lastStudyDate);

    /**
     * 原子增加已完成课程数
     */
    @Update("UPDATE user_learning_stats SET total_courses_completed = total_courses_completed + 1 " +
            "WHERE user_id = #{userId}")
    int atomicIncrementCompletedCourses(@Param("userId") Long userId);

    /**
     * 原子增加已报名课程数
     */
    @Update("UPDATE user_learning_stats SET total_courses_enrolled = total_courses_enrolled + 1 " +
            "WHERE user_id = #{userId}")
    int atomicIncrementEnrolledCourses(@Param("userId") Long userId);

    /**
     * 原子增加成就积分
     */
    @Update("UPDATE user_learning_stats SET total_achievement_points = total_achievement_points + #{points} " +
            "WHERE user_id = #{userId}")
    int atomicAddAchievementPoints(@Param("userId") Long userId, @Param("points") int points);

    /**
     * 原子更新连续打卡天数（连续情况：+1 并更新 max）
     */
    @Update("UPDATE user_learning_stats SET " +
            "current_streak_days = current_streak_days + 1, " +
            "total_checkin_days = total_checkin_days + 1, " +
            "max_streak_days = GREATEST(max_streak_days, current_streak_days + 1), " +
            "last_study_date = #{today} " +
            "WHERE user_id = #{userId}")
    int atomicIncrementStreak(@Param("userId") Long userId, @Param("today") LocalDate today);

    /**
     * 原子重置连续打卡天数（断签情况：重置为1）
     */
    @Update("UPDATE user_learning_stats SET " +
            "current_streak_days = 1, " +
            "total_checkin_days = total_checkin_days + 1, " +
            "last_study_date = #{today} " +
            "WHERE user_id = #{userId}")
    int atomicResetStreak(@Param("userId") Long userId, @Param("today") LocalDate today);

    /**
     * 原子初始化首次打卡（首次学习情况）
     */
    @Update("UPDATE user_learning_stats SET " +
            "current_streak_days = 1, " +
            "max_streak_days = GREATEST(max_streak_days, 1), " +
            "total_checkin_days = total_checkin_days + 1, " +
            "last_study_date = #{today} " +
            "WHERE user_id = #{userId}")
    int atomicInitStreak(@Param("userId") Long userId, @Param("today") LocalDate today);
}