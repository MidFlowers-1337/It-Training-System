package com.itts.modules.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.modules.course.dto.CourseChapterCreateRequest;
import com.itts.modules.course.dto.CourseChapterResponse;
import com.itts.modules.course.dto.CourseChapterUpdateRequest;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.entity.CourseChapter;
import com.itts.modules.course.mapper.CourseChapterMapper;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.course.service.CourseChapterService;
import com.itts.modules.student.entity.UserChapterProgress;
import com.itts.modules.student.mapper.UserChapterProgressMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程章节服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseChapterServiceImpl implements CourseChapterService {

    private final CourseChapterMapper courseChapterMapper;
    private final UserChapterProgressMapper userChapterProgressMapper;
    private final CourseMapper courseMapper;

    @Override
    public List<CourseChapterResponse> getCourseChapters(Long courseId, Long userId) {
        LambdaQueryWrapper<CourseChapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseChapter::getCourseId, courseId)
                .eq(CourseChapter::getStatus, 1)
                .orderByAsc(CourseChapter::getSortOrder);

        List<CourseChapter> chapters = courseChapterMapper.selectList(wrapper);

        // 查询用户章节进度
        LambdaQueryWrapper<UserChapterProgress> progressWrapper = new LambdaQueryWrapper<>();
        progressWrapper.eq(UserChapterProgress::getUserId, userId)
                .eq(UserChapterProgress::getCourseId, courseId);

        List<UserChapterProgress> progressList = userChapterProgressMapper.selectList(progressWrapper);
        Map<Long, UserChapterProgress> progressMap = progressList.stream()
                .collect(Collectors.toMap(UserChapterProgress::getChapterId, p -> p, (a, b) -> a));

        return chapters.stream().map(chapter -> {
            CourseChapterResponse response = new CourseChapterResponse();
            BeanUtils.copyProperties(chapter, response);
            response.setIsFree(chapter.getIsFree() == 1);

            UserChapterProgress progress = progressMap.get(chapter.getId());
            if (progress != null) {
                response.setCompleted(progress.getCompleted() == 1);
                response.setWatchDuration(progress.getWatchDuration());
                response.setLastPosition(progress.getLastPosition());
            } else {
                response.setCompleted(false);
                response.setWatchDuration(0);
                response.setLastPosition(0);
            }

            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public CourseChapterResponse getChapterById(Long chapterId) {
        CourseChapter chapter = courseChapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_FOUND);
        }

        CourseChapterResponse response = new CourseChapterResponse();
        BeanUtils.copyProperties(chapter, response);
        response.setIsFree(chapter.getIsFree() == 1);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseChapterResponse createChapter(Long courseId, CourseChapterCreateRequest request) {
        log.info("创建章节: courseId={}, title={}", courseId, request.getTitle());

        // 验证课程存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }

        CourseChapter chapter = new CourseChapter();
        chapter.setCourseId(courseId);
        chapter.setTitle(request.getTitle());
        chapter.setDescription(request.getDescription());
        chapter.setVideoUrl(request.getVideoUrl());
        chapter.setDuration(request.getDuration());
        chapter.setIsFree(Boolean.TRUE.equals(request.getIsFree()) ? 1 : 0);
        chapter.setStatus(0); // 默认未发布
        chapter.setCreatedAt(LocalDateTime.now());
        chapter.setUpdatedAt(LocalDateTime.now());

        // 如果未指定 sortOrder，自动追加到末尾
        if (request.getSortOrder() != null) {
            chapter.setSortOrder(request.getSortOrder());
        } else {
            Integer maxOrder = getMaxSortOrder(courseId);
            chapter.setSortOrder(maxOrder + 1);
        }

        courseChapterMapper.insert(chapter);

        log.info("章节创建成功: id={}, courseId={}", chapter.getId(), courseId);

        CourseChapterResponse response = new CourseChapterResponse();
        BeanUtils.copyProperties(chapter, response);
        response.setIsFree(chapter.getIsFree() == 1);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseChapterResponse updateChapter(Long chapterId, CourseChapterUpdateRequest request) {
        log.info("更新章节: chapterId={}", chapterId);

        CourseChapter chapter = courseChapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_FOUND);
        }

        // 只更新非空字段
        if (request.getTitle() != null) {
            chapter.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            chapter.setDescription(request.getDescription());
        }
        if (request.getVideoUrl() != null) {
            chapter.setVideoUrl(request.getVideoUrl());
        }
        if (request.getDuration() != null) {
            chapter.setDuration(request.getDuration());
        }
        if (request.getIsFree() != null) {
            chapter.setIsFree(request.getIsFree() ? 1 : 0);
        }
        if (request.getStatus() != null) {
            chapter.setStatus(request.getStatus());
        }

        chapter.setUpdatedAt(LocalDateTime.now());
        courseChapterMapper.updateById(chapter);

        log.info("章节更新成功: chapterId={}", chapterId);

        CourseChapterResponse response = new CourseChapterResponse();
        BeanUtils.copyProperties(chapter, response);
        response.setIsFree(chapter.getIsFree() == 1);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChapter(Long chapterId) {
        log.info("删除章节: chapterId={}", chapterId);

        CourseChapter chapter = courseChapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_FOUND);
        }

        // 检查是否有学习进度关联
        long progressCount = userChapterProgressMapper.selectCount(
            new LambdaQueryWrapper<UserChapterProgress>()
                .eq(UserChapterProgress::getChapterId, chapterId)
        );

        if (progressCount > 0) {
            // 软删除（已有学习进度关联）
            log.info("章节有{}条学习进度关联，执行软删除", progressCount);
            courseChapterMapper.deleteById(chapterId); // @TableLogic 会处理为软删除
        } else {
            // 无进度关联也用软删除（保持一致性）
            courseChapterMapper.deleteById(chapterId);
        }

        log.info("章节删除成功: chapterId={}", chapterId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reorderChapters(Long courseId, List<Long> chapterIds) {
        log.info("重新排序章节: courseId={}, chapterIds={}", courseId, chapterIds);

        // 验证所有章节都属于该课程
        List<CourseChapter> chapters = courseChapterMapper.selectList(
            new LambdaQueryWrapper<CourseChapter>()
                .eq(CourseChapter::getCourseId, courseId)
        );

        Map<Long, CourseChapter> chapterMap = chapters.stream()
            .collect(Collectors.toMap(CourseChapter::getId, c -> c));

        // [M-03 优化] 收集需要更新的章节，最后一条 CASE WHEN SQL 批量更新
        List<CourseChapter> toUpdate = new java.util.ArrayList<>();
        for (int i = 0; i < chapterIds.size(); i++) {
            Long chapterId = chapterIds.get(i);
            CourseChapter chapter = chapterMap.get(chapterId);
            if (chapter == null) {
                throw new BusinessException(ErrorCode.CHAPTER_NOT_BELONG_TO_COURSE);
            }
            chapter.setSortOrder(i + 1);
            toUpdate.add(chapter);
        }

        if (!toUpdate.isEmpty()) {
            courseChapterMapper.batchUpdateSortOrder(toUpdate);
        }

        log.info("章节排序更新成功: courseId={}", courseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markChapterCompleted(Long chapterId, Long userId) {
        CourseChapter chapter = courseChapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_FOUND);
        }

        LambdaQueryWrapper<UserChapterProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserChapterProgress::getUserId, userId)
                .eq(UserChapterProgress::getChapterId, chapterId);

        UserChapterProgress progress = userChapterProgressMapper.selectOne(wrapper);

        if (progress == null) {
            progress = new UserChapterProgress();
            progress.setUserId(userId);
            progress.setChapterId(chapterId);
            progress.setCourseId(chapter.getCourseId());
            progress.setWatchDuration(0);
            progress.setLastPosition(0);
            progress.setCompleted(1);
            progress.setCompletedAt(LocalDateTime.now());
            progress.setCreatedAt(LocalDateTime.now());
            progress.setUpdatedAt(LocalDateTime.now());
            try {
                userChapterProgressMapper.insert(progress);
            } catch (DuplicateKeyException e) {
                // 并发插入冲突，改为更新
                log.debug("章节进度并发插入冲突，转为更新: userId={}, chapterId={}", userId, chapterId);
                progress = userChapterProgressMapper.selectOne(wrapper);
                if (progress != null && progress.getCompleted() == 0) {
                    progress.setCompleted(1);
                    progress.setCompletedAt(LocalDateTime.now());
                    progress.setUpdatedAt(LocalDateTime.now());
                    userChapterProgressMapper.updateById(progress);
                }
            }
        } else if (progress.getCompleted() == 0) {
            progress.setCompleted(1);
            progress.setCompletedAt(LocalDateTime.now());
            progress.setUpdatedAt(LocalDateTime.now());
            userChapterProgressMapper.updateById(progress);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChapterProgress(Long chapterId, Long userId, Integer watchDuration, Integer lastPosition) {
        CourseChapter chapter = courseChapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_FOUND);
        }

        LambdaQueryWrapper<UserChapterProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserChapterProgress::getUserId, userId)
                .eq(UserChapterProgress::getChapterId, chapterId);

        UserChapterProgress progress = userChapterProgressMapper.selectOne(wrapper);

        if (progress == null) {
            progress = new UserChapterProgress();
            progress.setUserId(userId);
            progress.setChapterId(chapterId);
            progress.setCourseId(chapter.getCourseId());
            progress.setWatchDuration(watchDuration);
            progress.setLastPosition(lastPosition);
            progress.setCompleted(0);
            progress.setCreatedAt(LocalDateTime.now());
            progress.setUpdatedAt(LocalDateTime.now());
            try {
                userChapterProgressMapper.insert(progress);
            } catch (DuplicateKeyException e) {
                // 并发插入冲突，改为更新
                log.debug("章节进度并发插入冲突，转为更新: userId={}, chapterId={}", userId, chapterId);
                progress = userChapterProgressMapper.selectOne(wrapper);
                if (progress != null) {
                    progress.setWatchDuration(watchDuration);
                    progress.setLastPosition(lastPosition);
                    progress.setUpdatedAt(LocalDateTime.now());
                    userChapterProgressMapper.updateById(progress);
                }
            }
        } else {
            progress.setWatchDuration(watchDuration);
            progress.setLastPosition(lastPosition);
            progress.setUpdatedAt(LocalDateTime.now());
            userChapterProgressMapper.updateById(progress);
        }
    }

    /**
     * 获取课程下最大的排序值
     */
    private Integer getMaxSortOrder(Long courseId) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CourseChapter> page =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 1);
        page.setSearchCount(false);

        com.baomidou.mybatisplus.core.metadata.IPage<CourseChapter> result =
            courseChapterMapper.selectPage(page,
                new LambdaQueryWrapper<CourseChapter>()
                    .eq(CourseChapter::getCourseId, courseId)
                    .orderByDesc(CourseChapter::getSortOrder)
            );
        List<CourseChapter> chapters = result.getRecords();
        return chapters.isEmpty() ? 0 : chapters.get(0).getSortOrder();
    }
}
