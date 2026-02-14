package com.itts.modules.enrollment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itts.modules.enrollment.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 报名 Mapper
 */
@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {

    /**
     * 检查用户是否已报名该班期（已报名状态）
     */
    @Select("SELECT COUNT(*) FROM enrollment WHERE user_id = #{userId} AND session_id = #{sessionId} AND status = 0")
    int countByUserAndSession(@Param("userId") Long userId, @Param("sessionId") Long sessionId);

    /**
     * 根据用户和班期查询报名记录
     */
    @Select("SELECT * FROM enrollment WHERE user_id = #{userId} AND session_id = #{sessionId} ORDER BY id DESC LIMIT 1")
    Enrollment selectByUserAndSession(@Param("userId") Long userId, @Param("sessionId") Long sessionId);

    /**
     * 分页查询报名列表（带关联信息）
     */
    IPage<Enrollment> selectPageWithDetails(Page<Enrollment> page,
                                            @Param("userId") Long userId,
                                            @Param("sessionId") Long sessionId,
                                            @Param("status") Integer status);

    /**
     * 查询班期学员列表
     */
    IPage<Enrollment> selectStudentsBySessionId(Page<Enrollment> page, @Param("sessionId") Long sessionId);

    /**
     * 查询用户的报名列表（带关联信息，避免 N+1 查询）
     */
    List<Enrollment> selectUserEnrollmentsWithDetails(@Param("userId") Long userId);

    /**
     * 分页查询用户的报名列表（带关联信息）
     */
    IPage<Enrollment> selectUserEnrollmentsPage(Page<Enrollment> page, @Param("userId") Long userId);

    /**
     * 通过 JOIN 查询用户已报名的课程ID列表（替代 N+1 查询）
     *
     * @param userId 用户ID
     * @return 已报名的课程ID列表（已去重）
     */
    @Select("SELECT DISTINCT cs.course_id FROM enrollment e " +
            "JOIN class_session cs ON e.session_id = cs.id " +
            "WHERE e.user_id = #{userId} AND e.status = 0")
    List<Long> selectEnrolledCourseIdsByUserId(@Param("userId") Long userId);

    /**
     * 批量查询多个用户已报名的课程ID（替代循环内逐用户查询）
     *
     * @param userIds 用户ID列表
     * @return 每行包含 userId 和 courseId 的映射
     */
    @Select("<script>" +
            "SELECT DISTINCT e.user_id AS userId, cs.course_id AS courseId " +
            "FROM enrollment e " +
            "JOIN class_session cs ON e.session_id = cs.id " +
            "WHERE e.status = 0 AND e.user_id IN " +
            "<foreach collection='userIds' item='uid' open='(' separator=',' close=')'>" +
            "#{uid}" +
            "</foreach>" +
            "</script>")
    List<Map<String, Object>> selectEnrolledCourseIdsByUserIds(@Param("userIds") java.util.Collection<Long> userIds);

    /**
     * 按日期聚合统计报名数量（SQL层面聚合，避免全量加载到Java内存）
     *
     * @param startDate 开始时间（含）
     * @param endDate   结束时间（不含）
     * @param status    报名状态
     * @return 每日报名数量列表，每个Map包含 date 和 count 两个字段
     */
    @Select("SELECT DATE(enrolled_at) AS date, COUNT(*) AS count " +
            "FROM enrollment " +
            "WHERE enrolled_at >= #{startDate} AND enrolled_at < #{endDate} " +
            "AND status = #{status} " +
            "GROUP BY DATE(enrolled_at) " +
            "ORDER BY date")
    List<Map<String, Object>> countByDateRange(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               @Param("status") Integer status);

    /**
     * 查询所有报名记录（带关联信息，用于数据导出）
     */
    List<Enrollment> selectAllWithDetails();
}
