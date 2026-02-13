package com.itts.modules.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.recommend.entity.RecommendFeedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * 推荐反馈 Mapper
 */
@Mapper
public interface RecommendFeedbackMapper extends BaseMapper<RecommendFeedback> {
}
