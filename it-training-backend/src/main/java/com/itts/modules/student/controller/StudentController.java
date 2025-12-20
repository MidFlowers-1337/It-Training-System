package com.itts.modules.student.controller;

import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.student.dto.CheckinResponse;
import com.itts.modules.student.dto.StudentDashboardResponse;
import com.itts.modules.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学生端控制器
 */
@Tag(name = "学生端", description = "学生Dashboard、打卡、成就等功能")
@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "获取学生Dashboard数据")
    @GetMapping("/dashboard")
    public R<StudentDashboardResponse> getDashboard() {
        Long userId = SecurityUtils.getCurrentUserId();
        StudentDashboardResponse dashboard = studentService.getDashboard(userId);
        return R.ok(dashboard);
    }

    @Operation(summary = "学习打卡")
    @PostMapping("/checkin")
    public R<CheckinResponse> checkin() {
        Long userId = SecurityUtils.getCurrentUserId();
        CheckinResponse response = studentService.checkin(userId);
        return R.ok(response);
    }

    @Operation(summary = "获取学习统计")
    @GetMapping("/stats")
    public R<Object> getStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        // TODO: 实现学习统计
        return R.ok();
    }

    @Operation(summary = "获取我的成就列表")
    @GetMapping("/achievements")
    public R<Object> getAchievements() {
        Long userId = SecurityUtils.getCurrentUserId();
        // TODO: 实现成就列表
        return R.ok();
    }
}
