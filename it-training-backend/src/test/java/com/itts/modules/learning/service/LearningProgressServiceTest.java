package com.itts.modules.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.common.BaseServiceTest;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.learning.dto.LearningProgressResponse;
import com.itts.modules.learning.dto.UpdateProgressRequest;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.entity.UserLearningStats;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.learning.service.impl.LearningProgressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 学习进度服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("学习进度服务测试")
class LearningProgressServiceTest extends BaseServiceTest {

    @Mock
    private LearningProgressMapper learningProgressMapper;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private UserLearningStatsService userLearningStatsService;

    @Mock
    private StudyCheckinService studyCheckinService;

    @Mock
    private AchievementService achievementService;

    @InjectMocks
    private LearningProgressServiceImpl learningProgressService;

    private Course testCourse;
    private LearningProgress testProgress;

    @BeforeEach
    void setUp() {
        // 初始化测试课程
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setName("Java 入门");
        testCourse.setCode("JAVA-001");
        testCourse.setCategory("BACKEND");
        testCourse.setDifficulty(1);
        testCourse.setCoverImage("/images/java.png");

        // 初始化测试学习进度
        testProgress = new LearningProgress();
        testProgress.setId(1L);
        testProgress.setUserId(1L);
        testProgress.setCourseId(1L);
        testProgress.setProgressPercent(50);
        testProgress.setStudyDurationMinutes(120);
        testProgress.setStatus("in_progress");
        testProgress.setLastStudyAt(LocalDateTime.now().minusDays(1));
    }

