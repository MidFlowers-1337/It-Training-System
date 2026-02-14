package com.itts.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.course.entity.Course;
import com.itts.modules.stats.dto.CourseHotItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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

    /**
     * 按课程分类聚合报名人数（SQL 层 GROUP BY，避免全量加载到内存）
     * LEFT JOIN 确保没有班期的分类也能返回（enrollmentCount = 0）
     *
     * @param publishedStatus 已发布状态码
     * @return category + enrollmentCount 的聚合结果
     */
    @Select("""
            SELECT c.category,
                   COALESCE(SUM(cs.current_enrollment), 0) AS enrollmentCount
            FROM course c
            LEFT JOIN class_session cs ON cs.course_id = c.id AND cs.deleted = 0
            WHERE c.status = #{publishedStatus} AND c.deleted = 0
            GROUP BY c.category
            ORDER BY enrollmentCount DESC
            """)
    List<Map<String, Object>> selectCategoryEnrollmentDistribution(@Param("publishedStatus") int publishedStatus);

    /**
     * 查询热门课程 ID 及其报名人数（SQL 层排序 + LIMIT，避免全量加载）
     * 按班期 current_enrollment 之和降序排列
     *
     * @param publishedStatus 已发布状态码
     * @param limit           返回数量限制
     * @return courseId + enrollmentCount
     */
    @Select("""
            SELECT c.id AS courseId,
                   COALESCE(SUM(cs.current_enrollment), 0) AS enrollmentCount
            FROM course c
            LEFT JOIN class_session cs ON cs.course_id = c.id AND cs.deleted = 0
            WHERE c.status = #{publishedStatus} AND c.deleted = 0
            GROUP BY c.id
            ORDER BY enrollmentCount DESC
            LIMIT #{limit}
            """)
    List<Map<String, Object>> selectTopPopularCourseIds(@Param("publishedStatus") int publishedStatus,
                                                         @Param("limit") int limit);
}
