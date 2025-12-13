package com.itts.modules.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.learning.entity.UserSkillTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户技能标签Mapper
 */
@Mapper
public interface UserSkillTagMapper extends BaseMapper<UserSkillTag> {
}