package com.itts.modules.student.service.impl;

import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.enrollment.entity.Enrollment;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import com.itts.modules.learning.dto.AchievementResponse;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.entity.UserLearningStats;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.learning.mapper.StudyCheckinMapper;
import com.itts.modules.learning.service.AchievementService;
import com.itts.modules.learning.service.UserLearningStatsService;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import com.itts.modules.student.dto.StudentDashboardResponse;
import com.itts.modules.student.dto.StudentStatsResponse;
import com.itts.modules.student.entity.UserLearningStreak;
import com.itts.modules.student.entity.UserLevel;
import com.itts.modules.student.mapper.UserChapterProgressMapper;
import com.itts.modules.student.mapper.UserLearningStreakMapper;
import com.itts.modules.student.mapper.UserLevelMapper;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * StudentService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("学生服务测试")
class StudentServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private UserLevelMapper userLevelMapper;

    @Mock
    private UserLearningStreakMapper userLearningStreakMapper;

    @Mock
    private UserChapterProgressMapper userChapterProgressMapper;

    @Mock
    private EnrollmentMapper enrollmentMapper;

    @Mock
    private ClassSessionMapper classSessionMapper;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private com.itts.modules.course.mapper.CourseChapterMapper courseChapterMapper;

    @Mock
    private LearningProgressMapper learningProgressMapper;

    @Mock
    private StudyCheckinMapper studyCheckinMapper;

    @Mock
    private AchievementService achievementService;

    @Mock
    private UserLearningStatsService userLearningStatsService;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Long testUserId;
    private SysUser testUser;
    private UserLevel testUserLevel;
    private UserLearningStreak testStreak;

    @BeforeEach
    void setUp() {
        testUserId = 1L;

        // 准备测试用户
        testUser = new SysUser();
        testUser.setId(testUserId);
        testUser.setUsername("testuser");
        testUser.setRealName("测试用户");
        testUser.setEmail("test@example.com");

        // 准备用户等级
        testUserLevel = new UserLevel();
        testUserLevel.setUserId(testUserId);
        testUserLevel.setLevel(5);
        testUserLevel.setExperience(450);
        testUserLevel.setTotalExperience(1450);

        // 准备打卡记录
        testStreak = new UserLearningStreak();
        testStreak.setUserId(testUserId);
        testStreak.setStreakDays(7);
        testStreak.setMaxStreakDays(15);
        testStreak.setTotalCheckinDays(30);
        testStreak.setLastCheckinDate(LocalDate.now());
    }

    @Test
    @DisplayName("应该成功获取Dashboard数据")
    void should_ReturnDashboard_When_UserExists() {
        // Given
        when(sysUserMapper.selectById(testUserId)).thenReturn(testUser);
        when(userLevelMapper.selectOne(any())).thenReturn(testUserLevel);
        when(userLearningStreakMapper.selectOne(any())).thenReturn(testStreak);
        when(studyCheckinMapper.selectOne(any())).thenReturn(null);
        when(learningProgressMapper.selectOne(any())).thenReturn(null);
        when(learningProgressMapper.selectList(any())).thenReturn(new ArrayList<>());
        when(enrollmentMapper.selectList(any())).thenReturn(new ArrayList<>());
        when(achievementService.getRecentAchievements(eq(testUserId), anyInt()))
            .thenReturn(new ArrayList<>());

        // When
        StudentDashboardResponse dashboard = studentService.getDashboard(testUserId);

        // Then
        assertThat(dashboard).isNotNull();
        assertThat(dashboard.getUserInfo()).isNotNull();
        assertThat(dashboard.getUserInfo().getUsername()).isEqualTo("testuser");
        assertThat(dashboard.getUserInfo().getLevel()).isEqualTo(5);
        assertThat(dashboard.getTodayStats()).isNotNull();
        assertThat(dashboard.getTodayStats().getStreakDays()).isEqualTo(7);

        verify(sysUserMapper).selectById(testUserId);
        verify(userLevelMapper).selectOne(any());
    }

    @Test
    @DisplayName("应该成功获取学习统计")
    void should_ReturnStats_When_UserExists() {
        // Given
        UserLearningStats learningStats = new UserLearningStats();
        learningStats.setUserId(testUserId);
        learningStats.setTotalStudyMinutes(1200);
        learningStats.setCurrentStreakDays(7);
        learningStats.setMaxStreakDays(15);
        learningStats.setTotalCheckinDays(30);
        learningStats.setTotalAchievementPoints(500);

        when(userLearningStatsService.getOrCreateStats(testUserId)).thenReturn(learningStats);
        when(userLearningStreakMapper.selectOne(any())).thenReturn(testStreak);
        when(enrollmentMapper.selectList(any())).thenReturn(new ArrayList<>());
        when(achievementService.getUserAchievements(testUserId)).thenReturn(createMockAchievements(5));
        when(userLevelMapper.selectOne(any())).thenReturn(testUserLevel);

        // When
        StudentStatsResponse stats = studentService.getStats(testUserId);

        // Then
        assertThat(stats).isNotNull();
        assertThat(stats.getTotalStudyMinutes()).isEqualTo(1200);
        assertThat(stats.getStreakDays()).isEqualTo(7);
        assertThat(stats.getMaxStreakDays()).isEqualTo(15);
        assertThat(stats.getTotalCheckinDays()).isEqualTo(30);
        assertThat(stats.getAchievementsEarned()).isEqualTo(5);
        assertThat(stats.getLevel()).isEqualTo(5);
        assertThat(stats.getExperience()).isEqualTo(450);

        verify(userLearningStatsService).getOrCreateStats(testUserId);
        verify(achievementService).getUserAchievements(testUserId);
    }

    @Test
    @DisplayName("应该成功添加经验值")
    void should_AddExperience_When_ValidExpProvided() {
        // Given
        when(userLevelMapper.selectOne(any())).thenReturn(testUserLevel);

        // When
        studentService.addExperience(testUserId, 50);

        // Then
        verify(userLevelMapper).selectOne(any());
        verify(userLevelMapper).updateById(argThat(level ->
            level.getExperience() == 500 && level.getTotalExperience() == 1500
        ));
    }

    @Test
    @DisplayName("应该在经验值为0时不执行任何操作")
    void should_DoNothing_When_ExpIsZero() {
        // When
        studentService.addExperience(testUserId, 0);

        // Then
        verify(userLevelMapper, never()).selectOne(any());
        verify(userLevelMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("应该在经验值为负数时不执行任何操作")
    void should_DoNothing_When_ExpIsNegative() {
        // When
        studentService.addExperience(testUserId, -10);

        // Then
        verify(userLevelMapper, never()).selectOne(any());
        verify(userLevelMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("应该在经验值足够时升级")
    void should_LevelUp_When_ExperienceIsEnough() {
        // Given
        testUserLevel.setLevel(1);
        testUserLevel.setExperience(80);
        when(userLevelMapper.selectOne(any())).thenReturn(testUserLevel);

        // When - 添加30经验，总共110，应该升到2级
        studentService.addExperience(testUserId, 30);

        // Then
        verify(userLevelMapper).updateById(argThat(level ->
            level.getLevel() == 2 && level.getExperience() == 110
        ));
    }

    @Test
    @DisplayName("应该成功检查并解锁成就")
    void should_CheckAchievements_When_Called() {
        // Given
        List<AchievementResponse> achievements = createMockAchievements(2);
        when(achievementService.checkAndGrantAchievements(testUserId)).thenReturn(achievements);

        // When
        studentService.checkAndUnlockAchievements(testUserId);

        // Then
        verify(achievementService).checkAndGrantAchievements(testUserId);
    }

    // 辅助方法：创建模拟成就列表
    private List<AchievementResponse> createMockAchievements(int count) {
        List<AchievementResponse> achievements = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            AchievementResponse achievement = new AchievementResponse();
            achievement.setId((long) (i + 1));
            achievement.setName("成就" + (i + 1));
            achievement.setDescription("测试成就");
            achievement.setIcon("🏆");
            achievement.setPoints(100);
            achievement.setEarned(true);
            achievement.setEarnedAt(LocalDateTime.now());
            achievements.add(achievement);
        }
        return achievements;
    }
}
