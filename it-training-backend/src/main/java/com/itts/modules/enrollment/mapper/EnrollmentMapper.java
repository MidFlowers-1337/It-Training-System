package com.itts.modules.enrollment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itts.modules.enrollment.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
}
