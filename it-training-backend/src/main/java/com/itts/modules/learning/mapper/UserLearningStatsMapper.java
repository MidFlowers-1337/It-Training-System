package com.itts.modules.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.learning.entity.UserLearningStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}