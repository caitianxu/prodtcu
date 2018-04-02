package com.cjsz.tech.system.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.system.domain.Role;

import org.springframework.data.domain.Sort;

import java.util.List;


/**
 * Created by Administrator on 2016/10/25.
 */
public interface RoleService {

	//新增角色
    public void saveRole(Role role);

    //更新角色
    public void updateRole(Role role);
    
    //根据id删除角色
    public void deleteById(Long role_id);
    
    //根据id查询角色
    public Role selectById(Long role_id);
    
    //查询所有角色
    public List<Role> findAll();
    
    //分页查询
  	public Object pageQuery(Sort sort, PageConditionBean pageCondition, Long org_id , Long user_id);
  	
  	//根据用户id查询用户对应的角色
  	public List<Role> getRolesByUserId(Long user_id);
  	
  	//查询新增数据id
  	public Long getMaxRoleId();
  	
  	//删除角色
  	public void updateIsDelete(String role_ids);
  	
  	//删除组织下的角色
  	public void updateIsDeleteByOrgId(Long org_id);
  	
  	//查询组织下的角色
  	public List<Role> getRoleList(String searchText, Long org_id, String role_ids, Integer role_type ,Long user_id);

  	//角色名,机构id查询角色
	public Role getRoleByNameAndOrgId(String role_name, Long root_org_id);
}
