package com.itts.modules.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.recommend.entity.CourseSimilarity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程相似度Mapper
 */
@Mapper
public interface CourseSimilarityMapper extends BaseMapper<CourseSimilarity> {
}