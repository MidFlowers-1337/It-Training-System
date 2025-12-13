package com.itts.modules.stats.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    public StatsOverviewResponse getOverview() {
        StatsOverviewResponse response = new StatsOverviewResponse();

        // 课程总数（已上架）
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.eq(Course::getStatus, 1).eq(Course::getDeleted, 0);
        response.setCourseCount(courseMapper.selectCount(courseWrapper));

        // 班期总数
        LambdaQueryWrapper<ClassSession> sessionWrapper = new LambdaQueryWrapper<>();
        sessionWrapper.eq(ClassSession::getDeleted, 0);
        response.setSessionCount(classSessionMapper.selectCount(sessionWrapper));

        // 进行中的班期数
        LambdaQueryWrapper<ClassSession> activeSessionWrapper = new LambdaQueryWrapper<>();
        activeSessionWrapper.eq(ClassSession::getDeleted, 0)
                .in(ClassSession::getStatus, SessionStatus.ENROLLING.getCode(), SessionStatus.IN_PROGRESS.getCode());
        response.setActiveSessionCount(classSessionMapper.selectCount(activeSessionWrapper));

        // 学员总数
        LambdaQueryWrapper<SysUser> studentWrapper = new LambdaQueryWrapper<>();
        studentWrapper.eq(SysUser::getRole, RoleEnum.STUDENT.name())
                .eq(SysUser::getDeleted, 0);
        response.setStudentCount(sysUserMapper.selectCount(studentWrapper));

        // 讲师总数
        LambdaQueryWrapper<SysUser> instructorWrapper = new LambdaQueryWrapper<>();
        instructorWrapper.eq(SysUser::getRole, RoleEnum.INSTRUCTOR.name())
                .eq(SysUser::getDeleted, 0);
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
    public List<CourseHotItem> getCourseHotRanking(int limit) {
        if (limit <= 0) limit = 10;
        if (limit > 50) limit = 50;

        // 查询所有有效报名
        LambdaQueryWrapper<Enrollment> enrollmentWrapper = new LambdaQueryWrapper<>();
        enrollmentWrapper.eq(Enrollment::getStatus, EnrollmentStatus.ENROLLED.getCode());
        List<Enrollment> enrollments = enrollmentMapper.selectList(enrollmentWrapper);

        // 获取班期ID -> 课程ID映射
        List<Long> sessionIds = enrollments.stream()
                .map(Enrollment::getSessionId)
                .distinct()
                .collect(Collectors.toList());

        if (sessionIds.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<ClassSession> sessionWrapper = new LambdaQueryWrapper<>();
        sessionWrapper.in(ClassSession::getId, sessionIds);
        List<ClassSession> sessions = classSessionMapper.selectList(sessionWrapper);

        Map<Long, Long> sessionToCourse = sessions.stream()
                .collect(Collectors.toMap(ClassSession::getId, ClassSession::getCourseId));

        // 统计每门课程的报名数
        Map<Long, Long> courseEnrollments = new HashMap<>();
        for (Enrollment enrollment : enrollments) {
            Long courseId = sessionToCourse.get(enrollment.getSessionId());
            if (courseId != null) {
                courseEnrollments.merge(courseId, 1L, Long::sum);
            }
        }

        // 获取课程信息
        if (courseEnrollments.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.in(Course::getId, courseEnrollments.keySet());
        List<Course> courses = courseMapper.selectList(courseWrapper);

        Map<Long, Course> courseMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        // 构建排行结果
        List<CourseHotItem> result = courseEnrollments.entrySet().stream()
                .map(entry -> {
                    Course course = courseMap.get(entry.getKey());
                    if (course == null) return null;
                    return new CourseHotItem(
                            course.getId(),
                            course.getName(),
                            course.getCategory(),
                            entry.getValue()
                    );
                })
                .filter(item -> item != null)
                .sorted((a, b) -> Long.compare(b.getEnrollmentCount(), a.getEnrollmentCount()))
                .limit(limit)
                .collect(Collectors.toList());

        return result;
    }

    @Override
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
