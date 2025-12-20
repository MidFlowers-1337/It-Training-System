package com.itts.modules.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.common.exception.BusinessException;
import com.itts.modules.course.dto.CourseChapterResponse;
import com.itts.modules.course.entity.CourseChapter;
import com.itts.modules.course.mapper.CourseChapterMapper;
import com.itts.modules.course.service.CourseChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程章节服务实现
 */
@Service
@RequiredArgsConstructor
public class CourseChapterServiceImpl implements CourseChapterService {

    private final CourseChapterMapper courseChapterMapper;

    @Override
    public List<CourseChapterResponse> getCourseChapters(Long courseId, Long userId) {
        LambdaQueryWrapper<CourseChapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseChapter::getCourseId, courseId)
                .eq(CourseChapter::getStatus, 1)
                .orderByAsc(CourseChapter::getSortOrder);

        List<CourseChapter> chapters = courseChapterMapper.selectList(wrapper);

        return chapters.stream().map(chapter -> {
            CourseChapterResponse response = new CourseChapterResponse();
            BeanUtils.copyProperties(chapter, response);
            response.setIsFree(chapter.getIsFree() == 1);
            response.setCompleted(false); // TODO: 从学习进度表查询实际完成状态
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
    public void markChapterCompleted(Long chapterId, Long userId) {
        // TODO: 实现章节完成状态记录
        // 可以在 learning_progress 表中添加章节完成记录
    }
}
