package com.itts.modules.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.learning.dto.LearningPlanRequest;
import com.itts.modules.learning.dto.LearningPlanResponse;
import com.itts.modules.learning.entity.LearningPlan;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.mapper.LearningPlanMapper;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.learning.service.LearningPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学习计划服务实现
 */
@Service
@RequiredArgsConstructor
public class LearningPlanServiceImpl extends ServiceImpl<LearningPlanMapper, LearningPlan>
        implements LearningPlanService {

    private final CourseMapper courseMapper;
    private final LearningProgressMapper progressMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public LearningPlanResponse createPlan(Long userId, LearningPlanRequest request) {
        // 检查是否已有进行中的计划
        LearningPlan activePlan = getOne(
            new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getUserId, userId)
                .eq(LearningPlan::getStatus, "active")
        );
        
        if (activePlan != null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "您已有进行中的学习计划，请先完成或取消当前计划");
        }
        
        // 验证日期
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "开始日期不能晚于结束日期");
        }
        
        LearningPlan plan = new LearningPlan();
        plan.setUserId(userId);
        plan.setPlanName(request.getPlanName());
        plan.setDescription(request.getDescription());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setWeeklyGoalHours(request.getDailyTargetMinutes() != null ? 
            request.getDailyTargetMinutes() * 7 / 60 : 5);
        plan.setStatus("active");
        
        // 转换课程ID列表为JSON
        if (request.getTargetCourseIds() != null && !request.getTargetCourseIds().isEmpty()) {
            try {
                plan.setTargetCourses(objectMapper.writeValueAsString(request.getTargetCourseIds()));
            } catch (JsonProcessingException e) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "课程列表序列化失败");
            }
        }
        
        save(plan);
        
        return convertToResponse(plan);
    }

    @Override
    @Transactional
    public LearningPlanResponse updatePlan(Long userId, Long planId, LearningPlanRequest request) {
        LearningPlan plan = getById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学习计划不存在");
        }
        
        if (!"active".equals(plan.getStatus()) && !"paused".equals(plan.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "只能修改进行中或暂停的计划");
        }
        
        if (request.getPlanName() != null) {
            plan.setPlanName(request.getPlanName());
        }
        if (request.getDescription() != null) {
            plan.setDescription(request.getDescription());
        }
        if (request.getStartDate() != null) {
            plan.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            plan.setEndDate(request.getEndDate());
        }
        if (request.getDailyTargetMinutes() != null) {
            plan.setWeeklyGoalHours(request.getDailyTargetMinutes() * 7 / 60);
        }
        if (request.getTargetCourseIds() != null) {
            try {
                plan.setTargetCourses(objectMapper.writeValueAsString(request.getTargetCourseIds()));
            } catch (JsonProcessingException e) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "课程列表序列化失败");
            }
        }
        
        updateById(plan);
        
        return convertToResponse(plan);
    }

    @Override
    public List<LearningPlanResponse> getUserPlans(Long userId) {
        List<LearningPlan> plans = list(
            new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getUserId, userId)
                .orderByDesc(LearningPlan::getCreatedAt)
        );
        
        return plans.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public LearningPlanResponse getActivePlan(Long userId) {
        LearningPlan plan = getOne(
            new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getUserId, userId)
                .eq(LearningPlan::getStatus, "active")
        );
        
        return plan != null ? convertToResponse(plan) : null;
    }

    @Override
    public LearningPlanResponse getPlanDetail(Long userId, Long planId) {
        LearningPlan plan = getById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学习计划不存在");
        }
        
        return convertToResponse(plan);
    }

    @Override
    @Transactional
    public LearningPlanResponse pausePlan(Long userId, Long planId) {
        LearningPlan plan = getById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学习计划不存在");
        }
        
        if (!"active".equals(plan.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "只能暂停进行中的计划");
        }
        
        plan.setStatus("paused");
        updateById(plan);
        
        return convertToResponse(plan);
    }

    @Override
    @Transactional
    public LearningPlanResponse resumePlan(Long userId, Long planId) {
        LearningPlan plan = getById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学习计划不存在");
        }
        
        if (!"paused".equals(plan.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "只能恢复暂停的计划");
        }
        
        // 检查是否已有其他进行中的计划
        LearningPlan activePlan = getOne(
            new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getUserId, userId)
                .eq(LearningPlan::getStatus, "active")
                .ne(LearningPlan::getId, planId)
        );
        
        if (activePlan != null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "您已有进行中的学习计划");
        }
        
        plan.setStatus("active");
        updateById(plan);
        
        return convertToResponse(plan);
    }

    @Override
    @Transactional
    public LearningPlanResponse cancelPlan(Long userId, Long planId) {
        LearningPlan plan = getById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学习计划不存在");
        }
        
        if ("completed".equals(plan.getStatus()) || "canceled".equals(plan.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "计划已完成或已取消");
        }
        
        plan.setStatus("canceled");
        updateById(plan);
        
        return convertToResponse(plan);
    }

    @Override
    @Transactional
    public LearningPlanResponse completePlan(Long userId, Long planId) {
        LearningPlan plan = getById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学习计划不存在");
        }
        
        if (!"active".equals(plan.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "只能完成进行中的计划");
        }
        
        plan.setStatus("completed");
        updateById(plan);
        
        return convertToResponse(plan);
    }

    @Override
    @Transactional
    public void checkAndUpdatePlanStatus() {
        LocalDate today = LocalDate.now();
        
        // 查找所有已过期但仍为 active 的计划
        List<LearningPlan> expiredPlans = list(
            new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getStatus, "active")
                .lt(LearningPlan::getEndDate, today)
        );
        
        for (LearningPlan plan : expiredPlans) {
            plan.setStatus("completed");
            updateById(plan);
        }
    }

    /**
     * 转换为响应DTO
     */
    private LearningPlanResponse convertToResponse(LearningPlan plan) {
        LearningPlanResponse response = new LearningPlanResponse();
        response.setId(plan.getId());
        response.setUserId(plan.getUserId());
        response.setPlanName(plan.getPlanName());
        response.setDescription(plan.getDescription());
        response.setStartDate(plan.getStartDate());
        response.setEndDate(plan.getEndDate());
        response.setDailyTargetMinutes(plan.getWeeklyGoalHours() != null ? 
            plan.getWeeklyGoalHours() * 60 / 7 : 0);
        response.setWeeklyTargetDays(5); // 默认每周5天
        response.setStatus(plan.getStatus());
        response.setCreatedAt(plan.getCreatedAt());
        
        // 计算剩余天数
        LocalDate today = LocalDate.now();
        if (plan.getEndDate() != null && plan.getEndDate().isAfter(today)) {
            response.setRemainingDays((int) ChronoUnit.DAYS.between(today, plan.getEndDate()));
        } else {
            response.setRemainingDays(0);
        }
        
        // 解析目标课程
        List<LearningPlanResponse.PlanCourseItem> targetCourses = new ArrayList<>();
        if (plan.getTargetCourses() != null && !plan.getTargetCourses().isEmpty()) {
            try {
                List<Long> courseIds = objectMapper.readValue(
                    plan.getTargetCourses(), 
                    new TypeReference<List<Long>>() {}
                );
                
                int completedCount = 0;
                for (Long courseId : courseIds) {
                    Course course = courseMapper.selectById(courseId);
                    if (course != null) {
                        LearningPlanResponse.PlanCourseItem item = new LearningPlanResponse.PlanCourseItem();
                        item.setCourseId(courseId);
                        item.setCourseName(course.getName());
                        item.setCategory(course.getCategory());
                        
                        // 获取学习进度
                        LearningProgress progress = progressMapper.selectOne(
                            new LambdaQueryWrapper<LearningProgress>()
                                .eq(LearningProgress::getUserId, plan.getUserId())
                                .eq(LearningProgress::getCourseId, courseId)
                        );
                        
                        if (progress != null) {
                            item.setProgressPercent(progress.getProgressPercent());
                            item.setCompleted("completed".equals(progress.getStatus()));
                            if (item.getCompleted()) {
                                completedCount++;
                            }
                        } else {
                            item.setProgressPercent(0);
                            item.setCompleted(false);
                        }
                        
                        targetCourses.add(item);
                    }
                }
                
                response.setCompletedCourses(completedCount);
                response.setTotalCourses(courseIds.size());
                
                // 计算进度百分比
                if (!courseIds.isEmpty()) {
                    response.setProgressPercent(completedCount * 100 / courseIds.size());
                } else {
                    response.setProgressPercent(0);
                }
                
            } catch (JsonProcessingException e) {
                // 忽略解析错误
            }
        } else {
            response.setCompletedCourses(0);
            response.setTotalCourses(0);
            response.setProgressPercent(0);
        }
        
        response.setTargetCourses(targetCourses);
        
        return response;
    }
}