package com.itts.modules.course.service;

import com.itts.modules.course.dto.CourseChapterResponse;

import java.util.List;

/**
 * 课程章节服务接口
 */
public interface CourseChapterService {

    /**
     * 获取课程的所有章节
     *
     * @param courseId 课程ID
     * @param userId 用户ID（用于判断章节完成状态）
     * @return 章节列表
     */
    List<CourseChapterResponse> getCourseChapters(Long courseId, Long userId);

    /**
     * 获取章节详情
     *
     * @param chapterId 章节ID
     * @return 章节详情
     */
    CourseChapterResponse getChapterById(Long chapterId);

    /**
     * 标记章节为已完成
     *
     * @param chapterId 章节ID
     * @param userId 用户ID
     */
    void markChapterCompleted(Long chapterId, Long userId);
}
