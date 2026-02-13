package com.itts.modules.course.service;

import com.itts.modules.course.dto.CourseChapterCreateRequest;
import com.itts.modules.course.dto.CourseChapterResponse;
import com.itts.modules.course.dto.CourseChapterUpdateRequest;

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
     * 创建章节
     *
     * @param courseId 课程ID
     * @param request 创建请求
     * @return 章节响应
     */
    CourseChapterResponse createChapter(Long courseId, CourseChapterCreateRequest request);

    /**
     * 更新章节
     *
     * @param chapterId 章节ID
     * @param request 更新请求
     * @return 章节响应
     */
    CourseChapterResponse updateChapter(Long chapterId, CourseChapterUpdateRequest request);

    /**
     * 删除章节
     *
     * @param chapterId 章节ID
     */
    void deleteChapter(Long chapterId);

    /**
     * 重新排序章节
     *
     * @param courseId 课程ID
     * @param chapterIds 按期望顺序排列的章节ID列表
     */
    void reorderChapters(Long courseId, List<Long> chapterIds);

    /**
     * 标记章节为已完成
     *
     * @param chapterId 章节ID
     * @param userId 用户ID
     */
    void markChapterCompleted(Long chapterId, Long userId);

    /**
     * 更新章节观看进度
     *
     * @param chapterId 章节ID
     * @param userId 用户ID
     * @param watchDuration 观看时长（秒）
     * @param lastPosition 最后观看位置（秒）
     */
    void updateChapterProgress(Long chapterId, Long userId, Integer watchDuration, Integer lastPosition);
}
