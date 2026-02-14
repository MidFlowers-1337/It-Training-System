package com.itts.modules.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.recommend.entity.UserPreference;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户学习偏好Mapper
 */
@Mapper
public interface UserPreferenceMapper extends BaseMapper<UserPreference> {
}