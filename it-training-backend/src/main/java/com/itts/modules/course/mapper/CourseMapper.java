package com.itts.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.course.entity.Course;
import com.itts.modules.stats.dto.CourseHotItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    /**
     * 查询课程热度排行（一次JOIN查询，避免N+1问题）
     * 关联 enrollment -> class_session -> course 三表
     */
    @Select("""
            SELECT c.id AS courseId, c.name AS courseName, c.category, COUNT(e.id) AS enrollmentCount
            FROM enrollment e
            JOIN class_session cs ON e.session_id = cs.id AND cs.deleted = 0
            JOIN course c ON cs.course_id = c.id AND c.deleted = 0
            WHERE e.status = #{enrolledStatus}
            GROUP BY c.id, c.name, c.category
            ORDER BY enrollmentCount DESC
            LIMIT #{limit}
            """)
    List<CourseHotItem> selectCourseHotRanking(@Param("enrolledStatus") int enrolledStatus, @Param("limit") int limit);
}
