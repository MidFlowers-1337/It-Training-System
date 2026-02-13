package com.itts.modules.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.course.dto.CourseChapterCreateRequest;
import com.itts.modules.course.dto.CourseChapterResponse;
import com.itts.modules.course.dto.CourseCreateRequest;
import com.itts.modules.course.dto.CourseResponse;
import com.itts.modules.course.dto.CourseUpdateRequest;
import com.itts.modules.course.dto.CourseChapterUpdateRequest;
import com.itts.modules.course.service.CourseChapterService;
import com.itts.modules.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Validated
public class CourseController {

    private final CourseService courseService;
    private final CourseChapterService courseChapterService;

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
    @PreAuthorize("hasRole('ADMIN')")
    public R<CourseResponse> createCourse(@Valid @RequestBody CourseCreateRequest request) {
        CourseResponse course = courseService.createCourse(request);
        return R.ok(course);
    }

    @Operation(summary = "更新课程")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public R<CourseResponse> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseUpdateRequest request) {
        CourseResponse course = courseService.updateCourse(id, request);
        return R.ok(course);
    }

    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return R.ok();
    }

    @Operation(summary = "发布课程")
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> publishCourse(@PathVariable Long id) {
        courseService.publishCourse(id);
        return R.ok();
    }

    @Operation(summary = "下架课程")
    @PatchMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> unpublishCourse(@PathVariable Long id) {
        courseService.unpublishCourse(id);
        return R.ok();
    }

    @Operation(summary = "获取课程章节列表")
    @GetMapping("/{courseId}/chapters")
    public R<List<CourseChapterResponse>> getCourseChapters(@PathVariable Long courseId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<CourseChapterResponse> chapters = courseChapterService.getCourseChapters(courseId, userId);
        return R.ok(chapters);
    }

    @Operation(summary = "获取章节详情")
    @GetMapping("/chapters/{chapterId}")
    public R<CourseChapterResponse> getChapterById(@PathVariable Long chapterId) {
        CourseChapterResponse chapter = courseChapterService.getChapterById(chapterId);
        return R.ok(chapter);
    }

    @Operation(summary = "标记章节为已完成")
    @PostMapping("/chapters/{chapterId}/complete")
    public R<Void> markChapterCompleted(@PathVariable Long chapterId) {
        Long userId = SecurityUtils.getCurrentUserId();
        courseChapterService.markChapterCompleted(chapterId, userId);
        return R.ok();
    }

    @Operation(summary = "更新章节观看进度")
    @PostMapping("/chapters/{chapterId}/progress")
    public R<Void> updateChapterProgress(
            @PathVariable Long chapterId,
            @RequestParam @Min(value = 0, message = "观看时长不能为负数") Integer watchDuration,
            @RequestParam @Min(value = 0, message = "播放位置不能为负数") Integer lastPosition) {
        Long userId = SecurityUtils.getCurrentUserId();
        courseChapterService.updateChapterProgress(chapterId, userId, watchDuration, lastPosition);
        return R.ok();
    }

    // ==================== 章节管理 CRUD（管理员专属） ====================

    @Operation(summary = "创建章节")
    @PostMapping("/{courseId}/chapters")
    @PreAuthorize("hasRole('ADMIN')")
    public R<CourseChapterResponse> createChapter(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseChapterCreateRequest request) {
        CourseChapterResponse chapter = courseChapterService.createChapter(courseId, request);
        return R.ok(chapter);
    }

    @Operation(summary = "更新章节")
    @PutMapping("/chapters/{chapterId}")
    @PreAuthorize("hasRole('ADMIN')")
    public R<CourseChapterResponse> updateChapter(
            @PathVariable Long chapterId,
            @Valid @RequestBody CourseChapterUpdateRequest request) {
        CourseChapterResponse chapter = courseChapterService.updateChapter(chapterId, request);
        return R.ok(chapter);
    }

    @Operation(summary = "删除章节")
    @DeleteMapping("/chapters/{chapterId}")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> deleteChapter(@PathVariable Long chapterId) {
        courseChapterService.deleteChapter(chapterId);
        return R.ok();
    }

    @Operation(summary = "重新排序章节")
    @PutMapping("/{courseId}/chapters/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> reorderChapters(
            @PathVariable Long courseId,
            @RequestBody List<Long> chapterIds) {
        courseChapterService.reorderChapters(courseId, chapterIds);
        return R.ok();
    }
}
