package com.cjsz.tech.system.mapper;


import java.util.List;

import com.cjsz.tech.system.beans.RoleResRelBean;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.domain.SysRoleResRel;
import org.apache.ibatis.annotations.Update;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface RoleResRelMapper extends BaseMapper<SysRoleResRel> {
	
	@Delete("delete from sys_role_res_rel where role_id = #{role_id} and res_id = #{res_id}")
	void deleteByRoleAndRes(@Param("role_id") Long role_id, @Param("res_id") Long res_id);
	
	@Delete("delete from sys_role_res_rel where res_id = #{res_id}")
	void deleteByRes(@Param("res_id") Long res_id);

	@Delete("delete from sys_role_res_rel where res_id in (${res_ids})")
	void deteleByResIds(@Param("res_ids") String res_ids);
	
	@Delete("delete from sys_role_res_rel where role_id = #{role_id}")
	void deleteByRole(@Param("role_id") Long role_id);
	
	@Delete("delete from sys_role_res_rel where role_id in (${role_id})")
	void deleteByRoles(@Param("role_id") String role_id);
	
	@Select("select * from sys_role_res_rel where role_id in (${role_id})")
	List<SysRoleResRel> getRelListByRole(@Param("role_id") String role_id);

	@Select("select sr.role_res_rel_id,sr.role_id,s.res_id,s.pid,s.res_name,s.full_path,sr.data_type_id,s.res_type,sr.enabled,sr.create_time," +
			"length(s.full_path)-length(replace(s.full_path,'|','')) path_length, s.order_weight from `sys_resource` s " +
			"left join sys_role_res_rel sr on (s.res_id=sr.res_id and sr.role_id = #{0}) " +
			"left join sys_role r on (r.role_id=sr.role_id) where s.pid = #{1} and s.org_id =#{2} and (sr.role_id = #{0} or sr.role_id is null) and s.enabled = 1")
	List<RoleResRelBean> getAllRelByRoleAndPidAndOrgId(Long role_id, Long pid, Long org_id);

	@Select("select srr.role_res_rel_id,srr.role_id,s.res_id,s.pid,s.res_name,s.full_path,sr.data_type_id,s.res_type,srr.enabled,sr.create_time," +
			"length(s.full_path)-length(replace(s.full_path,'|','')) path_length, s.order_weight from `sys_resource` s " +
			"left join sys_role_res_rel sr on (s.res_id=sr.res_id and sr.role_id = #{3}) " +
			" left join sys_role_res_rel srr on (s.res_id=srr.res_id and srr.role_id = #{0}) " +
			" left join sys_role r on (r.role_id=sr.role_id) " +
			" where s.pid = #{1} and sr.enabled = 1 and s.org_id =#{2} and (sr.role_id = #{3} or sr.role_id is null) and s.enabled = 1")
	List<RoleResRelBean> getAllRelByRoleAndPidAndOrgIdAndRole(Long role_id, Long pid, Long org_id , Long sysRole_id);

	@Update("update sys_role_res_rel set enabled = #{1} , update_time = now() where role_res_rel_id = #{0}")
	void updateRelEnabled(Long rel_id, Integer enabled);

	@Update("update sys_role_res_rel set data_type_id = #{1} , update_time = now() where role_res_rel_id = #{0}")
	void updateRelDataType(Long role_res_rel_id, Integer data_type_id);

	@Delete("delete from sys_role_res_rel where role_id = #{role_id} and res_id in(${res_ids})")
	void deleteByRoleIdAndResIds(@Param("res_ids") String res_ids, @Param("role_id") Long role_id);
}
