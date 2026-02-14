package com.itts.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.course.entity.CourseChapter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 课程章节 Mapper
 */
@Mapper
public interface CourseChapterMapper extends BaseMapper<CourseChapter> {

    /**
     * 批量更新章节排序（单条 SQL，避免 N 次往返）
     *
     * @param chapters 包含 id 和 sortOrder 的章节列表
     */
    void batchUpdateSortOrder(List<CourseChapter> chapters);
}
