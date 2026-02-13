package com.itts.modules.student.controller;

import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.achievement.service.AchievementService;
import com.itts.modules.achievement.dto.AchievementResponse;
import com.itts.modules.student.dto.StudentDashboardResponse;
import com.itts.modules.student.dto.StudentStatsResponse;
import com.itts.modules.student.service.StudentDashboardService;
import com.itts.modules.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学生端控制器
 */
@Tag(name = "学生端", description = "学生Dashboard、打卡、成就等功能")
@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentDashboardService studentDashboardService;
    private final AchievementService achievementService;

    @Operation(summary = "获取学生Dashboard数据")
    @GetMapping("/dashboard")
    public R<StudentDashboardResponse> getDashboard() {
        Long userId = SecurityUtils.getCurrentUserId();
        StudentDashboardResponse dashboard = studentDashboardService.getDashboard(userId);
        return R.ok(dashboard);
    }

    @Operation(summary = "获取学习统计")
    @GetMapping("/stats")
    public R<StudentStatsResponse> getStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        StudentStatsResponse stats = studentService.getStats(userId);
        return R.ok(stats);
    }

    @Operation(summary = "获取我的成就列表")
    @GetMapping("/achievements")
    public R<List<AchievementResponse>> getAchievements() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<AchievementResponse> achievements = achievementService.getAllAchievements(userId);
        return R.ok(achievements);
    }
}
