package com.itts.modules.stats.controller;

import com.itts.common.response.R;
import com.itts.modules.stats.dto.PublicStatsResponse;
import com.itts.modules.stats.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "公开统计", description = "Landing Page 公开数据统计")
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicStatsController {

    private final StatsService statsService;

    @Operation(summary = "获取平台概览统计（公开）")
    @GetMapping("/stats")
    public R<PublicStatsResponse> getPublicStats() {
        return R.ok(statsService.getPublicStats());
    }
}
