package com.itts.modules.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.common.exception.BusinessException;
import com.itts.modules.course.dto.CourseChapterResponse;
import com.itts.modules.course.entity.CourseChapter;
import com.itts.modules.course.mapper.CourseChapterMapper;
import com.itts.modules.course.service.CourseChapterService;
import com.itts.modules.student.entity.UserChapterProgress;
import com.itts.modules.student.mapper.UserChapterProgressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程章节服务实现
 */
@Service
@RequiredArgsConstructor
public class CourseChapterServiceImpl implements CourseChapterService {

    private final CourseChapterMapper courseChapterMapper;
    private final UserChapterProgressMapper userChapterProgressMapper;

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
                .collect(Collectors.toMap(UserChapterProgress::getChapterId, p -> p));

        return chapters.stream().map(chapter -> {
            CourseChapterResponse response = new CourseChapterResponse();
            BeanUtils.copyProperties(chapter, response);
            response.setIsFree(chapter.getIsFree() == 1);

            // 从进度表查询实际完成状态和播放进度
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
            throw new BusinessException("章节不存在");
        }

        CourseChapterResponse response = new CourseChapterResponse();
        BeanUtils.copyProperties(chapter, response);
        response.setIsFree(chapter.getIsFree() == 1);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markChapterCompleted(Long chapterId, Long userId) {
        // 获取章节信息
        CourseChapter chapter = courseChapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException("章节不存在");
        }

        // 查询或创建用户章节进度
        LambdaQueryWrapper<UserChapterProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserChapterProgress::getUserId, userId)
                .eq(UserChapterProgress::getChapterId, chapterId);

        UserChapterProgress progress = userChapterProgressMapper.selectOne(wrapper);

        if (progress == null) {
            // 创建新的进度记录
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
            userChapterProgressMapper.insert(progress);
        } else if (progress.getCompleted() == 0) {
            // 更新为已完成
            progress.setCompleted(1);
            progress.setCompletedAt(LocalDateTime.now());
            progress.setUpdatedAt(LocalDateTime.now());
            userChapterProgressMapper.updateById(progress);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChapterProgress(Long chapterId, Long userId, Integer watchDuration, Integer lastPosition) {
        // 获取章节信息
        CourseChapter chapter = courseChapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException("章节不存在");
        }

        // 查询或创建用户章节进度
        LambdaQueryWrapper<UserChapterProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserChapterProgress::getUserId, userId)
                .eq(UserChapterProgress::getChapterId, chapterId);

        UserChapterProgress progress = userChapterProgressMapper.selectOne(wrapper);

        if (progress == null) {
            // 创建新的进度记录
            progress = new UserChapterProgress();
            progress.setUserId(userId);
            progress.setChapterId(chapterId);
            progress.setCourseId(chapter.getCourseId());
            progress.setWatchDuration(watchDuration);
            progress.setLastPosition(lastPosition);
            progress.setCompleted(0);
            progress.setCreatedAt(LocalDateTime.now());
            progress.setUpdatedAt(LocalDateTime.now());
            userChapterProgressMapper.insert(progress);
        } else {
            // 更新观看进度
            progress.setWatchDuration(watchDuration);
            progress.setLastPosition(lastPosition);
            progress.setUpdatedAt(LocalDateTime.now());
            userChapterProgressMapper.updateById(progress);
        }
    }
}
