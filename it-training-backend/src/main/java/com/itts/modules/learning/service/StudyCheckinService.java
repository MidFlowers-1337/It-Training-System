package com.itts.modules.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itts.modules.learning.dto.StudyCheckinRequest;
import com.itts.modules.learning.dto.StudyCheckinResponse;
import com.itts.modules.learning.entity.StudyCheckin;

import java.time.LocalDate;
import java.util.List;

/**
 * 学习打卡服务接口
 */
public interface StudyCheckinService extends IService<StudyCheckin> {

    /**
     * 学习打卡
     * @param userId 用户ID
     * @param request 打卡请求
     * @return 打卡结果
     */
    StudyCheckinResponse checkin(Long userId, StudyCheckinRequest request);

    /**
     * 检查今日是否已打卡
     * @param userId 用户ID
     * @return 是否已打卡
     */
    boolean isTodayCheckedIn(Long userId);

    /**
     * 获取用户打卡历史
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 打卡记录列表
     */
    List<StudyCheckinResponse> getCheckinHistory(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取用户当前连续打卡天数
     * @param userId 用户ID
     * @return 连续打卡天数
     */
    int getCurrentStreak(Long userId);

    /**
     * 获取用户最长连续打卡天数
     * @param userId 用户ID
     * @return 最长连续打卡天数
     */
    int getMaxStreak(Long userId);

    /**
     * 获取用户本月打卡日历
     * @param userId 用户ID
     * @param year 年份
     * @param month 月份
     * @return 打卡日期列表
     */
    List<LocalDate> getMonthlyCheckinDates(Long userId, int year, int month);

    /**
     * 获取今日打卡详情
     * @param userId 用户ID
     * @return 今日打卡详情，如果未打卡返回null
     */
    StudyCheckinResponse getTodayCheckin(Long userId);
}