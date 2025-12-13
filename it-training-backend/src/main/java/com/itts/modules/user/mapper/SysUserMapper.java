package com.itts.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itts.modules.user.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户 Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 根据角色查询用户列表
     */
    @Select("SELECT * FROM sys_user WHERE role = #{role} AND deleted = 0")
    List<SysUser> selectByRole(@Param("role") String role);
}
