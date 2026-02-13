package com.itts.modules.stats.controller;

import com.itts.common.response.R;
import com.itts.modules.course.dto.CourseResponse;
import com.itts.modules.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公开课程接口 — Landing Page 无需登录即可浏览
 */
@Tag(name = "公开课程", description = "Landing Page 公开课程浏览")
@RestController
@RequestMapping("/api/v1/public/courses")
@RequiredArgsConstructor
public class PublicCourseController {

    private final CourseService courseService;

    @Operation(summary = "获取精选课程列表（公开）")
    @GetMapping("/featured")
    public R<List<CourseResponse>> getFeaturedCourses(
            @RequestParam(defaultValue = "8") int limit) {
        List<CourseResponse> all = courseService.listPublishedCourses();
        // 取前 limit 条作为精选
        List<CourseResponse> featured = all.stream()
                .limit(Math.min(limit, 20))
                .toList();
        return R.ok(featured);
    }
}
