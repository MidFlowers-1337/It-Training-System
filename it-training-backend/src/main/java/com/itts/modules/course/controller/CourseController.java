package com.itts.modules.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.common.response.R;
import com.itts.modules.course.dto.CourseCreateRequest;
import com.itts.modules.course.dto.CourseResponse;
import com.itts.modules.course.dto.CourseUpdateRequest;
import com.itts.modules.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 课程管理控制器
 */
@Tag(name = "课程管理", description = "课程CRUD和上下架操作")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "分页查询课程列表")
    @GetMapping
    public R<IPage<CourseResponse>> listCourses(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "分类过滤") @RequestParam(required = false) String category,
            @Parameter(description = "状态过滤") @RequestParam(required = false) Integer status,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        IPage<CourseResponse> result = courseService.listCourses(page, size, category, status, keyword);
        return R.ok(result);
    }

    @Operation(summary = "获取所有已发布课程（下拉选择用）")
    @GetMapping("/published")
    public R<List<CourseResponse>> listPublishedCourses() {
        List<CourseResponse> courses = courseService.listPublishedCourses();
        return R.ok(courses);
    }

    @Operation(summary = "根据ID获取课程")
    @GetMapping("/{id}")
    public R<CourseResponse> getCourseById(@PathVariable Long id) {
        CourseResponse course = courseService.getCourseById(id);
        return R.ok(course);
    }

    @Operation(summary = "创建课程")
    @PostMapping
    public R<CourseResponse> createCourse(@Valid @RequestBody CourseCreateRequest request) {
        CourseResponse course = courseService.createCourse(request);
        return R.ok(course);
    }

    @Operation(summary = "更新课程")
    @PutMapping("/{id}")
    public R<CourseResponse> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseUpdateRequest request) {
        CourseResponse course = courseService.updateCourse(id, request);
        return R.ok(course);
    }

    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    public R<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return R.ok();
    }

    @Operation(summary = "发布课程")
    @PatchMapping("/{id}/publish")
    public R<Void> publishCourse(@PathVariable Long id) {
        courseService.publishCourse(id);
        return R.ok();
    }

    @Operation(summary = "下架课程")
    @PatchMapping("/{id}/unpublish")
    public R<Void> unpublishCourse(@PathVariable Long id) {
        courseService.unpublishCourse(id);
        return R.ok();
    }
}
