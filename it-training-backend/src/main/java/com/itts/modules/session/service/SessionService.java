package com.itts.modules.session.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.modules.session.dto.SessionCreateRequest;
import com.itts.modules.session.dto.SessionResponse;
import com.itts.modules.session.dto.SessionUpdateRequest;

import java.util.List;

/**
 * 班期服务接口
 */
public interface SessionService {

    /**
     * 分页查询班期列表
     * @param page 页码
     * @param size 每页大小
     * @param courseId 课程ID过滤
     * @param instructorId 讲师ID过滤
     * @param status 状态过滤
     * @return 班期分页列表
     */
    IPage<SessionResponse> listSessions(int page, int size, Long courseId, Long instructorId, Integer status);

    /**
     * 获取可报名的班期列表（学员端）
     * @param courseId 课程ID过滤
     * @return 班期列表
     */
    List<SessionResponse> listEnrollableSessions(Long courseId);

    /**
     * 根据ID获取班期
     * @param id 班期ID
     * @return 班期信息
     */
    SessionResponse getSessionById(Long id);

    /**
     * 创建班期
     * @param request 创建请求
     * @return 班期信息
     */
    SessionResponse createSession(SessionCreateRequest request);

    /**
     * 更新班期
     * @param id 班期ID
     * @param request 更新请求
     * @return 班期信息
     */
    SessionResponse updateSession(Long id, SessionUpdateRequest request);

    /**
     * 删除班期
     * @param id 班期ID
     */
    void deleteSession(Long id);

    /**
     * 开放报名
     * @param id 班期ID
     */
    void openEnrollment(Long id);

    /**
     * 关闭报名
     * @param id 班期ID
     */
    void closeEnrollment(Long id);

    /**
     * 获取当前讲师的班期列表
     * @return 班期列表
     */
    List<SessionResponse> getMySessionsAsInstructor();
}
