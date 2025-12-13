package com.itts.modules.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.modules.course.dto.CourseCreateRequest;
import com.itts.modules.course.dto.CourseResponse;
import com.itts.modules.course.dto.CourseUpdateRequest;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService {

    /**
     * 分页查询课程列表
     * @param page 页码
     * @param size 每页大小
     * @param category 分类过滤
     * @param status 状态过滤
     * @param keyword 搜索关键词
     * @return 课程分页列表
     */
    IPage<CourseResponse> listCourses(int page, int size, String category, Integer status, String keyword);

    /**
     * 获取所有已发布的课程（用于下拉选择）
     * @return 课程列表
     */
    List<CourseResponse> listPublishedCourses();

    /**
     * 根据ID获取课程
     * @param id 课程ID
     * @return 课程信息
     */
    CourseResponse getCourseById(Long id);

    /**
     * 创建课程
     * @param request 创建请求
     * @return 课程信息
     */
    CourseResponse createCourse(CourseCreateRequest request);

    /**
     * 更新课程
     * @param id 课程ID
     * @param request 更新请求
     * @return 课程信息
     */
    CourseResponse updateCourse(Long id, CourseUpdateRequest request);

    /**
     * 删除课程
     * @param id 课程ID
     */
    void deleteCourse(Long id);

    /**
     * 发布课程
     * @param id 课程ID
     */
    void publishCourse(Long id);

    /**
     * 下架课程
     * @param id 课程ID
     */
    void unpublishCourse(Long id);
}
