package com.itts.modules.achievement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.achievement.entity.UserAchievement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户成就 Mapper
 */
@Mapper
public interface UserAchievementMapper extends BaseMapper<UserAchievement> {

    /**
     * 根据用户ID查询已获得的成就
     */
    @Select("SELECT * FROM user_achievement WHERE user_id = #{userId} ORDER BY earned_at DESC")
    List<UserAchievement> selectByUserId(@Param("userId") Long userId);

    /**
     * 检查用户是否已获得某成就
     */
    @Select("SELECT COUNT(*) FROM user_achievement WHERE user_id = #{userId} AND achievement_id = #{achievementId}")
    int countByUserIdAndAchievementId(@Param("userId") Long userId, @Param("achievementId") Long achievementId);

    /**
     * 统计用户获得的成就总积分
     */
    @Select("SELECT COALESCE(SUM(a.points), 0) FROM user_achievement ua " +
            "JOIN achievement a ON ua.achievement_id = a.id " +
            "WHERE ua.user_id = #{userId}")
    int sumPointsByUserId(@Param("userId") Long userId);
}