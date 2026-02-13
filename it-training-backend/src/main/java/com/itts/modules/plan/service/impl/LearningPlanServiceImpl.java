package com.itts.modules.plan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.enums.LearningStatus;
import com.itts.enums.PlanStatus;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.plan.dto.LearningPlanRequest;
import com.itts.modules.plan.dto.LearningPlanResponse;
import com.itts.modules.plan.entity.LearningPlan;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.plan.mapper.LearningPlanMapper;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.plan.service.LearningPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 学习计划服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LearningPlanServiceImpl extends ServiceImpl<LearningPlanMapper, LearningPlan>
        implements LearningPlanService {

    private final CourseMapper courseMapper;
    private final LearningProgressMapper progressMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LearningPlanResponse createPlan(Long userId, LearningPlanRequest request) {
        // [M8 并发防护] 使用 SELECT ... FOR UPDATE 悲观锁检查活跃计划，防止并发创建
        LearningPlan activePlan = getBaseMapper()
                .selectByUserIdAndStatusForUpdate(userId, PlanStatus.ACTIVE.getCode());

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
        plan.setStatus(PlanStatus.ACTIVE.getCode());
        
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
    @Transactional(rollbackFor = Exception.class)
    public LearningPlanResponse updatePlan(Long userId, Long planId, LearningPlanRequest request) {
        LearningPlan plan = getById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学习计划不存在");
        }
        
        if (!PlanStatus.ACTIVE.getCode().equals(plan.getStatus()) && !PlanStatus.PAUSED.getCode().equals(plan.getStatus())) {
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

        // [m6 修复] 更新后统一校验日期区间
        if (plan.getStartDate() != null && plan.getEndDate() != null
                && plan.getStartDate().isAfter(plan.getEndDate())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "开始日期不能晚于结束日期");
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
    public IPage<LearningPlanResponse> getUserPlans(Long userId, int page, int size) {
        Page<LearningPlan> pageParam = new Page<>(page, size);

        IPage<LearningPlan> planPage = page(pageParam,
            new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getUserId, userId)
                .orderByDesc(LearningPlan::getCreatedAt)
        );

        return planPage.convert(this::convertToResponse);
    }

    @Override
    public LearningPlanResponse getActivePlan(Long userId) {
        LearningPlan plan = getOne(
            new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getUserId, userId)
                .eq(LearningPlan::getStatus, PlanStatus.ACTIVE.getCode())
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
    @Transactional(rollbackFor = Exception.class)
    public LearningPlanResponse pausePlan(Long userId, Long planId) {
        LearningPlan plan = getById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学习计划不存在");
        }
        
        if (!PlanStatus.ACTIVE.getCode().equals(plan.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "只能暂停进行中的计划");
        }
        
        plan.setStatus(PlanStatus.PAUSED.getCode());
        updateById(plan);
        
        return convertToResponse(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LearningPlanResponse resumePlan(Long userId, Long planId) {
        LearningPlan plan = getById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学习计划不存在");
        }
        
        if (!PlanStatus.PAUSED.getCode().equals(plan.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "只能恢复暂停的计划");
        }
        
        // 检查是否已有其他进行中的计划
        LearningPlan activePlan = getOne(
            new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getUserId, userId)
                .eq(LearningPlan::getStatus, PlanStatus.ACTIVE.getCode())
                .ne(LearningPlan::getId, planId)
        );
        
        if (activePlan != null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "您已有进行中的学习计划");
        }
        
        plan.setStatus(PlanStatus.ACTIVE.getCode());
        updateById(plan);
        
        return convertToResponse(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LearningPlanResponse cancelPlan(Long userId, Long planId) {
        LearningPlan plan = getById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学习计划不存在");
        }
        
        if (PlanStatus.COMPLETED.getCode().equals(plan.getStatus()) || PlanStatus.CANCELED.getCode().equals(plan.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "计划已完成或已取消");
        }
        
        plan.setStatus(PlanStatus.CANCELED.getCode());
        updateById(plan);
        
        return convertToResponse(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LearningPlanResponse completePlan(Long userId, Long planId) {
        LearningPlan plan = getById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学习计划不存在");
        }
        
        if (!PlanStatus.ACTIVE.getCode().equals(plan.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "只能完成进行中的计划");
        }
        
        plan.setStatus(PlanStatus.COMPLETED.getCode());
        updateById(plan);
        
        return convertToResponse(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAndUpdatePlanStatus() {
        LocalDate today = LocalDate.now();
        
        // 批量更新所有已过期但仍为 active 的计划，替代逐条 updateById
        LearningPlan updateEntity = new LearningPlan();
        updateEntity.setStatus(PlanStatus.COMPLETED.getCode());
        
        update(updateEntity,
            new LambdaQueryWrapper<LearningPlan>()
                .eq(LearningPlan::getStatus, PlanStatus.ACTIVE.getCode())
                .lt(LearningPlan::getEndDate, today)
        );
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

                // [Phase 4 #Arch-17] 批量查询课程和进度，消除 N+1 问题
                Map<Long, Course> courseMap = courseIds.isEmpty()
                    ? Collections.emptyMap()
                    : courseMapper.selectBatchIds(courseIds).stream()
                        .collect(Collectors.toMap(Course::getId, Function.identity()));

                List<LearningProgress> progressList = progressMapper.selectList(
                    new LambdaQueryWrapper<LearningProgress>()
                        .eq(LearningProgress::getUserId, plan.getUserId())
                        .in(LearningProgress::getCourseId, courseIds)
                );
                Map<Long, LearningProgress> progressMap = progressList.stream()
                    .collect(Collectors.toMap(LearningProgress::getCourseId, Function.identity(), (a, b) -> a));

                int completedCount = 0;
                for (Long courseId : courseIds) {
                    Course course = courseMap.get(courseId);
                    if (course != null) {
                        LearningPlanResponse.PlanCourseItem item = new LearningPlanResponse.PlanCourseItem();
                        item.setCourseId(courseId);
                        item.setCourseName(course.getName());
                        item.setCategory(course.getCategory());

                        // 获取学习进度（从批量查询结果）
                        LearningProgress progress = progressMap.get(courseId);

                        if (progress != null) {
                            item.setProgressPercent(progress.getProgressPercent());
                            item.setCompleted(LearningStatus.COMPLETED.getCode().equals(progress.getStatus()));
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
                // [m13 修复] 记录日志而非静默吞没，便于发现数据一致性问题
                log.error("解析学习计划目标课程JSON失败: planId={}, targetCourses={}",
                    plan.getId(), plan.getTargetCourses(), e);
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