package com.itts.modules.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.learning.entity.LearningPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习计划Mapper
 */
@Mapper
public interface LearningPlanMapper extends BaseMapper<LearningPlan> {
}