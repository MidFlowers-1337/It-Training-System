package com.itts.modules.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.student.entity.UserLearningStreak;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;

/**
 * 学习打卡 Mapper
 */
@Mapper
public interface UserLearningStreakMapper extends BaseMapper<UserLearningStreak> {

    /**
     * 原子更新连续打卡（连续打卡场景：last_checkin_date 恰好是昨天）
     * 使用 CAS 条件防止并发丢失更新
     *
     * @return 影响行数（0 表示条件不匹配，说明并发已被其他请求处理）
     */
    @Update("UPDATE user_learning_streak SET " +
            "streak_days = streak_days + 1, " +
            "max_streak_days = GREATEST(max_streak_days, streak_days + 1), " +
            "total_checkin_days = total_checkin_days + 1, " +
            "last_checkin_date = #{today}, " +
            "updated_at = NOW() " +
            "WHERE user_id = #{userId} AND last_checkin_date = #{yesterday}")
    int atomicIncrementStreak(@Param("userId") Long userId,
                              @Param("today") LocalDate today,
                              @Param("yesterday") LocalDate yesterday);

    /**
     * 原子更新断签重置（last_checkin_date 不是昨天，重置连续天数为 1）
     */
    @Update("UPDATE user_learning_streak SET " +
            "streak_days = 1, " +
            "total_checkin_days = total_checkin_days + 1, " +
            "last_checkin_date = #{today}, " +
            "updated_at = NOW() " +
            "WHERE user_id = #{userId} AND last_checkin_date != #{today}")
    int atomicResetStreak(@Param("userId") Long userId,
                          @Param("today") LocalDate today);
}
