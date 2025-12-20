package com.itts.modules.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.student.entity.UserLearningStreak;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习打卡 Mapper
 */
@Mapper
public interface UserLearningStreakMapper extends BaseMapper<UserLearningStreak> {
}
