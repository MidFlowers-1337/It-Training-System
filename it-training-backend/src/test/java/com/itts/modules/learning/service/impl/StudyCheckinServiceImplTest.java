package com.itts.modules.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.learning.dto.AchievementResponse;
import com.itts.modules.learning.dto.StudyCheckinRequest;
import com.itts.modules.learning.dto.StudyCheckinResponse;
import com.itts.modules.learning.entity.StudyCheckin;
import com.itts.modules.learning.mapper.StudyCheckinMapper;
import com.itts.modules.learning.service.AchievementService;
import com.itts.modules.learning.service.UserLearningStatsService;
import com.itts.modules.student.entity.UserLearningStreak;
import com.itts.modules.student.mapper.UserLearningStreakMapper;
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
 * StudyCheckinService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("学习打卡服务测试")
class StudyCheckinServiceImplTest {

    @Mock
    private StudyCheckinMapper studyCheckinMapper;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private UserLearningStatsService userLearningStatsService;

    @Mock
    private AchievementService achievementService;

    @Mock
    private UserLearningStreakMapper userLearningStreakMapper;

    @Mock
    private com.itts.modules.student.service.StudentService studentService;

    @InjectMocks
    private StudyCheckinServiceImpl studyCheckinService;

    private Long testUserId;
    private StudyCheckinRequest testRequest;

    @BeforeEach
    void setUp() {
        testUserId = 1L;

        testRequest = new StudyCheckinRequest();
        testRequest.setStudyMinutes(60);
        testRequest.setStudyContent("今日学习Java基础");
        testRequest.setCourseId(1L);
    }

