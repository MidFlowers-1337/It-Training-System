package com.itts.modules.learning.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itts.modules.learning.dto.LearningDashboardResponse;
import com.itts.modules.learning.dto.LearningProgressResponse;
import com.itts.modules.learning.dto.UpdateProgressRequest;
import com.itts.modules.learning.entity.LearningProgress;

import java.util.List;

/**
 * 学习进度服务接口
 */
public interface LearningProgressService extends IService<LearningProgress> {

    /**
     * 获取用户学习仪表盘数据
     * @param userId 用户ID
     * @return 仪表盘数据
     */
    LearningDashboardResponse getDashboard(Long userId);

    /**
     * 获取用户所有课程的学习进度
     * @param userId 用户ID
     * @return 进度列表
     */
    List<LearningProgressResponse> getUserProgress(Long userId);

    /**
     * 分页获取用户所有课程的学习进度
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 进度分页列表
     */
    IPage<LearningProgressResponse> getUserProgress(Long userId, int page, int size);

    /**
     * 获取用户某课程的学习进度
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 进度详情
     */
    LearningProgressResponse getCourseProgress(Long userId, Long courseId);

    /**
     * 更新学习进度
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新后的进度
     */
    LearningProgressResponse updateProgress(Long userId, UpdateProgressRequest request);

    /**
     * 初始化用户课程进度（报名时调用）
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 初始化的进度
     */
    LearningProgress initProgress(Long userId, Long courseId);

    /**
     * 标记课程完成
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 更新后的进度
     */
    LearningProgressResponse markCompleted(Long userId, Long courseId);

    /**
     * 获取进行中的课程列表
     * @param userId 用户ID
     * @return 进行中的课程进度列表
     */
    List<LearningProgressResponse> getInProgressCourses(Long userId);

    /**
     * 分页获取进行中的课程列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 进行中的课程进度分页列表
     */
    IPage<LearningProgressResponse> getInProgressCourses(Long userId, int page, int size);
}