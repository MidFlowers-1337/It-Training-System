package com.itts.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.course.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 课程 Mapper
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 根据课程编码查询课程
     */
    @Select("SELECT * FROM course WHERE code = #{code} AND deleted = 0")
    Course selectByCode(@Param("code") String code);

    /**
     * 检查课程编码是否存在
     */
    @Select("SELECT COUNT(*) FROM course WHERE code = #{code} AND deleted = 0")
    int countByCode(@Param("code") String code);

    /**
     * 检查课程是否有班期
     */
    @Select("SELECT COUNT(*) FROM class_session WHERE course_id = #{courseId} AND deleted = 0")
    int countSessionsByCourseId(@Param("courseId") Long courseId);
}
