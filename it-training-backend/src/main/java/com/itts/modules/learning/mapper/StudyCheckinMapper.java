package com.itts.modules.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.learning.entity.StudyCheckin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 学习打卡 Mapper
 */
@Mapper
public interface StudyCheckinMapper extends BaseMapper<StudyCheckin> {

    /**
     * 根据用户ID和日期查询打卡记录
     */
    @Select("SELECT * FROM study_checkin WHERE user_id = #{userId} AND checkin_date = #{date}")
    StudyCheckin selectByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * 根据用户ID查询最近N天的打卡记录
     */
    @Select("SELECT * FROM study_checkin WHERE user_id = #{userId} AND checkin_date >= #{startDate} ORDER BY checkin_date DESC")
    List<StudyCheckin> selectRecentByUserId(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);

    /**
     * 统计用户总打卡天数
     */
    @Select("SELECT COUNT(*) FROM study_checkin WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);

    /**
     * 计算用户当前连续打卡天数
     */
    @Select("SELECT COUNT(*) FROM (" +
            "  SELECT checkin_date, " +
            "         DATE_SUB(checkin_date, INTERVAL ROW_NUMBER() OVER (ORDER BY checkin_date DESC) DAY) as grp " +
            "  FROM study_checkin " +
            "  WHERE user_id = #{userId} " +
            ") t " +
            "WHERE grp = (SELECT DATE_SUB(MAX(checkin_date), INTERVAL 0 DAY) FROM study_checkin WHERE user_id = #{userId})")
    int calculateCurrentStreak(@Param("userId") Long userId);

    /**
     * 查询用户某月的打卡记录
     */
    @Select("SELECT * FROM study_checkin WHERE user_id = #{userId} " +
            "AND YEAR(checkin_date) = #{year} AND MONTH(checkin_date) = #{month} " +
            "ORDER BY checkin_date")
    List<StudyCheckin> selectByUserIdAndMonth(@Param("userId") Long userId, 
                                               @Param("year") int year, 
                                               @Param("month") int month);
}