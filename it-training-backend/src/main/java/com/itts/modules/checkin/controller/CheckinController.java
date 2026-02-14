package com.itts.modules.checkin.controller;

import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.checkin.dto.StudyCheckinRequest;
import com.itts.modules.checkin.dto.StudyCheckinResponse;
import com.itts.modules.checkin.service.StudyCheckinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "学习打卡", description = "学习打卡相关接口")
@RestController
@RequestMapping("/api/v1/learning")
@RequiredArgsConstructor
public class CheckinController {

    private final StudyCheckinService studyCheckinService;

    @Operation(summary = "学习打卡")
    @PostMapping("/checkin")
    public R<StudyCheckinResponse> checkin(@Valid @RequestBody StudyCheckinRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(studyCheckinService.checkin(userId, request));
    }

    @Operation(summary = "检查今日是否已打卡")
    @GetMapping("/checkin/today")
    public R<Boolean> isTodayCheckedIn() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(studyCheckinService.isTodayCheckedIn(userId));
    }

    @Operation(summary = "获取今日打卡详情")
    @GetMapping("/checkin/today/detail")
    public R<StudyCheckinResponse> getTodayCheckin() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(studyCheckinService.getTodayCheckin(userId));
    }

    @Operation(summary = "获取打卡历史")
    @GetMapping("/checkin/history")
    public R<List<StudyCheckinResponse>> getCheckinHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(studyCheckinService.getCheckinHistory(userId, startDate, endDate));
    }

    @Operation(summary = "获取当前连续打卡天数")
    @GetMapping("/checkin/streak")
    public R<Integer> getCurrentStreak() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(studyCheckinService.getCurrentStreak(userId));
    }

    @Operation(summary = "获取月度打卡日历")
    @GetMapping("/checkin/calendar/{year}/{month}")
    public R<List<LocalDate>> getMonthlyCheckinDates(
            @PathVariable int year,
            @PathVariable int month) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(studyCheckinService.getMonthlyCheckinDates(userId, year, month));
    }
}
