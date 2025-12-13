package com.itts.modules.session.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itts.modules.session.entity.ClassSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 班期 Mapper
 */
@Mapper
public interface ClassSessionMapper extends BaseMapper<ClassSession> {

    /**
     * 根据班期编码查询班期
     */
    @Select("SELECT * FROM class_session WHERE session_code = #{sessionCode} AND deleted = 0")
    ClassSession selectBySessionCode(@Param("sessionCode") String sessionCode);

    /**
     * 检查班期编码是否存在
     */
    @Select("SELECT COUNT(*) FROM class_session WHERE session_code = #{sessionCode} AND deleted = 0")
    int countBySessionCode(@Param("sessionCode") String sessionCode);

    /**
     * 检查班期是否有报名
     */
    @Select("SELECT COUNT(*) FROM enrollment WHERE session_id = #{sessionId} AND status = 0")
    int countEnrollmentsBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 原子增加报名人数（用于并发控制）
     * 只有当 current_enrollment < max_capacity 且 status = 1（报名中）时才更新
     * @return 受影响行数，0表示名额已满或状态不允许
     */
    @Update("UPDATE class_session SET current_enrollment = current_enrollment + 1, " +
            "status = CASE WHEN current_enrollment + 1 >= max_capacity THEN 2 ELSE status END " +
            "WHERE id = #{sessionId} AND current_enrollment < max_capacity AND status = 1 AND deleted = 0")
    int incrementEnrollment(@Param("sessionId") Long sessionId);

    /**
     * 原子减少报名人数
     */
    @Update("UPDATE class_session SET current_enrollment = current_enrollment - 1, " +
            "status = CASE WHEN status = 2 AND current_enrollment - 1 < max_capacity THEN 1 ELSE status END " +
            "WHERE id = #{sessionId} AND current_enrollment > 0 AND deleted = 0")
    int decrementEnrollment(@Param("sessionId") Long sessionId);

    /**
     * 分页查询班期列表（带关联信息）
     */
    IPage<ClassSession> selectPageWithDetails(Page<ClassSession> page,
                                              @Param("courseId") Long courseId,
                                              @Param("instructorId") Long instructorId,
                                              @Param("status") Integer status);
}
