package com.itts.modules.plan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itts.modules.plan.dto.LearningPlanRequest;
import com.itts.modules.plan.dto.LearningPlanResponse;
import com.itts.modules.plan.entity.LearningPlan;

import java.util.List;

/**
 * 学习计划服务接口
 */
public interface LearningPlanService extends IService<LearningPlan> {

    /**
     * 创建学习计划
     * @param userId 用户ID
     * @param request 创建请求
     * @return 创建的计划
     */
    LearningPlanResponse createPlan(Long userId, LearningPlanRequest request);

    /**
     * 更新学习计划
     * @param userId 用户ID
     * @param planId 计划ID
     * @param request 更新请求
     * @return 更新后的计划
     */
    LearningPlanResponse updatePlan(Long userId, Long planId, LearningPlanRequest request);

    /**
     * 获取用户的学习计划列表
     * @param userId 用户ID
     * @return 计划列表
     */
    List<LearningPlanResponse> getUserPlans(Long userId);

    /**
     * 分页获取用户的学习计划列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 计划分页列表
     */
    IPage<LearningPlanResponse> getUserPlans(Long userId, int page, int size);

    /**
     * 获取用户当前进行中的计划
     * @param userId 用户ID
     * @return 进行中的计划
     */
    LearningPlanResponse getActivePlan(Long userId);

    /**
     * 获取计划详情
     * @param userId 用户ID
     * @param planId 计划ID
     * @return 计划详情
     */
    LearningPlanResponse getPlanDetail(Long userId, Long planId);

    /**
     * 暂停计划
     * @param userId 用户ID
     * @param planId 计划ID
     * @return 更新后的计划
     */
    LearningPlanResponse pausePlan(Long userId, Long planId);

    /**
     * 恢复计划
     * @param userId 用户ID
     * @param planId 计划ID
     * @return 更新后的计划
     */
    LearningPlanResponse resumePlan(Long userId, Long planId);

    /**
     * 取消计划
     * @param userId 用户ID
     * @param planId 计划ID
     * @return 更新后的计划
     */
    LearningPlanResponse cancelPlan(Long userId, Long planId);

    /**
     * 完成计划
     * @param userId 用户ID
     * @param planId 计划ID
     * @return 更新后的计划
     */
    LearningPlanResponse completePlan(Long userId, Long planId);

    /**
     * 检查并更新计划状态（定时任务调用）
     */
    void checkAndUpdatePlanStatus();
}