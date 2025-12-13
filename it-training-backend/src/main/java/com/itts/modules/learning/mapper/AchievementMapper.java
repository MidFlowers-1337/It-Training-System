package com.itts.modules.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.learning.entity.Achievement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 成就定义 Mapper
 */
@Mapper
public interface AchievementMapper extends BaseMapper<Achievement> {

    /**
     * 根据条件类型查询成就列表
     */
    @Select("SELECT * FROM achievement WHERE condition_type = #{conditionType} AND status = 1 ORDER BY condition_value")
    List<Achievement> selectByConditionType(@Param("conditionType") String conditionType);

    /**
     * 查询所有启用的成就
     */
    @Select("SELECT * FROM achievement WHERE status = 1 ORDER BY category, condition_value")
    List<Achievement> selectAllEnabled();

    /**
     * 根据编码查询成就
     */
    @Select("SELECT * FROM achievement WHERE code = #{code}")
    Achievement selectByCode(@Param("code") String code);
}