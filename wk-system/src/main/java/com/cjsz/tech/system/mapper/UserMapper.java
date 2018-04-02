package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.conditions.UserCondition;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.provider.UserProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface UserMapper extends BaseMapper<SysUser> {
	
	@Update("update sys_user set org_id = null where user_id in (${userids})")
	void updateOrgByIds(@Param("userids") String userids);
	
	@Update("update sys_user set is_delete = 1 where user_id in (${userids})")
	void updateIsDelete(@Param("userids") String userids);
	
	@Update("update sys_user set is_delete = 1 where user_id = #{user_id}")
	void updateIsDeleteByUserId(@Param("user_id") Long user_id);

	@SelectProvider(type = UserProvider.class, method = "selectAll")
	List<SysUser> getUserList(@Param("condition") UserCondition condition);
	
	@Select("select user_id from sys_user order by user_id desc limit 1")
	Long getMaxUserId();
	
	@Select("SELECT u.* , o.org_name, GROUP_CONCAT(r.role_type order by r.role_id asc) as  role_type, " 
				+ " GROUP_CONCAT(r.role_name order by r.role_id asc) as  role_name, "
				+ " GROUP_CONCAT(r.role_id order by r.role_id asc) as  role_id "
				+ " FROM sys_user u "
				+ " LEFT JOIN sys_organization o on o.org_id = u.org_id and o.is_delete = 2 "
				+ " LEFT JOIN sys_user_role_rel ur on u.user_id = ur.user_id and ur.is_delete = 2 "
				+ " LEFT JOIN sys_role r on r.role_id = ur.role_id and r.is_delete = 2 "
				+ " where u.is_delete = 2 and u.user_id = #{0}")
	SysUser getUserById(Long user_id);
	
	@Select("select * from sys_user where user_name = #{0} and user_pwd = #{1}")
	SysUser selectLoginUser(String user_name, String user_pwd);
	
	@Select("select * from sys_user where token = #{0}")
	SysUser findByToken(String token);
	
	@Select("select * from sys_user where user_name = #{0} and is_delete = 2")
	SysUser findByUserName(String user_name);
	
	@Select("select * from sys_user where user_id in (${user_ids}) and is_delete = 2")
	List<SysUser> getUserListByIds(@Param("user_ids") String user_ids);
	
	@Update("update sys_user set enabled = #{enabled} where org_id in (${org_ids})")
	void updateEnabled(@Param("enabled") Integer enabled, @Param("org_ids") String org_ids);
	
	@Select("SELECT * FROM sys_user where org_id != #{org_id} and user_id in (${user_ids}) ")
	List<SysUser> getOtherUserByIds(@Param("user_ids") String user_ids, @Param("org_id") Long org_id);

	@Select("SELECT u.* FROM sys_user u LEFT JOIN sys_user_role_rel ur on u.user_id = ur.user_id "
			+ " LEFT JOIN sys_role r on r.role_id = ur.role_id "
			+ " where r.role_type = 1  and u.user_id in (${user_ids}) and u.is_delete = 2")
	List<SysUser> getAdminUserByIds(@Param("user_ids") String user_ids);
	
	@Select("select * from sys_user where org_id in (${ids}) and is_delete = 2")
	List<SysUser> getUsersByOrgIds(@Param("ids") String ids);

	@Select("select * from sys_user where dept_id in (${ids}) and is_delete = 2")
	List<SysUser> getUsersByDeptIds(@Param("ids") String ids);
}
