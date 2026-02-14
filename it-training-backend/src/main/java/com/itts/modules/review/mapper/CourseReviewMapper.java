package com.itts.modules.review.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.review.entity.CourseReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 课程评价 Mapper
 */
@Mapper
public interface CourseReviewMapper extends BaseMapper<CourseReview> {

    /**
     * 获取课程平均评分
     */
    @Select("SELECT COALESCE(AVG(rating), 0) FROM course_review WHERE course_id = #{courseId}")
    Double selectAverageRatingByCourseId(@Param("courseId") Long courseId);

    /**
     * 获取课程评价总数
     */
    @Select("SELECT COUNT(*) FROM course_review WHERE course_id = #{courseId}")
    Long selectCountByCourseId(@Param("courseId") Long courseId);
}
