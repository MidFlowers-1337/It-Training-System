package com.itts.modules.student.service;

import com.itts.modules.student.dto.StudentDashboardResponse;

/**
 * 学生 Dashboard 数据聚合服务接口
 */
public interface StudentDashboardService {

    /**
     * 获取学生Dashboard数据
     *
     * @param userId 用户ID
     * @return Dashboard数据
     */
    StudentDashboardResponse getDashboard(Long userId);
}
