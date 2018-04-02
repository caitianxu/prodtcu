package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.Role;
import com.cjsz.tech.system.domain.SysUserRoleRel;
import com.cjsz.tech.system.mapper.RoleMapper;
import com.cjsz.tech.system.service.RoleService;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2016/10/25.
 */
@Service
public class RoleServiceImpl implements RoleService{
	
    @Autowired
    private RoleMapper roleMapper;

	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "角色")
    public void saveRole(Role role){
		role.setIs_delete(Constants.NOT_DELETE);
		role.setCreate_time(new Date());
		role.setUpdate_time(new Date());
    	roleMapper.insert(role);
    }

	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "角色")
    public void updateRole(Role role){
		role.setUpdate_time(new Date());
    	roleMapper.updateByPrimaryKey(role);
    }

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "角色")
    public void deleteById(Long role_id){
    	roleMapper.deleteByPrimaryKey(role_id);
    }
    
    public Role selectById(Long role_id) {
		return roleMapper.selectByPrimaryKey(role_id);
	}
    
    public List<Role> findAll(){
        return roleMapper.selectAll();
    }
    
  	public Object pageQuery(Sort sort,  PageConditionBean pageCondition, Long org_id ,Long user_id) {
  		PageHelper.startPage(pageCondition.getPageNum(), pageCondition.getPageSize());
  		String order = ConditionOrderUtil.prepareOrder(sort);
  		if (order != null) {
  			PageHelper.orderBy(order);
  		}
  		List<Role> result = getRoleList(pageCondition.getSearchText(), org_id, null, null,user_id);
  		PageList pageList = new PageList(result, null);
  		return pageList;
  	}

  	public List<Role> getRoleList(String searchText, Long org_id, String role_ids, Integer role_type,Long user_id) {
		List<Role> list = roleMapper.getRoleList(searchText, org_id, role_ids, role_type);
		SysUserRoleRel role = roleMapper.selectRoleByUser(user_id);
		if (role != null && org_id == 1 && role.getRole_id() != 1){
			for (int i = 0;i<list.size();i++){
				if (list.get(i).getRole_id() == 1){
					list.remove(list.get(i));
				}
			}
		}
		return list;
	}
  	
	public List<Role> getRolesByUserId(Long user_id) {
		return roleMapper.getRolesByUserId(user_id);
	}

	@Override
	public Long getMaxRoleId() {
		return roleMapper.getMaxRoleId();
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "角色")
	public void updateIsDelete(String role_ids) {
		roleMapper.updateIsDelete(role_ids);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "角色")
	public void updateIsDeleteByOrgId(Long org_id) {
		roleMapper.updateIsDeleteByOrgId(org_id);		
	}

	@Override
	public Role getRoleByNameAndOrgId(String role_name, Long root_org_id) {
		return roleMapper.getRoleByNameAndOrgId(role_name, root_org_id);
	}
}
