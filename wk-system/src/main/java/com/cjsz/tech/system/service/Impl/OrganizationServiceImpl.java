package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.OrgExtendBean;
import com.cjsz.tech.system.beans.OrganizationBean;
import com.cjsz.tech.system.conditions.ProjectCondition;
import com.cjsz.tech.system.domain.*;
import com.cjsz.tech.system.mapper.OrganizationMapper;
import com.cjsz.tech.system.service.*;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.utils.IDUtil;
import com.cjsz.tech.utils.PasswordUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 组织服务
 * Created by Administrator on 2016/10/25.
 */
@Service
public class OrganizationServiceImpl implements OrganizationService{

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
//    private OrgExtendMapper orgExtendMapper;
    private OrgExtendService orgExtendService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleRelService userRoleRelService;

    @Autowired
    private RoleService roleService;

    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "组织")
    public void saveOrganization(Organization organization){
    	organization.setCreate_time(new Date());
    	organization.setEnabled(Constants.ENABLE);
    	organization.setIs_delete(Constants.NOT_DELETE);
    	organization.setUpdate_time(new Date());
    	organization.setPwd_lock(2);
    	organizationMapper.insert(organization);
		Long orgId = organizationMapper.getMaxOrgId();
		//上级组织
		Organization org = organizationMapper.selecOrgByOrgId(organization.getPid());
		organization.setFull_path(org.getFull_path() + orgId + "|");
		organizationMapper.updateByPrimaryKey(organization);
    }

    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "组织")
    public void updateOrganization(Organization organization){
		//组织原始数据
		Organization org = organizationMapper.selecOrgByOrgId(organization.getOrg_id());
		//路径改表
		if(!organization.getPid().equals(org.getPid())){
			Organization org1 = organizationMapper.selecOrgByOrgId(organization.getPid());
			//下级组织路径改变
			String old_full_path = organization.getFull_path();
			String new_full_path = org1.getFull_path() + organization.getOrg_id() + "|";
			organizationMapper.updateFullPath(old_full_path, new_full_path);
			organization.setFull_path(new_full_path);
		}
    	organization.setUpdate_time(new Date());
    	organization.setIs_delete(Constants.NOT_DELETE);
    	organizationMapper.updateByPrimaryKey(organization);
    }

    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "组织")
    public void deleteById(Long org_id){
    	organizationMapper.deleteByPrimaryKey(org_id);
    }

    public Organization selectById(Long org_id) {
    	return organizationMapper.selectByPrimaryKey(org_id);
	}

    public List<Organization> findAll(){
	    return organizationMapper.selectAll();
    }

    public List<OrganizationBean> getAll(){
    	return organizationMapper.getAll();
    }

	//查询所有机构（没有组织）
	public List<Organization> selectOrgs(){
		return organizationMapper.selectOrgs();
	}

	//查询机构（除orgId = 1 以外，没有组织）
	public List<Organization> selectOtherOrgs(){
		return organizationMapper.selectOtherOrgs();
	}

    public List<Organization> getOrgsByPid(Long org_id) {
    	return organizationMapper.selectOrgByPid(org_id);
	}

    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "组织")
    public void updateIsDelete(Long org_id){
    	organizationMapper.updateIsDelete(org_id);
    }

    public void getTree(OrganizationBean org, List<OrganizationBean> orgs, List<OrganizationBean> tree) {
		for(OrganizationBean orgBean : orgs){
			if(org.getPid() != null){
				if(org.getPid().longValue() == orgBean.getOrg_id().longValue()){
					orgBean.getChildren().add(org);
					tree.add(org);
				}
			}

		}
	}

    public void getOrgTree(OrganizationBean orgbean, List<OrganizationBean> orgBeans) {
    	if(orgBeans.size() > 0){
    		for(OrganizationBean org : orgBeans){
    			if(org.getPid() != null){
    				if(org.getPid().longValue() == orgbean.getOrg_id().longValue()){
        				orgbean.getChildren().add(org);
        				getOrgTree(org, orgBeans);
        			}
    			}
    		}
    	}
	}

    public Organization getRootOrg(Long org_id){
    	Organization org = selectById(org_id);
    	while(org.getPid() != null){
			org =selectById(org.getPid());
    	}
    	return org;
    }

    //获取组织下的所有子集组织
    public void getNodeOrgs(Organization org, List<Organization> orgList, List<Organization> orgs) {
        if(orgList.size() > 0){
        	for(Organization orgtion : orgList){
        		if(orgtion.getPid() != null){
	        		if(orgtion.getPid().longValue() == org.getOrg_id().longValue()){
	        			orgs.add(orgtion);
	        			getNodeOrgs(orgtion, orgList, orgs);
	        		}
        		}
        	}
        }
    }

    public List<Organization> getOrgsByIds(String ids){
    	List<Organization> orgList = organizationMapper.getOrgList();
    	List<Organization> myselfList  = organizationMapper.getOrgsByIds(ids);
		List<Organization> orgs = new ArrayList<Organization>();
        List<String> list = Arrays.asList(ids.split(","));
    	for(String org_id : list){
    		Organization org = selectById(Long.valueOf(org_id.trim()));
    		getNodeOrgs(org, orgList, orgs);
    	}
    	orgs.addAll(myselfList);
    	return orgs;
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "组织")
    public String deleteOrgs(String ids, String mark){
    	//获取组织ids的下级组织及本身
		List<Organization> orgs = getOrgsByIds(ids);
		List<Long> org_ids = new ArrayList<Long>();
		for(Organization org : orgs){
			org_ids.add(org.getOrg_id());
		}
		String orgIds = StringUtils.join(org_ids, ",");
		//组织下的用户
		List<SysUser> users = userService.getUsersByDeptIds(orgIds);
		if(StringUtil.isEmpty(mark) && users.size() > 0){
			return "failed";
		}else{
			//删除组织
			organizationMapper.updateIsDeleteByIds(orgIds);
			//删除组织下的用户
			if(users.size() > 0){
				List<Long> userids = new ArrayList<Long>();
				for(SysUser user : users){
					userids.add(user.getUser_id());
				}
				String user_ids = StringUtils.join(userids, ",");
				userService.updateIsDelete(user_ids);
				//解绑用户角色关系
				userRoleRelService.updateIsDeleteByUsers(user_ids);
			}
			return Constants.ACTION_DELETE;
		}
    }

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "组织")
    public void deleteOrgList(String ids){
    	//获取组织ids的下级组织及本身
		List<Organization> orgs = getOrgsByIds(ids);
		List<Long> org_ids = new ArrayList<Long>();
		for(Organization org : orgs){
			org_ids.add(org.getOrg_id());
		}
		String orgIds = StringUtils.join(org_ids, ",");
		//删除组织
		organizationMapper.updateIsDeleteByIds(orgIds);
		List<SysUser> users = userService.getUsersByOrgIds(orgIds);
		//删除组织下的用户
		if(users.size() > 0){
			List<Long> userids = new ArrayList<Long>();
			for(SysUser user : users){
				userids.add(user.getUser_id());
			}
			String user_ids = StringUtils.join(userids, ",");
			userService.updateIsDelete(user_ids);
			//解绑用户角色关系
			userRoleRelService.updateIsDeleteByUsers(user_ids);
		}
    }

	public List<SysUser> getUsersByOrgId(Long org_id) {
		return organizationMapper.getUsersByOrgId(org_id);
	}

	@Override
	public Long getMaxOrgId() {
		return organizationMapper.getMaxOrgId();
	}

	@Transactional
	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "机构管理员用户，角色及关系")
	public void initOrganizationRel(OrgExtendBean orgExtend) {
		String md5Pwd = PasswordUtil.entryptPassword(Constants.ADMIN_PWD);
		//添加组织下的管理员用户
		SysUser user = new SysUser();
		user.setUser_name(orgExtend.getShort_name() + orgExtend.getExtend_code());
		user.setUser_real_name(orgExtend.getOrg_name() + Constants.ADMIN_REAL_NAME);
		user.setUser_pwd(md5Pwd);
		user.setOrg_id(orgExtend.getOrg_id());
		user.setDept_id(orgExtend.getOrg_id());
		userService.saveUser(user);
		//添加组织下的管理员角色
		Role role = new Role();
		role.setRole_name(Constants.ADMIN_ROLE_NAME);
		role.setOrg_id(orgExtend.getOrg_id());
		role.setRole_type(Constants.IS_ADMIN_ROLE);
		roleService.saveRole(role);
		//添加管理员角色用户关系
		Long user_id = userService.getMaxUserId();
		Long role_id = roleService.getMaxRoleId();
		SysUserRoleRel rel = new SysUserRoleRel();
		rel.setUser_id(user_id);
		rel.setRole_id(role_id);
		userRoleRelService.saveRel(rel);
	}

	@Override
	public List<OrgExtendBean> getOrgExtend(String keyword, String project_code, Long org_id, String myself) {
		return organizationMapper.getOrgExtend(keyword, project_code, org_id, myself);
	}

	@Transactional
	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "机构")
	public void saveOrgExtend(OrgExtendBean orgextend) {
		//机构基本信息
		Organization org = new Organization();
		org.setOrg_name(orgextend.getOrg_name());
		org.setCreate_time(new Date());
		org.setEnabled(Constants.DISABLE);
		org.setIs_delete(Constants.NOT_DELETE);
		org.setUpdate_time(new Date());
		organizationMapper.insert(org);
		Long orgId = org.getOrg_id();
		String full_path = "0|" + orgId + "|";
		org.setFull_path(full_path);
		organizationMapper.updateByPrimaryKey(org);
		//机构扩展属性
		String code = IDUtil.createId();
		OrgExtend extend = new OrgExtend();
		extend.setOrg_id(orgId);
		extend.setProject_code(orgextend.getProject_code());
		extend.setProject_name(orgextend.getProject_name());
		extend.setShort_name(orgextend.getShort_name());
		extend.setExtend_code(code);
		extend.setProvince(orgextend.getProvince());
		extend.setProvince_id(orgextend.getProvince_id());
		extend.setCity(orgextend.getCity());
		extend.setCity_id(orgextend.getCity_id());
		extend.setArea(orgextend.getArea());
		extend.setArea_id(orgextend.getArea_id());
		extend.setStreet(orgextend.getStreet());
		extend.setContacts_name(orgextend.getContacts_name());
		extend.setContacts_phone(orgextend.getContacts_phone());
		extend.setAdmin_logo(orgextend.getAdmin_logo());
		extend.setBig_screen_logo(orgextend.getBig_screen_logo());
		extend.setBig_screen_bg(orgextend.getBig_screen_bg());
		extend.setAdmin_login_logo(orgextend.getAdmin_login_logo());
		extend.setIs_delete(Constants.NOT_DELETE);
		orgExtendService.saveExtend(extend);
	}

	@Override
	public Object pageQuery(Sort sort, ProjectCondition projectCondition, String myself) {
		PageHelper.startPage(projectCondition.getPageNum(), projectCondition.getPageSize());
  		String order = ConditionOrderUtil.prepareOrder(sort);
  		if (order != null) {
  			PageHelper.orderBy(order);
  		}
  		List<OrgExtendBean> result = organizationMapper.getOrgExtend(projectCondition.getSearchText(), projectCondition.getProject_code(), null, myself);
  		PageList pageList = new PageList(result, null);
  		return pageList;
	}

	@Transactional
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "机构")
	public void updateOrgExtend(OrgExtendBean orgextend, Organization org) {
		//停用，启用
		if(orgextend.getEnabled() != org.getEnabled()){
			//更新机构启用停用状态
			org.setEnabled(orgextend.getEnabled());
			org.setUpdate_time(new Date());
			org.setIs_delete(Constants.NOT_DELETE);
			organizationMapper.updateByPrimaryKey(org);
			List<Organization> orgList = organizationMapper.getOrgList();
    		List<Organization> orgs = new ArrayList<Organization>();
			getNodeOrgs(org, orgList, orgs);
			String org_ids = "";
			orgs.add(org);
			if(orgs.size() > 0){
    			for(Organization organ : orgs){
    				org_ids += organ.getOrg_id().toString() + ",";
    			}
    			org_ids = org_ids.substring(0, org_ids.length() - 1);
    			//更新组织下的用户启用停用状态
    			userService.updateEnabled(orgextend.getEnabled(), org_ids);
    		}
		}else{
			//更新机构扩展信息
			OrgExtend extend = orgExtendService.getByOrgId(org.getOrg_id());
			extend.setProvince(orgextend.getProvince());
			extend.setProvince_id(orgextend.getProvince_id());
			extend.setCity(orgextend.getCity());
			extend.setCity_id(orgextend.getCity_id());
			extend.setArea(orgextend.getArea());
			extend.setProject_code(orgextend.getProject_code());
			extend.setProject_name(orgextend.getProject_name());
			extend.setArea_id(orgextend.getArea_id());
			extend.setStreet(orgextend.getStreet());
			extend.setContacts_name(orgextend.getContacts_name());
			extend.setContacts_phone(orgextend.getContacts_phone());
			extend.setAdmin_logo(orgextend.getAdmin_logo());
			extend.setBig_screen_logo(orgextend.getBig_screen_logo());
			extend.setBig_screen_bg(orgextend.getBig_screen_bg());
			extend.setAdmin_login_logo(orgextend.getAdmin_login_logo());
			extend.setIs_delete(Constants.NOT_DELETE);
			orgExtendService.updateExtend(extend);
		}
	}

	@Transactional
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "机构")
	public void deleteOrgExtendRel(Long org_id) {
		List<Long> orgs=  organizationMapper.getOrgIdsByPid(org_id);
		if(orgs.size() > 0){
			String ids = StringUtils.join(orgs, ",");
			deleteOrgList(ids);
		}
		//删除组织
		organizationMapper.updateIsDelete(org_id);
		//删除组织下的机构扩展信息
		orgExtendService.updateIsDelete(org_id);
		//删除角色
		roleService.updateIsDeleteByOrgId(org_id);
		//删除组织下的用户
		List<SysUser> users = getUsersByOrgId(org_id);
		if(users.size() > 0){
			List<Long> userids = new ArrayList<Long>();
			for(SysUser user : users){
				userids.add(user.getUser_id());
			}
			String user_ids = StringUtils.join(userids, ",");
			userService.updateIsDelete(user_ids);
			//解绑用户角色关系
			userRoleRelService.updateIsDeleteByUsers(user_ids);
		}
	}

	@Override
	public Organization getOrgByName(String org_name) {
		return organizationMapper.getOrgByName(org_name);
	}

	@Override
	public OrganizationBean selecOrgById(Long org_id) {
		return organizationMapper.selecOrgById(org_id);
	}

	@Override
	public OrgExtendBean getExtendByOrgId(Long orgId) {
		return organizationMapper.getExtendByOrgId(orgId);
	}

	@Override
	public List<Organization> getRootOrgsByPid() {
		return organizationMapper.getRootOrgsByPid();
	}

	@Override
	public List<Organization> getOrgList() {
		return organizationMapper.getOrgList();
	}

	@Override
	//通过full_path找到机构集合
	public List<Organization> getOrgListByFullPath(String full_path){
		return organizationMapper.getOrgListByFullPath(full_path);
	}

	@Override
	//获取当前修改的组织本身和下级组织
	public List<Organization> getOwnerOrgs(String full_path) {
		return organizationMapper.getOwnerOrgs(full_path);
	}

    @Override
    public void insertAppOrgRel(Long org_id, String org_name) {
        organizationMapper.insertAppOrgRel(org_id,org_name);
    }

    @Override
    public void updateOrgName(String org_name, Long org_id) {
        organizationMapper.updateOrgName(org_name, org_id);
    }

    @Override
    public Boolean orgIsLock(Long org_id) {
        Boolean is_lock = false;
        Integer pwd_lock = organizationMapper.selectIsLock(org_id);
        if (pwd_lock != null && pwd_lock.intValue() == 1){
        	is_lock = true;
		}
		return is_lock;
    }

    @Override
    public void updatePwdLock(Long org_id, Integer pwd_lock) {
        organizationMapper.updatePwdLock(org_id,pwd_lock);
    }

	@Override
	public List<Organization> getOrgDept(Long org_id) {
		Organization org = organizationMapper.selectByPrimaryKey(org_id);
		//查询机构下所有组织
		List<Organization> list = organizationMapper.selectByOrgId(org.getFull_path());
		return list;
	}

	@Override
	public List<Organization> selectOrgTree(List<Organization> cats1) {
		List<Organization> rstList  = new ArrayList<Organization>();
		while(cats1.size()>0){
			Organization curMenu = cats1.get(0);
			if(!rstList.contains(curMenu)) {
				rstList.add(curMenu);
			}
			cats1.remove(0);
			List<Organization> children = this.getChildren(cats1, curMenu.getOrg_id());
			curMenu.setChildren(children);
		}
		return rstList;
	}

	private List<Organization> getChildren(List<Organization> allList, Long pid){
		List<Organization> children = new ArrayList<Organization>();
		List<Organization> copyList = new ArrayList<Organization>();
		copyList.addAll(allList);
		for(Organization curMenu :copyList){
			if(curMenu.getPid().equals(pid)){
				curMenu.setChildren(this.getChildren(allList, curMenu.getOrg_id()));
				children.add(curMenu);
				allList.remove(curMenu);
			}
		}
		return children;
	}
}