    @Test
    @DisplayName("应该成功首次打卡并奖励经验值")
    void should_CheckinSuccessfully_When_FirstTimeToday() {
        // Given
        when(studyCheckinMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(studyCheckinMapper.insert(any(StudyCheckin.class))).thenReturn(1);
        when(achievementService.checkAndGrantAchievements(testUserId)).thenReturn(new ArrayList<>());
        when(studyCheckinMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // When
        StudyCheckinResponse response = studyCheckinService.checkin(testUserId, testRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStudyMinutes()).isEqualTo(60);
        assertThat(response.getStudyContent()).isEqualTo("今日学习Java基础");

        verify(studyCheckinMapper).insert(any(StudyCheckin.class));
        verify(userLearningStatsService).updateStreakDays(testUserId);
        verify(userLearningStatsService).addStudyTime(testUserId, 60);
        verify(studentService).addExperience(testUserId, 20); // 验证经验值奖励
        verify(achievementService).checkAndGrantAchievements(testUserId);
    }

    @Test
    @DisplayName("应该成功更新今日已有的打卡记录")
    void should_UpdateCheckin_When_AlreadyCheckedInToday() {
        // Given
        StudyCheckin existingCheckin = new StudyCheckin();
        existingCheckin.setId(1L);
        existingCheckin.setUserId(testUserId);
        existingCheckin.setCheckinDate(LocalDate.now());
        existingCheckin.setStudyMinutes(30);
        existingCheckin.setNote("早上学习");
        existingCheckin.setCoursesStudied("[1]");

        when(studyCheckinMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingCheckin);
        when(studyCheckinMapper.updateById(any(StudyCheckin.class))).thenReturn(1);
        when(achievementService.checkAndGrantAchievements(testUserId)).thenReturn(new ArrayList<>());
        when(studyCheckinMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // When
        StudyCheckinResponse response = studyCheckinService.checkin(testUserId, testRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStudyMinutes()).isEqualTo(90); // 30 + 60

        verify(studyCheckinMapper).updateById(argThat(checkin ->
            checkin.getStudyMinutes() == 90 &&
            checkin.getNote().contains("早上学习") &&
            checkin.getNote().contains("今日学习Java基础")
        ));
        verify(userLearningStatsService).addStudyTime(testUserId, 60);
        verify(studentService, never()).addExperience(anyLong(), anyInt()); // 不应该再次奖励经验
    }

    @Test
    @DisplayName("应该正确检查今日是否已打卡")
    void should_ReturnTrue_When_CheckedInToday() {
        // Given
        when(studyCheckinMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // When
        boolean checkedIn = studyCheckinService.isTodayCheckedIn(testUserId);

        // Then
        assertThat(checkedIn).isTrue();
        verify(studyCheckinMapper).selectCount(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该正确检查今日未打卡")
    void should_ReturnFalse_When_NotCheckedInToday() {
        // Given
        when(studyCheckinMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        // When
        boolean checkedIn = studyCheckinService.isTodayCheckedIn(testUserId);

        // Then
        assertThat(checkedIn).isFalse();
        verify(studyCheckinMapper).selectCount(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该正确计算连续打卡天数")
    void should_CalculateCurrentStreak_Correctly() {
        // Given - 模拟连续3天打卡
        when(studyCheckinMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(1L)  // 今天
            .thenReturn(1L)  // 昨天
            .thenReturn(1L)  // 前天
            .thenReturn(0L); // 大前天没打卡

        // When
        int streak = studyCheckinService.getCurrentStreak(testUserId);

        // Then
        assertThat(streak).isEqualTo(3);
        verify(studyCheckinMapper, atLeast(3)).selectCount(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该在今天未打卡时从昨天开始计算连续天数")
    void should_StartFromYesterday_When_NotCheckedInToday() {
        // Given - 今天没打卡，昨天和前天打卡了
        when(studyCheckinMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(0L)  // 今天没打卡
            .thenReturn(1L)  // 昨天
            .thenReturn(1L)  // 前天
            .thenReturn(0L); // 大前天没打卡

        // When
        int streak = studyCheckinService.getCurrentStreak(testUserId);

        // Then
        assertThat(streak).isEqualTo(2);
    }

    @Test
    @DisplayName("应该正确获取打卡历史")
    void should_ReturnCheckinHistory_When_DateRangeProvided() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        List<StudyCheckin> checkins = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            StudyCheckin checkin = new StudyCheckin();
            checkin.setId((long) (i + 1));
            checkin.setUserId(testUserId);
            checkin.setCheckinDate(LocalDate.now().minusDays(i));
            checkin.setStudyMinutes(60);
            checkin.setNote("学习记录" + i);
            checkin.setCreatedAt(LocalDateTime.now());
            checkins.add(checkin);
        }

        when(studyCheckinMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(checkins);

        // When
        List<StudyCheckinResponse> history = studyCheckinService.getCheckinHistory(testUserId, startDate, endDate);

        // Then
        assertThat(history).hasSize(5);
        assertThat(history.get(0).getStudyMinutes()).isEqualTo(60);
        verify(studyCheckinMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该在获得新成就时返回成就信息")
    void should_ReturnNewAchievement_When_AchievementEarned() {
        // Given
        when(studyCheckinMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(studyCheckinMapper.insert(any(StudyCheckin.class))).thenReturn(1);
        when(studyCheckinMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        AchievementResponse achievement = new AchievementResponse();
        achievement.setId(1L);
        achievement.setName("首次打卡");
        achievement.setIcon("🎉");
        achievement.setPoints(50);

        List<AchievementResponse> achievements = new ArrayList<>();
        achievements.add(achievement);

        when(achievementService.checkAndGrantAchievements(testUserId)).thenReturn(achievements);

        // When
        StudyCheckinResponse response = studyCheckinService.checkin(testUserId, testRequest);

        // Then
        assertThat(response.getNewAchievementEarned()).isTrue();
        assertThat(response.getNewAchievement()).isNotNull();
        assertThat(response.getNewAchievement().getName()).isEqualTo("首次打卡");
    }

    @Test
    @DisplayName("应该正确格式化学习时长")
    void should_FormatStudyTime_Correctly() {
        // Given
        testRequest.setStudyMinutes(125); // 2小时5分钟

        when(studyCheckinMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(studyCheckinMapper.insert(any(StudyCheckin.class))).thenReturn(1);
        when(achievementService.checkAndGrantAchievements(testUserId)).thenReturn(new ArrayList<>());
        when(studyCheckinMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // When
        StudyCheckinResponse response = studyCheckinService.checkin(testUserId, testRequest);

        // Then
        assertThat(response.getStudyDurationFormatted()).isEqualTo("2小时5分钟");
    }
}
