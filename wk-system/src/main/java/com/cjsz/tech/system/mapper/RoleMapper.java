package com.cjsz.tech.system.mapper;

import java.util.List;

import com.cjsz.tech.system.domain.SysUserRoleRel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.domain.Role;
import com.cjsz.tech.system.provider.RoleProvider;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface RoleMapper extends BaseMapper<Role> {
	
	@Select("select r.* from sys_role r LEFT JOIN sys_user_role_rel ur on ur.role_id = r.role_id "
			+ " WHERE ur.user_id = #{0} and ur.is_delete = 2")
	List<Role> getRolesByUserId(Long user_id);
	
	@SelectProvider(type = RoleProvider.class, method = "selectAll")
	List<Role> getRoleList(@Param("keyword") String keyword, @Param("org_id") Long org_id,
						   @Param("role_ids") String role_ids, @Param("role_type") Integer role_type);
	
	@Select("select role_id from sys_role order by role_id desc limit 1")
	Long getMaxRoleId();
	
	@Update("update sys_role set is_delete = 1 , update_time = now() where role_id in (${role_ids})")
	void updateIsDelete(@Param("role_ids") String role_ids);
	
	@Update("update sys_role set is_delete = 1 , update_time = now() where org_id = #{0}")
	void updateIsDeleteByOrgId(Long org_id);
	
	@Select("select * from sys_role where role_name = #{0} and org_id = #{1} and is_delete = 2")
	Role getRoleByNameAndOrgId(String role_name, Long org_id);

	@Select("select * from sys_user_role_rel where user_id = #{0} and is_delete = 2 order by create_time desc limit 1")
	SysUserRoleRel selectRoleByUser(Long user_id);
}
