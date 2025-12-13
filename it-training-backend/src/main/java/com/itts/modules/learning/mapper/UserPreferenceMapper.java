package com.itts.modules.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.learning.entity.UserPreference;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户学习偏好Mapper
 */
@Mapper
public interface UserPreferenceMapper extends BaseMapper<UserPreference> {
}