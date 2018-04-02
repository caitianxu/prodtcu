package com.cjsz.tech.system.service;

import com.cjsz.tech.system.beans.OrgExtendBean;
import com.cjsz.tech.system.beans.OrganizationBean;
import com.cjsz.tech.system.conditions.ProjectCondition;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.domain.SysUser;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface OrganizationService {

	// 新增组织
    public void saveOrganization(Organization organization);

    //更新组织
    public void updateOrganization(Organization organization);

    //根据id删除组织
    public void deleteById(Long org_id);

    //根据id查询组织
    public Organization selectById(Long org_id);

    //查询所有组织
    public List<Organization> findAll();

    //查询所有组织 返回OrganizationBean
    public List<OrganizationBean> getAll();

	//查询所有机构（没有组织）
	public List<Organization> selectOrgs();

	//查询机构（除orgId = 1 以外，没有组织）
	public List<Organization> selectOtherOrgs();

    //获取组织树
    public void getTree(OrganizationBean org, List<OrganizationBean> orgs, List<OrganizationBean> tree);

    //获取组织ids的下级组织及本身
	public List<Organization> getOrgsByIds(String ids);

	//通过组织id查询该组织下的所有用户
	public List<SysUser> getUsersByOrgId(Long org_id);

	//查询新增数据id
	public Long getMaxOrgId();

	// 初始化组织
    public void initOrganizationRel(OrgExtendBean orgExtend);

    //获取用户下的组织树
	public void getOrgTree(OrganizationBean org, List<OrganizationBean> orgBeans);

	//获取用户的根组织
	public Organization getRootOrg(Long org_id);

	//获取组织下的所有子节点id集合
	public void getNodeOrgs(Organization org, List<Organization> orgList, List<Organization> orgs);

	//获取所有的根组织 myself有值时，剔除org_id为1的组织
	public List<OrgExtendBean> getOrgExtend(String keyword, String project_code, Long org_id, String myself);

	// 添加机构, 机构扩展属性
    public void saveOrgExtend(OrgExtendBean orgextend);

    //分页查询
  	public Object pageQuery(Sort sort, ProjectCondition projectCondition, String myself);

  	//修改机构信息及扩展信息,启用/停用
	public void updateOrgExtend(OrgExtendBean orgextend, Organization org);

	//删除机构信息及扩展信息，关联角色，组织， 用户，用户角色关系
	public void deleteOrgExtendRel(Long org_id);

	//通过机构名称查机构
	public Organization getOrgByName(String org_name);

	//通过组织id查组织
	public OrganizationBean selecOrgById(Long org_id);

	//通过机构id查机构
	public OrgExtendBean getExtendByOrgId(Long orgId);

	//获取超级管理员创建的机构
	public List<Organization> getRootOrgsByPid();

	//通过ids删除组织
	public String deleteOrgs(String org_ids, String mark);

	//查询所有组织
	public List<Organization> getOrgList();

	//通过full_path找到机构集合
	public List<Organization> getOrgListByFullPath(String full_path);

	//获取当前组织本身和下级组织
	public List<Organization> getOwnerOrgs(String full_path);

    void insertAppOrgRel(Long org_id, String org_name);

    //更新机构名称
    void updateOrgName(String org_name, Long org_id);

    //查询机构会员密码是否锁定
    Boolean orgIsLock(Long org_id);

    //更改机构锁定密码状态
    void updatePwdLock(Long org_id, Integer pwd_lock);

    //查询结构下所有组织
    List<Organization> getOrgDept(Long org_id);

    //机构组织树
	List<Organization> selectOrgTree(List<Organization> deptList);

	//通过项目ID查机构

}
