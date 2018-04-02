package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.conditions.UserCondition;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.domain.SysUserRoleRel;
import com.cjsz.tech.system.mapper.UserMapper;
import com.cjsz.tech.system.service.UserRoleRelService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.utils.PasswordUtil;
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
public class UserServiceImpl implements UserService {
	
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserRoleRelService userRoleRelService;

	@Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "用户")
    public void saveUser(SysUser sysUser){
    	sysUser.setEnabled(Constants.ENABLE); //启用
		sysUser.setIs_delete(Constants.NOT_DELETE);
        userMapper.insert(sysUser);
    }

    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "用户")
    public void updateUser(SysUser sysUser){
        userMapper.updateByPrimaryKey(sysUser);
    }

	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "用户")
    public void deleteById(Long user_id){
        userMapper.deleteByPrimaryKey(user_id);
    }
    
    public SysUser selectById(Long user_id) {
		return userMapper.selectByPrimaryKey(user_id);
	}
    
    public List<SysUser> findAll(){
        return userMapper.selectAll();
    }

	public Object pageQuery(Sort sort,  UserCondition userCondition) {
		PageHelper.startPage(userCondition.getPageNum(), userCondition.getPageSize());
		String order = ConditionOrderUtil.prepareOrder(sort);
		if (order != null) {
			PageHelper.orderBy(order);
		}
		List<SysUser> result = userMapper.getUserList(userCondition);
		PageList pageList = new PageList(result, null);
		return pageList;
	}
    
	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "用户")
	public void updateOrgByIds(String userids) {
		userMapper.updateOrgByIds(userids);
	}
	
	@SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "用户")
	public void updateIsDelete(String userids) {
		userMapper.updateIsDelete(userids);
	}

	@Override
	public Long getMaxUserId() {
		return userMapper.getMaxUserId();
	}

	@Override
	public SysUser getUserById(Long user_id) {
		return userMapper.getUserById(user_id);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "用户")
	public void updateIsDeleteByUserId(Long user_id) {
		userMapper.updateIsDeleteByUserId(user_id);		
	}

	@Override
	public SysUser selectLoginUser(String user_name, String user_pwd) {
		String encodePwd = PasswordUtil.entryptPassword(user_pwd);
		return userMapper.selectLoginUser(user_name, encodePwd);
	}

	@Override
	public SysUser findByToken(String token) {
		return userMapper.findByToken(token);
	}

	@Override
	public SysUser findByUserName(String user_name) {
		return userMapper.findByUserName(user_name);
	}

	@Override
	public List<SysUser> getUserListByIds(String user_ids) {
		return userMapper.getUserListByIds(user_ids);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "用户")
	public void updateEnabled(Integer enabled, String org_ids) {
		userMapper.updateEnabled(enabled, org_ids);
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "用户")
	public SysUser saveUserAndRel(SysUser sysUser) {
		String md5Pwd = PasswordUtil.entryptPassword(sysUser.getUser_pwd());
		sysUser.setUser_pwd(md5Pwd);
		sysUser.setToken("");
		sysUser.setEnabled(Constants.ENABLE); //启用
		sysUser.setIs_delete(Constants.NOT_DELETE);
		userMapper.insert(sysUser);
		Long userId = userMapper.getMaxUserId();
		//2.新增角色用户关系
		String[] role_ids = sysUser.getRole_id().split(",");
		for(String role_id: role_ids){
			userRoleRelService.saveUserRoleRel(Long.valueOf(role_id), userId);
		}
   		SysUser user = getUserById(userId);
		return user;
	}

	@Override
	@SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "用户")
	public SysUser updateUserAndRel(SysUser sysUser) {
		SysUser user = selectById(sysUser.getUser_id());
		//停用启用
		if(user.getEnabled() != sysUser.getEnabled()){
			user.setEnabled(sysUser.getEnabled());
		}else{
			user.setUser_real_name(sysUser.getUser_real_name());
			user.setDept_id(sysUser.getDept_id());
			user.setSex(sysUser.getSex());
			user.setBirth(sysUser.getBirth());
			user.setIcon(sysUser.getIcon());
			user.setEmail(sysUser.getEmail());
			user.setPhone(sysUser.getPhone());
			user.setAddress(sysUser.getAddress());
			//更新角色用户关系
			SysUserRoleRel sysUserRoleRel = userRoleRelService.getRelListByUser(user.getUser_id().toString()).get(0);
			String[] role_ids = sysUser.getRole_id().split(",");
			if(!sysUserRoleRel.getRole_id().toString().equals(role_ids[0])){
				sysUserRoleRel.setRole_id(Long.valueOf(role_ids[0]));
				sysUserRoleRel.setUpdate_time(new Date());
				userRoleRelService.updateRel(sysUserRoleRel);
			}
		}
		userMapper.updateByPrimaryKey(user);
		SysUser data = getUserById(user.getUser_id());
		return data;
	}

	@Override
	public List<SysUser> getOtherUserByIds(String user_ids, Long org_id) {
		return userMapper.getOtherUserByIds(user_ids, org_id);
	}

	@Override
	public List<SysUser> getAdminUserByIds(String user_ids) {
		return userMapper.getAdminUserByIds(user_ids);
	}

	@Override
	public List<SysUser> getUsersByOrgIds(String orgIds) {
		return userMapper.getUsersByOrgIds(orgIds);
	}

	@Override
	public List<SysUser> getUsersByDeptIds(String orgIds) {
		return userMapper.getUsersByDeptIds(orgIds);
	}
}
