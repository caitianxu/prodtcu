package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUserRoleRel;
import com.cjsz.tech.system.mapper.UserRoleRelMapper;
import com.cjsz.tech.system.service.UserRoleRelService;
import com.cjsz.tech.system.utils.SysActionLogType;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2016/10/25.
 */
@Service
public class UserRoleRelServiceImpl implements UserRoleRelService{
	
    @Autowired
    private UserRoleRelMapper userRoleRelMapper;

    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "用户角色关系")
	public void saveRel(SysUserRoleRel rel) {
		rel.setEnabled(Constants.ENABLE);
		rel.setCreate_time(new Date());
		rel.setIs_delete(Constants.NOT_DELETE);
		rel.setUpdate_time(new Date());
		userRoleRelMapper.insert(rel);
	}

    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "用户角色关系")
	public void updateRel(SysUserRoleRel rel) {
    	rel.setUpdate_time(new Date());
		userRoleRelMapper.updateByPrimaryKey(rel);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "用户角色关系")
	public void deleteById(Long rel_id) {
		userRoleRelMapper.deleteByPrimaryKey(rel_id);
	}

	@Override
	public SysUserRoleRel selectById(Long rel_id) {
		return userRoleRelMapper.selectByPrimaryKey(rel_id);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "用户角色关系")
	public void deleteByRoleAndUser(String role_id, Long user_id) {
		userRoleRelMapper.deleteByRoleAndUser(role_id, user_id);		
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "用户角色关系")
	public void deteleByUser(Long user_id) {
		userRoleRelMapper.deleteByUser(user_id);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "用户角色关系")
	public void deteleByRole(Long role_id) {
		userRoleRelMapper.deleteByRole(role_id);
	}

	@Override
	public List<SysUserRoleRel> getRelListByRole(String role_id) {
		return userRoleRelMapper.getRelListByRole(role_id);
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "用户角色关系")
	public void updateIsDeleteByRole(String role_id) {
		userRoleRelMapper.updateIsDeleteByRole(role_id);		
	}

	@Override
	public SysUserRoleRel getRelByRoleAndUser(Long role_id, Long user_id) {
		return userRoleRelMapper.getRelByRoleAndUser(role_id, user_id);
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "用户角色关系")
	public void updateIsDeleteByUser(Long user_id) {
		userRoleRelMapper.updateIsDeleteByUser(user_id);		
	}

	@Override
	public Long getMaxRelId() {
		return userRoleRelMapper.getMaxRelId();
	}

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "用户角色关系")
	public void updateIsDeleteByUsers(String user_ids) {
		userRoleRelMapper.updateIsDeleteByUsers(user_ids);		
	}

	@Override
	public List<SysUserRoleRel> getRelListByUser(String user_ids) {
		return userRoleRelMapper.getRelListByUser(user_ids);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "用户角色关系")
	public void saveUserRoleRel(Long role_id, Long userId) {
		SysUserRoleRel rel = getRelByRoleAndUser(role_id, userId);
		if(rel == null){
			rel = new SysUserRoleRel();
			rel.setUser_id(userId);
			rel.setRole_id(role_id);
			rel.setEnabled(Constants.ENABLE);
			rel.setCreate_time(new Date());
			rel.setIs_delete(Constants.NOT_DELETE);
			rel.setUpdate_time(new Date());
			userRoleRelMapper.insert(rel);
		}else{
			rel.setIs_delete(2);
			rel.setUpdate_time(new Date());
			userRoleRelMapper.updateByPrimaryKey(rel);
		}		
	}
}