    @Test
    @DisplayName("获取用户学习进度列表 - 成功")
    void getUserProgress_Success() {
        // 准备模拟数据
        List<LearningProgress> progressList = Arrays.asList(testProgress);
        when(learningProgressMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(progressList);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);

        // 执行测试
        List<LearningProgressResponse> result = learningProgressService.getUserProgress(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java 入门", result.get(0).getCourseName());
        assertEquals(50, result.get(0).getProgressPercent());
        assertEquals("in_progress", result.get(0).getStatus());

        // 验证调用
        verify(learningProgressMapper).selectList(any(LambdaQueryWrapper.class));
        verify(courseMapper).selectById(1L);
    }

    @Test
    @DisplayName("获取用户学习进度列表 - 空列表")
    void getUserProgress_EmptyList() {
        // 准备模拟数据
        when(learningProgressMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList());

        // 执行测试
        List<LearningProgressResponse> result = learningProgressService.getUserProgress(1L);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取课程学习进度 - 成功")
    void getCourseProgress_Success() {
        // 准备模拟数据
        when(learningProgressMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testProgress);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);

        // 执行测试
        LearningProgressResponse result = learningProgressService.getCourseProgress(1L, 1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getCourseId());
        assertEquals(50, result.getProgressPercent());
        assertEquals("Java 入门", result.getCourseName());
    }

    @Test
    @DisplayName("获取课程学习进度 - 不存在")
    void getCourseProgress_NotFound() {
        // 准备模拟数据
        when(learningProgressMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(null);

        // 执行测试
        LearningProgressResponse result = learningProgressService.getCourseProgress(1L, 999L);

        // 验证结果
        assertNull(result);
    }

    @Test
    @DisplayName("初始化学习进度 - 成功创建新记录")
    void initProgress_CreateNew() {
        // 准备模拟数据 - 不存在记录
        when(learningProgressMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(null);
        when(learningProgressMapper.insert(any(LearningProgress.class))).thenReturn(1);

        // 执行测试
        LearningProgress result = learningProgressService.initProgress(1L, 1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getCourseId());
        assertEquals(0, result.getProgressPercent());
        assertEquals("not_started", result.getStatus());

        // 验证调用
        verify(learningProgressMapper).insert(any(LearningProgress.class));
    }

    @Test
    @DisplayName("初始化学习进度 - 已存在返回现有记录")
    void initProgress_ExistingRecord() {
        // 准备模拟数据 - 已存在记录
        when(learningProgressMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testProgress);

        // 执行测试
        LearningProgress result = learningProgressService.initProgress(1L, 1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(testProgress.getId(), result.getId());

        // 验证不会插入新记录
        verify(learningProgressMapper, never()).insert(any(LearningProgress.class));
    }

    @Test
    @DisplayName("更新学习进度 - 成功")
    void updateProgress_Success() {
        // 准备请求
        UpdateProgressRequest request = new UpdateProgressRequest();
        request.setCourseId(1L);
        request.setProgressPercent(75);
        request.setStudyMinutes(30);

        // 准备模拟数据
        when(learningProgressMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testProgress);
        when(learningProgressMapper.updateById(any(LearningProgress.class))).thenReturn(1);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);

        // 执行测试
        LearningProgressResponse result = learningProgressService.updateProgress(1L, request);

        // 验证结果
        assertNotNull(result);
        assertEquals(75, result.getProgressPercent());

        // 验证调用
        verify(userLearningStatsService).addStudyTime(1L, 30);
        verify(achievementService).checkAndGrantAchievements(1L);
    }

    @Test
    @DisplayName("更新学习进度 - 进度100%自动完成")
    void updateProgress_AutoComplete() {
        // 准备请求
        UpdateProgressRequest request = new UpdateProgressRequest();
        request.setCourseId(1L);
        request.setProgressPercent(100);

        // 准备模拟数据
        when(learningProgressMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testProgress);
        when(learningProgressMapper.updateById(any(LearningProgress.class))).thenReturn(1);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);

        // 执行测试
        LearningProgressResponse result = learningProgressService.updateProgress(1L, request);

        // 验证结果
        assertNotNull(result);
        assertEquals(100, result.getProgressPercent());

        // 验证调用 - 完成时更新完成课程数
        verify(userLearningStatsService).incrementCompletedCourses(1L);
        verify(achievementService).checkAndGrantAchievements(1L);
    }

    @Test
    @DisplayName("标记课程完成 - 成功")
    void markCompleted_Success() {
        // 准备模拟数据 - 进行中的课程
        testProgress.setStatus("in_progress");
        testProgress.setProgressPercent(80);

        when(learningProgressMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testProgress);
        when(learningProgressMapper.updateById(any(LearningProgress.class))).thenReturn(1);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);

        // 执行测试
        LearningProgressResponse result = learningProgressService.markCompleted(1L, 1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(100, result.getProgressPercent());

        // 验证调用
        verify(userLearningStatsService).incrementCompletedCourses(1L);
        verify(achievementService).checkAndGrantAchievements(1L);
    }

    @Test
    @DisplayName("标记课程完成 - 已完成不重复处理")
    void markCompleted_AlreadyCompleted() {
        // 准备模拟数据 - 已完成的课程
        testProgress.setStatus("completed");
        testProgress.setProgressPercent(100);
        testProgress.setCompletedAt(LocalDateTime.now().minusDays(7));

        when(learningProgressMapper.selectOne(any(LambdaQueryWrapper.class)))
                .thenReturn(testProgress);
        when(courseMapper.selectById(1L)).thenReturn(testCourse);

        // 执行测试
        LearningProgressResponse result = learningProgressService.markCompleted(1L, 1L);

        // 验证结果
        assertNotNull(result);

        // 验证不会重复更新
        verify(learningProgressMapper, never()).updateById(any(LearningProgress.class));
        verify(userLearningStatsService, never()).incrementCompletedCourses(any());
    }

    @Test
    @DisplayName("获取进行中课程列表 - 成功")
    void getInProgressCourses_Success() {
        // 准备模拟数据
        LearningProgress progress1 = new LearningProgress();
        progress1.setId(1L);
        progress1.setUserId(1L);
        progress1.setCourseId(1L);
        progress1.setProgressPercent(50);
        progress1.setStatus("in_progress");

        LearningProgress progress2 = new LearningProgress();
        progress2.setId(2L);
        progress2.setUserId(1L);
        progress2.setCourseId(2L);
        progress2.setProgressPercent(0);
        progress2.setStatus("not_started");

        List<LearningProgress> progressList = Arrays.asList(progress1, progress2);
        when(learningProgressMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(progressList);
        when(courseMapper.selectById(any())).thenReturn(testCourse);

        // 执行测试
        List<LearningProgressResponse> result = learningProgressService.getInProgressCourses(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取学习仪表盘 - 成功")
    void getDashboard_Success() {
        // 准备模拟数据
        UserLearningStats stats = new UserLearningStats();
        stats.setUserId(1L);
        stats.setTotalStudyMinutes(600);
        stats.setTotalCoursesEnrolled(5);
        stats.setTotalCoursesCompleted(2);
        stats.setCurrentStreakDays(7);
        stats.setMaxStreakDays(15);
        stats.setTotalCheckinDays(30);
        stats.setTotalAchievementPoints(100);

        when(userLearningStatsService.getOrCreateStats(1L)).thenReturn(stats);
        when(studyCheckinService.isTodayCheckedIn(1L)).thenReturn(true);
        when(learningProgressMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList());
        when(achievementService.getRecentAchievements(1L, 5)).thenReturn(Arrays.asList());
        when(studyCheckinService.getCheckinHistory(eq(1L), any(), any()))
                .thenReturn(Arrays.asList());

        // 执行测试
        var result = learningProgressService.getDashboard(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(600, result.getTotalStudyMinutes());
        assertEquals(5, result.getTotalCoursesEnrolled());
        assertEquals(2, result.getTotalCoursesCompleted());
        assertEquals(7, result.getCurrentStreakDays());
        assertTrue(result.getTodayCheckedIn());
    }
}
