package com.itts.modules.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.plan.entity.LearningPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 学习计划Mapper
 */
@Mapper
public interface LearningPlanMapper extends BaseMapper<LearningPlan> {

    /**
     * 使用悲观锁查询用户是否有处于指定状态的计划（防止并发创建多个 ACTIVE 计划）
     *
     * @param userId 用户ID
     * @param status 计划状态
     * @return 找到的计划（加行锁），若不存在返回 null
     */
    @Select("SELECT * FROM learning_plan WHERE user_id = #{userId} AND status = #{status} LIMIT 1 FOR UPDATE")
    LearningPlan selectByUserIdAndStatusForUpdate(@Param("userId") Long userId, @Param("status") String status);
}