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
import com.itts.modules.stats.dto.PublicStatsResponse;
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
    private final com.itts.modules.learning.mapper.LearningProgressMapper learningProgressMapper;
    private final com.itts.modules.checkin.mapper.StudyCheckinMapper studyCheckinMapper;

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

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        // 使用 SQL 聚合查询，避免全量加载报名记录到内存
        List<Map<String, Object>> dbResults = enrollmentMapper.countByDateRange(
                startDateTime, endDateTime, EnrollmentStatus.ENROLLED.getCode());

        // 将数据库聚合结果转为 Map<日期字符串, 数量>
        Map<String, Long> countMap = dbResults.stream()
                .collect(Collectors.toMap(
                        row -> row.get("date").toString(),
                        row -> ((Number) row.get("count")).longValue()
                ));

        // 填充所有日期（包括没有数据的日期显示为 0）
        List<EnrollmentTrendItem> result = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            String dateStr = current.toString(); // yyyy-MM-dd 格式，与 MySQL DATE() 返回格式一致
            result.add(new EnrollmentTrendItem(dateStr, countMap.getOrDefault(dateStr, 0L)));
            current = current.plusDays(1);
        }

        return result;
    }

    @Override
    @Cacheable(value = RedisCacheConfig.CACHE_STATS, key = "'public_stats'")
    public PublicStatsResponse getPublicStats() {
        PublicStatsResponse response = new PublicStatsResponse();

        // 课程总数（已发布且未删除）
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.eq(Course::getStatus, CourseStatus.PUBLISHED.getCode())
                .eq(Course::getDeleted, DeleteFlag.NOT_DELETED);
        response.setCourseCount(courseMapper.selectCount(courseWrapper));

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

        // 平均完课率（基于 learning_progress 表实际数据计算）
        LambdaQueryWrapper<com.itts.modules.learning.entity.LearningProgress> totalWrapper = new LambdaQueryWrapper<>();
        long totalCount = learningProgressMapper.selectCount(totalWrapper);

        if (totalCount == 0) {
            response.setCompletionRate(0.0);
        } else {
            LambdaQueryWrapper<com.itts.modules.learning.entity.LearningProgress> completedWrapper = new LambdaQueryWrapper<>();
            completedWrapper.eq(com.itts.modules.learning.entity.LearningProgress::getStatus, "completed");
            long completedCount = learningProgressMapper.selectCount(completedWrapper);
            double rate = Math.round(completedCount * 1000.0 / totalCount) / 10.0;
            response.setCompletionRate(rate);
        }

        return response;
    }

    @Override
    public List<com.itts.modules.stats.dto.UserActivityItem> getUserActivity(int days) {
        List<com.itts.modules.stats.dto.UserActivityItem> result = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);

        // [M6 性能优化] 使用 SQL 聚合替代全量加载
        // 1. 打卡活跃用户聚合
        List<Map<String, Object>> checkinActivity = studyCheckinMapper.countActiveUsersByDate(startDate, today);
        Map<String, Long> checkinMap = checkinActivity.stream()
            .collect(Collectors.toMap(
                row -> row.get("date").toString(),
                row -> ((Number) row.get("activeCount")).longValue()
            ));

        // 2. 学习进度活跃用户聚合
        LocalDateTime startDateTime = startDate.atStartOfDay();
        List<Map<String, Object>> progressActivity = learningProgressMapper.countActiveUsersByDate(startDateTime);
        Map<String, Long> progressMap = progressActivity.stream()
            .collect(Collectors.toMap(
                row -> row.get("date").toString(),
                row -> ((Number) row.get("activeCount")).longValue()
            ));

        // 3. 合并（取两者之和的近似值，因为无法在 SQL 层做跨表去重）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            long checkinCount = checkinMap.getOrDefault(dateStr, 0L);
            long progressCount = progressMap.getOrDefault(dateStr, 0L);
            // 取较大值作为活跃用户估算（避免简单相加导致重复计数）
            long activeCount = Math.max(checkinCount, progressCount);
            result.add(new com.itts.modules.stats.dto.UserActivityItem(dateStr, activeCount));
        }

        return result;
    }

    @Override
    public List<com.itts.modules.stats.dto.CourseCompletionRateItem> getCompletionRate() {
        // [M4 性能优化] 使用 SQL 聚合替代全量加载 learning_progress
        List<Map<String, Object>> dbResults = learningProgressMapper.selectCompletionRateGroupByCourse();

        List<com.itts.modules.stats.dto.CourseCompletionRateItem> result = new ArrayList<>();
        for (Map<String, Object> row : dbResults) {
            Long courseId = ((Number) row.get("courseId")).longValue();
            String courseName = row.get("courseName") != null ? row.get("courseName").toString() : "未知课程";
            long total = ((Number) row.get("total")).longValue();
            long completed = ((Number) row.get("completed")).longValue();
            double rate = total > 0 ? Math.round(completed * 1000.0 / total) / 10.0 : 0.0;

            result.add(new com.itts.modules.stats.dto.CourseCompletionRateItem(
                courseId, courseName, total, completed, rate));
        }

        return result;
    }

    @Override
    public List<com.itts.modules.stats.dto.CategoryDistributionItem> getCategoryDistribution() {
        // [S-02 性能优化] SQL 层 GROUP BY 聚合，替代全量加载课程+班期到内存
        List<Map<String, Object>> dbResults = courseMapper.selectCategoryEnrollmentDistribution(
                CourseStatus.PUBLISHED.getCode());

        List<com.itts.modules.stats.dto.CategoryDistributionItem> result = new ArrayList<>();
        for (Map<String, Object> row : dbResults) {
            String category = row.get("category") != null ? row.get("category").toString() : "OTHER";
            long enrollmentCount = ((Number) row.get("enrollmentCount")).longValue();

            // 枚举翻译（轻量操作，保留在 Java 侧）
            String categoryName;
            try {
                categoryName = com.itts.enums.CourseCategory.valueOf(category).getDesc();
            } catch (IllegalArgumentException e) {
                categoryName = category;
            }

            result.add(new com.itts.modules.stats.dto.CategoryDistributionItem(
                    category, categoryName, enrollmentCount));
        }

        return result;
    }

    @Override
    public String exportEnrollmentsCSV() {
        // 查询所有报名记录（关联课程名、学员名、班期编号）
        List<Enrollment> enrollments = enrollmentMapper.selectAllWithDetails();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        StringBuilder csv = new StringBuilder();
        // UTF-8 BOM 头，确保 Excel 正确识别中文编码
        csv.append("\uFEFF");
        // 表头
        csv.append("报名ID,学员用户名,学员姓名,课程名称,班期编号,报名时间\n");

        // 数据行
        for (Enrollment e : enrollments) {
            csv.append(e.getId()).append(",");
            csv.append(escapeCsvField(e.getUserName())).append(",");
            csv.append(escapeCsvField(e.getRealName())).append(",");
            csv.append(escapeCsvField(e.getCourseName())).append(",");
            csv.append(escapeCsvField(e.getSessionCode())).append(",");
            csv.append(e.getEnrolledAt() != null ? e.getEnrolledAt().format(dtf) : "").append("\n");
        }

        return csv.toString();
    }

    @Override
    public void exportEnrollmentsCSV(java.io.OutputStream out) throws java.io.IOException {
        // [M5 性能优化] 流式写入，避免全量 String 驻留内存
        List<Enrollment> enrollments = enrollmentMapper.selectAllWithDetails();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        java.io.BufferedWriter writer = new java.io.BufferedWriter(
            new java.io.OutputStreamWriter(out, java.nio.charset.StandardCharsets.UTF_8));

        // UTF-8 BOM 头
        writer.write("\uFEFF");
        // 表头
        writer.write("报名ID,学员用户名,学员姓名,课程名称,班期编号,报名时间\n");
        writer.flush();

        // 逐行写入并定期 flush，降低内存峰值
        int count = 0;
        for (Enrollment e : enrollments) {
            writer.write(String.valueOf(e.getId()));
            writer.write(",");
            writer.write(escapeCsvField(e.getUserName()));
            writer.write(",");
            writer.write(escapeCsvField(e.getRealName()));
            writer.write(",");
            writer.write(escapeCsvField(e.getCourseName()));
            writer.write(",");
            writer.write(escapeCsvField(e.getSessionCode()));
            writer.write(",");
            writer.write(e.getEnrolledAt() != null ? e.getEnrolledAt().format(dtf) : "");
            writer.write("\n");

            if (++count % 500 == 0) {
                writer.flush();
            }
        }
        writer.flush();
    }

    /**
     * CSV 字段转义：含逗号、引号、换行的字段用双引号包裹
     * 同时防御 CSV 公式注入（以 =, +, -, @, \t, \r 开头的内容加前缀单引号）
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        // 公式注入防护：以危险字符开头时，加前缀单引号使 Excel 将其视为文本
        if (!field.isEmpty()) {
            char first = field.charAt(0);
            if (first == '=' || first == '+' || first == '-' || first == '@'
                    || first == '\t' || first == '\r') {
                field = "'" + field;
            }
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("'")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}
