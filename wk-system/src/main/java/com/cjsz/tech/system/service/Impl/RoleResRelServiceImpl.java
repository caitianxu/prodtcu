package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.RoleResRelBean;
import com.cjsz.tech.system.domain.SysUserRoleRel;
import com.cjsz.tech.system.mapper.RoleMapper;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cjsz.tech.system.domain.SysRoleResRel;
import com.cjsz.tech.system.mapper.RoleResRelMapper;
import com.cjsz.tech.system.service.RoleResRelService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
@Service
public class RoleResRelServiceImpl implements RoleResRelService {
	
	@Autowired
    private RoleResRelMapper roleResRelMapper;

	@Autowired
	private RoleMapper roleMapper;

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "资源权限")
	public void saveRel(SysRoleResRel rel) {
		rel.setUpdate_time(new Date());
		roleResRelMapper.insert(rel);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "资源权限")
	public void saveRels(List<SysRoleResRel> rels) {
		roleResRelMapper.insertList(rels);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资源权限")
	public void updateRel(SysRoleResRel rel) {
		rel.setUpdate_time(new Date());
		roleResRelMapper.updateByPrimaryKey(rel);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资源权限")
	public void deleteById(Long rel_id) {
		roleResRelMapper.deleteByPrimaryKey(rel_id);
	}

	@Override
	public SysRoleResRel selectById(Long rel_id) {
		return roleResRelMapper.selectByPrimaryKey(rel_id);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "资源权限")
	public void deleteByRoleAndUser(Long role_id, Long res_id) {
		roleResRelMapper.deleteByRoleAndRes(role_id, res_id);		
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "资源权限")
	public void deteleByRes(Long res_id) {
		roleResRelMapper.deleteByRes(res_id);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "资源权限")
	public void deteleByRoles(String role_id) {
		roleResRelMapper.deleteByRoles(role_id);
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "资源权限")
	public void deteleByResIds(String res_ids) {
		roleResRelMapper.deteleByResIds(res_ids);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "资源权限")
	public void deteleByRole(Long role_id) {
		roleResRelMapper.deleteByRole(role_id);
	}
	
	@Override
	public List<SysRoleResRel> getRelListByRole(String role_id) {
		return roleResRelMapper.getRelListByRole(role_id);
	}

	@Override
	public List<RoleResRelBean> selectAllRelTree(List<RoleResRelBean> relBeans) {
		List<RoleResRelBean> rstList  = new ArrayList<RoleResRelBean>();
		while(relBeans.size()>0){
			RoleResRelBean curRel = relBeans.get(0);
			if(!rstList.contains(curRel)) {
				rstList.add(curRel);
			}
			relBeans.remove(0);
			List<RoleResRelBean> children = this.getChildren(relBeans, curRel.getRes_id());
			curRel.setChildren(children);
		}
		return rstList;
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资源权限")
	public void updateRelEnabled(Long rel_id, Integer enabled) {
		roleResRelMapper.updateRelEnabled(rel_id, enabled);
	}

	@Override
	public Object pageQuery(Sort sort, RoleResRelBean relBean, Long org_id,Long user_id) {
		SysUserRoleRel role = roleMapper.selectRoleByUser(user_id);
		PageHelper.startPage(relBean.getPageNum(), relBean.getPageSize());
		String order = ConditionOrderUtil.prepareOrder(sort);
		if (order != null) {
			PageHelper.orderBy(order);
		}
		List<RoleResRelBean> result = new ArrayList<>();
		if (org_id == 1 && role.getRole_id() != 1){
			result = roleResRelMapper.getAllRelByRoleAndPidAndOrgIdAndRole(relBean.getRole_id(), relBean.getPid(), org_id , role.getRole_id());
		}else {
			result = roleResRelMapper.getAllRelByRoleAndPidAndOrgId(relBean.getRole_id(), relBean.getPid(), org_id);
		}
		PageList pageList = new PageList(result, null);
		return pageList;
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资源权限")
	public void updateRelDataType(Long role_res_rel_id, Integer data_type_id) {
		roleResRelMapper.updateRelDataType(role_res_rel_id, data_type_id);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "资源权限")
	public void deleteByRoleIdAndResIds(String res_ids, Long role_id) {
		roleResRelMapper.deleteByRoleIdAndResIds(res_ids, role_id);
	}

	private List<RoleResRelBean> getChildren(List<RoleResRelBean> allList, Long pid){
		List<RoleResRelBean> children =new ArrayList<RoleResRelBean>();
		List<RoleResRelBean> copyList = new ArrayList<RoleResRelBean>();
		copyList.addAll(allList);
		for(RoleResRelBean curRel :copyList){
			if(curRel.getPid().equals(pid)){
				curRel.setChildren(this.getChildren(allList, curRel.getRes_id()));
				children.add(curRel);
				allList.remove(curRel);
			}
		}
		return children;
	}

}
