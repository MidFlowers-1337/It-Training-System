package com.itts.modules.stats.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.common.config.RedisCacheConfig;
import com.itts.enums.CourseStatus;
import com.itts.enums.DeleteFlag;
import com.itts.enums.EnrollmentStatus;
import com.itts.enums.RoleEnum;
import com.itts.enums.SessionStatus;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.enrollment.entity.Enrollment;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import com.itts.modules.stats.dto.CourseHotItem;
import com.itts.modules.stats.dto.EnrollmentTrendItem;
import com.itts.modules.stats.dto.StatsOverviewResponse;
import com.itts.modules.stats.service.StatsService;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final CourseMapper courseMapper;
    private final ClassSessionMapper classSessionMapper;
    private final SysUserMapper sysUserMapper;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    @Cacheable(value = RedisCacheConfig.CACHE_STATS, key = "'overview'")
    public StatsOverviewResponse getOverview() {
        StatsOverviewResponse response = new StatsOverviewResponse();

        // 课程总数（已上架）
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.eq(Course::getStatus, CourseStatus.PUBLISHED.getCode())
                .eq(Course::getDeleted, DeleteFlag.NOT_DELETED);
        response.setCourseCount(courseMapper.selectCount(courseWrapper));

        // 班期总数
        LambdaQueryWrapper<ClassSession> sessionWrapper = new LambdaQueryWrapper<>();
        sessionWrapper.eq(ClassSession::getDeleted, DeleteFlag.NOT_DELETED);
        response.setSessionCount(classSessionMapper.selectCount(sessionWrapper));

        // 进行中的班期数
        LambdaQueryWrapper<ClassSession> activeSessionWrapper = new LambdaQueryWrapper<>();
        activeSessionWrapper.eq(ClassSession::getDeleted, DeleteFlag.NOT_DELETED)
                .in(ClassSession::getStatus, SessionStatus.ENROLLING.getCode(), SessionStatus.IN_PROGRESS.getCode());
        response.setActiveSessionCount(classSessionMapper.selectCount(activeSessionWrapper));

        // 学员总数
        LambdaQueryWrapper<SysUser> studentWrapper = new LambdaQueryWrapper<>();
        studentWrapper.eq(SysUser::getRole, RoleEnum.STUDENT.name())
                .eq(SysUser::getDeleted, DeleteFlag.NOT_DELETED);
        response.setStudentCount(sysUserMapper.selectCount(studentWrapper));

        // 讲师总数
        LambdaQueryWrapper<SysUser> instructorWrapper = new LambdaQueryWrapper<>();
        instructorWrapper.eq(SysUser::getRole, RoleEnum.INSTRUCTOR.name())
                .eq(SysUser::getDeleted, DeleteFlag.NOT_DELETED);
        response.setInstructorCount(sysUserMapper.selectCount(instructorWrapper));

        // 报名总数（有效报名）
        LambdaQueryWrapper<Enrollment> enrollmentWrapper = new LambdaQueryWrapper<>();
        enrollmentWrapper.eq(Enrollment::getStatus, EnrollmentStatus.ENROLLED.getCode());
        response.setEnrollmentCount(enrollmentMapper.selectCount(enrollmentWrapper));

        // 本月报名数
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LambdaQueryWrapper<Enrollment> monthlyWrapper = new LambdaQueryWrapper<>();
        monthlyWrapper.eq(Enrollment::getStatus, EnrollmentStatus.ENROLLED.getCode())
                .ge(Enrollment::getEnrolledAt, monthStart);
        response.setMonthlyEnrollmentCount(enrollmentMapper.selectCount(monthlyWrapper));

        return response;
    }

    @Override
    @Cacheable(value = RedisCacheConfig.CACHE_HOT_RANKING, key = "'top_' + #limit")
    public List<CourseHotItem> getCourseHotRanking(int limit) {
        if (limit <= 0) limit = 10;
        if (limit > 50) limit = 50;

        // 使用一次JOIN查询获取课程热度排行（优化：避免N+1问题）
        return courseMapper.selectCourseHotRanking(EnrollmentStatus.ENROLLED.getCode(), limit);
    }

    @Override
    @Cacheable(value = RedisCacheConfig.CACHE_STATS, key = "'trend_' + #days")
    public List<EnrollmentTrendItem> getEnrollmentTrend(int days) {
        if (days <= 0) days = 7;
        if (days > 90) days = 90;

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        // 查询时间范围内的报名
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(Enrollment::getEnrolledAt, startDate.atStartOfDay())
                .le(Enrollment::getEnrolledAt, endDate.plusDays(1).atStartOfDay())
                .eq(Enrollment::getStatus, EnrollmentStatus.ENROLLED.getCode());

        List<Enrollment> enrollments = enrollmentMapper.selectList(wrapper);

        // 按日期统计
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> dailyCounts = new HashMap<>();

        // 初始化所有日期
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            dailyCounts.put(date.format(formatter), 0L);
        }

        // 统计每日报名数
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getEnrolledAt() != null) {
                String dateStr = enrollment.getEnrolledAt().toLocalDate().format(formatter);
                dailyCounts.merge(dateStr, 1L, Long::sum);
            }
        }

        // 转换为列表并排序
        List<EnrollmentTrendItem> result = dailyCounts.entrySet().stream()
                .map(entry -> new EnrollmentTrendItem(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());

        return result;
    }
}
