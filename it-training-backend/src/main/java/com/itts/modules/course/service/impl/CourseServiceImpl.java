package com.itts.modules.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itts.common.config.RedisCacheConfig;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.enums.CourseCategory;
import com.itts.enums.CourseDifficulty;
import com.itts.enums.CourseStatus;
import com.itts.enums.DeleteFlag;
import com.itts.modules.course.dto.CourseCreateRequest;
import com.itts.modules.course.dto.CourseResponse;
import com.itts.modules.course.dto.CourseUpdateRequest;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;

    @Override
    public IPage<CourseResponse> listCourses(int page, int size, String category, Integer status, String keyword) {
        Page<Course> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getDeleted, DeleteFlag.NOT_DELETED);

        // 分类过滤
        if (StringUtils.hasText(category)) {
            wrapper.eq(Course::getCategory, category);
        }

        // 状态过滤
        if (status != null) {
            wrapper.eq(Course::getStatus, status);
        }

        // 关键词搜索（课程名称、编码、描述）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Course::getName, keyword)
                    .or()
                    .like(Course::getCode, keyword)
                    .or()
                    .like(Course::getDescription, keyword)
            );
        }

        wrapper.orderByDesc(Course::getCreatedAt);

        IPage<Course> coursePage = courseMapper.selectPage(pageParam, wrapper);

        // 转换为响应对象
        return coursePage.convert(this::convertToResponse);
    }

    @Override
    @Cacheable(value = RedisCacheConfig.CACHE_COURSE_LIST, key = "'published'")
    public List<CourseResponse> listPublishedCourses() {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getDeleted, DeleteFlag.NOT_DELETED)
                .eq(Course::getStatus, CourseStatus.PUBLISHED.getCode()) // 已发布
                .orderByAsc(Course::getName);

        List<Course> courses = courseMapper.selectList(wrapper);
        return courses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = RedisCacheConfig.CACHE_COURSES, key = "#id")
    public CourseResponse getCourseById(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null || DeleteFlag.isDeleted(course.getDeleted())) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }
        return convertToResponse(course);
    }

    @Override
    @Transactional
    @CacheEvict(value = RedisCacheConfig.CACHE_COURSE_LIST, allEntries = true)
    public CourseResponse createCourse(CourseCreateRequest request) {
        log.info("创建课程: {}", request.getCode());

        // 检查课程编码是否已存在
        int count = courseMapper.countByCode(request.getCode());
        if (count > 0) {
            throw new BusinessException(ErrorCode.COURSE_CODE_EXISTS);
        }

        Course course = new Course();
        BeanUtils.copyProperties(request, course);
        course.setStatus(CourseStatus.DRAFT.getCode()); // 默认草稿状态
        course.setDeleted(DeleteFlag.NOT_DELETED);

        courseMapper.insert(course);

        log.info("课程创建成功: {}, ID: {}", request.getCode(), course.getId());
        return convertToResponse(course);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CACHE_COURSES, key = "#id"),
            @CacheEvict(value = RedisCacheConfig.CACHE_COURSE_LIST, allEntries = true)
    })
    public CourseResponse updateCourse(Long id, CourseUpdateRequest request) {
        log.info("更新课程: {}", id);

        Course course = courseMapper.selectById(id);
        if (course == null || DeleteFlag.isDeleted(course.getDeleted())) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }

        // 更新字段（只更新非空字段）
        if (StringUtils.hasText(request.getName())) {
            course.setName(request.getName());
        }
        if (StringUtils.hasText(request.getCategory())) {
            course.setCategory(request.getCategory());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getCoverImage() != null) {
            course.setCoverImage(request.getCoverImage());
        }
        if (request.getDifficulty() != null) {
            course.setDifficulty(request.getDifficulty());
        }
        if (request.getDurationHours() != null) {
            course.setDurationHours(request.getDurationHours());
        }
        if (request.getTags() != null) {
            course.setTags(request.getTags());
        }

        courseMapper.updateById(course);

        log.info("课程更新成功: {}", id);
        return convertToResponse(course);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CACHE_COURSES, key = "#id"),
            @CacheEvict(value = RedisCacheConfig.CACHE_COURSE_LIST, allEntries = true)
    })
    public void deleteCourse(Long id) {
        log.info("删除课程: {}", id);

        Course course = courseMapper.selectById(id);
        if (course == null || DeleteFlag.isDeleted(course.getDeleted())) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }

        // 检查是否有班期
        int sessionCount = courseMapper.countSessionsByCourseId(id);
        if (sessionCount > 0) {
            throw new BusinessException(ErrorCode.COURSE_HAS_SESSION);
        }

        // 软删除
        course.setDeleted(DeleteFlag.DELETED);
        courseMapper.updateById(course);

        log.info("课程删除成功: {}", id);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CACHE_COURSES, key = "#id"),
            @CacheEvict(value = RedisCacheConfig.CACHE_COURSE_LIST, allEntries = true)
    })
    public void publishCourse(Long id) {
        log.info("发布课程: {}", id);

        Course course = courseMapper.selectById(id);
        if (course == null || DeleteFlag.isDeleted(course.getDeleted())) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }

        course.setStatus(CourseStatus.PUBLISHED.getCode()); // 已发布
        courseMapper.updateById(course);

        log.info("课程发布成功: {}", id);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CACHE_COURSES, key = "#id"),
            @CacheEvict(value = RedisCacheConfig.CACHE_COURSE_LIST, allEntries = true)
    })
    public void unpublishCourse(Long id) {
        log.info("下架课程: {}", id);

        Course course = courseMapper.selectById(id);
        if (course == null || DeleteFlag.isDeleted(course.getDeleted())) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }

        course.setStatus(CourseStatus.DRAFT.getCode()); // 草稿
        courseMapper.updateById(course);

        log.info("课程下架成功: {}", id);
    }

    /**
     * 转换为响应对象
     */
    private CourseResponse convertToResponse(Course course) {
        CourseResponse response = new CourseResponse();
        BeanUtils.copyProperties(course, response);

        // 设置分类名称
        try {
            CourseCategory category = CourseCategory.valueOf(course.getCategory());
            response.setCategoryName(category.getDesc());
        } catch (Exception e) {
            response.setCategoryName(course.getCategory());
        }

        // 设置难度名称
        CourseDifficulty difficulty = CourseDifficulty.fromCode(course.getDifficulty());
        if (difficulty != null) {
            response.setDifficultyName(difficulty.getDesc());
        }

        // 设置状态名称
        CourseStatus status = CourseStatus.fromCode(course.getStatus());
        response.setStatusName(status != null ? status.getDesc() : "未知");

        return response;
    }
}
