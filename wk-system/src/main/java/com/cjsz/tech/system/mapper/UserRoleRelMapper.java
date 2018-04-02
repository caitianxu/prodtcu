package com.cjsz.tech.system.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.domain.SysUserRoleRel;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface UserRoleRelMapper extends BaseMapper<SysUserRoleRel> {
	
	@Delete("delete from sys_user_role_rel where role_id in (${role_id}) and user_id = #{user_id}")
	void deleteByRoleAndUser(@Param("role_id") String role_id, @Param("user_id") Long user_id);
	
	@Delete("delete from sys_user_role_rel where user_id = #{user_id}")
	void deleteByUser(@Param("user_id") Long user_id);
	
	@Delete("delete from sys_user_role_rel where role_id = #{role_id}")
	void deleteByRole(@Param("role_id") Long role_id);
	
	@Update("update sys_user_role_rel set is_delete = 1 , update_time = now() where role_id in (${role_id})")
	void updateIsDeleteByRole(@Param("role_id") String role_id);
	
	@Select("select * from sys_user_role_rel where role_id in (${role_id}) and is_delete = 2")
	List<SysUserRoleRel> getRelListByRole(@Param("role_id") String role_id);
	
	@Select("select * from sys_user_role_rel where role_id = #{0} and user_id = #{1}")
	SysUserRoleRel getRelByRoleAndUser(Long role_id, Long user_id);
	
	@Update("update sys_user_role_rel set is_delete = 1 , update_time = now() where user_id = #{0}")
	void updateIsDeleteByUser(Long user_id);
	
	@Update("update sys_user_role_rel set is_delete = 1 , update_time = now() where user_id in (${user_ids})")
	void updateIsDeleteByUsers(@Param("user_ids") String user_ids);
	
	@Select("select user_role_rel_id from sys_user_role_rel order by user_role_rel_id desc limit 1")
	Long getMaxRelId();
	
	@Select("select * from sys_user_role_rel where user_id in (${user_ids}) and is_delete = 2")
	List<SysUserRoleRel> getRelListByUser(@Param("user_ids") String user_ids);
}
