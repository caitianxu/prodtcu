package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.beans.OrgExtendBean;
import com.cjsz.tech.system.beans.OrganizationBean;
import com.cjsz.tech.system.beans.TreeBean;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.provider.OrganizationProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface OrganizationMapper extends BaseMapper<Organization> {

	@Select("select * from sys_organization where pid = #{0} and is_delete = 2")
	List<Organization> selectOrgByPid(Long pid);

	@Select("select * from sys_organization where is_delete = 2")
	List<OrganizationBean> getAll();

	@Select("select * from sys_organization where is_delete = 2")
	List<Organization> getOrgList();

	@Select("select * from sys_organization where org_code = #{0} and is_delete = 2")
	Organization getOrgByCode(String orgCode);


	@Select("SELECT u.* FROM sys_user u where u.org_id = #{0} and u.is_delete = 2 ")
	List<SysUser> getUsersByOrgId(Long org_id);

	@Select("select org_id from sys_organization order by org_id desc limit 1")
	Long getMaxOrgId();

	@Update("update sys_organization set is_delete = 1 , update_time = now() where org_id = #{0}")
	void updateIsDelete(Long org_id);

	@Update("update sys_organization set is_delete = 1 , update_time = now() where org_id in (${ids})")
	void updateIsDeleteByIds(@Param("ids") String ids);

	@Select("select * from sys_organization where pid = #{0} and is_delete = 2")
	List<OrganizationBean> selectBeansByPid(Long pid);

	@SelectProvider(type = OrganizationProvider.class, method = "getOrgExtend")
	List<OrgExtendBean> getOrgExtend(@Param("keyword") String keyword, @Param("project_code") String project_code, @Param("org_id") Long org_id, @Param("myself") String myself);

	@Select("select * from sys_organization where org_name = #{0} and is_delete = 2 and (pid is null or pid = 0)")
	Organization getOrgByName(String org_name);

	@Select("select * from sys_organization  where is_delete = 2 and org_id = #{0} limit 1")
	OrganizationBean selecOrgById(Long org_id);

	@Select("select * from sys_organization  where org_id = #{0} limit 1")
	Organization selecOrgByOrgId(Long org_id);

	@Select("select o.org_name, o.enabled, o.create_time, e.*, CONCAT(e.province , e.city , e.area) as address "
			+ " from sys_organization o "
			+ " LEFT JOIN sys_org_extend e ON e.org_id = o.org_id "
			+ " where o.is_delete = 2 and e.is_delete = 2  "
			+ " and (o.pid is null or o.pid = 0) "
			+ " and o.org_id = #{0} limit 1")
	OrgExtendBean getExtendByOrgId(Long orgId);

	@Select("select * from sys_organization where (pid is null or pid = 0) and is_delete = 2 and org_id <> 1")
	List<Organization> getRootOrgsByPid();

	@Select("select org_id from sys_organization where pid = #{0} and is_delete = 2")
	List<Long> getOrgIdsByPid(Long pid);

	@Select("select org_id from sys_organization where pid in (${ids}) and is_delete = 2")
	List<Long> getOrgIdsByPids(@Param("ids") String ids);

	@Select("select * from sys_organization where org_id in (${ids}) and is_delete = 2")
	List<Organization> getOrgsByIds(@Param("ids") String ids);

	//查询机构（除orgId = 1 以外，没有组织）
	@Select("select * from sys_organization where org_id != 1 and (pid is null or pid = 0)")
	List<Organization> selectOtherOrgs();

	//查询所有机构（没有组织）
	@Select("select * from sys_organization where pid is null or pid = 0")
	List<Organization> selectOrgs();

	@Update("update sys_organization o set o.full_path = REPLACE(o.full_path,#{0},#{1}), o.update_time = now() " +
			"where o.full_path like concat(#{0},'%') and o.full_path != #{0}")
	public void updateFullPath(String old_full_path, String new_full_path);

	//通过full_path找到机构集合
	@Select("select * from sys_organization where full_path like concat('${full_path}','%')")
	List<Organization> getOrgListByFullPath(@Param("full_path") String full_path);

	//获取当前修改的组织本身和下级组织
	@Select("select * from sys_organization where full_path like concat(#{0},'%')")
	List<Organization> getOwnerOrgs(String full_path);

	//当前存在并使用的机构
	@Select("select o.org_id id, e.project_code code, o.org_name text, 'org' type from sys_organization o " +
			"left join sys_org_extend e on o.org_id = e.org_id " +
			"where o.enabled = 1 and o.is_delete = 2 and o.pid is null " +
			"and e.is_delete = 2")
	List<TreeBean> getCurOrgList();

	@Insert("insert app_type_org_rel set org_id = #{0},org_name = #{1} ,app_type_id = 2,create_time = now()")
    void insertAppOrgRel(Long org_id, String org_name);

	@Select("select * from sys_organization where is_delete =2 and pid is null")
    List<Organization> selectAllOrg();

    @Update("update sys_organization set org_name = #{0}, update_time = now() where org_id = #{1}")
    void updateOrgName(String org_name, Long org_id);

    @Select("select pwd_lock from sys_organization where org_id = #{0} limit 1")
    Integer selectIsLock(Long org_id);

    @Update("update sys_organization set pwd_lock = #{1},update_time = now() where org_id = #{0}")
    void updatePwdLock(Long org_id, Integer pwd_lock);

    @Select("select * from sys_organization where is_delete = 2 and enabled = 1 and full_path like concat(#{0},'%') and full_path != #{0}")
	List<Organization> selectByOrgId(String full_path);
}
