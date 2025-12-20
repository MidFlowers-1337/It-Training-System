package com.itts.modules.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.student.entity.UserChapterProgress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户章节进度 Mapper
 */
@Mapper
public interface UserChapterProgressMapper extends BaseMapper<UserChapterProgress> {
}
