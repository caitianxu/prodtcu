package com.cjsz.tech.system.service;

import com.cjsz.tech.system.conditions.UserCondition;
import com.cjsz.tech.system.domain.SysUser;
import org.springframework.data.domain.Sort;

import java.util.List;


/**
 * Created by Administrator on 2016/10/25.
 */
public interface UserService {

	//新增用户
    public void saveUser(SysUser sysUser);

    //更新用户
    public void updateUser(SysUser sysUser);
    
    //根据id删除用户
    public void deleteById(Long user_id);
    
    //根据id查询用户
    public SysUser selectById(Long user_id);
    
    //查询所有用户
    public List<SysUser> findAll();
    
    //分页查询
    public Object pageQuery(Sort sort, UserCondition userCondition);

    public void updateOrgByIds(String userids);

    //删除用户
    public void updateIsDelete(String userids);
	
    //查询新增数据id
    public Long getMaxUserId();
    
    //根据id查询用户  ，关联组织和角色
    public SysUser getUserById(Long user_id);
    
    //删除用户
    public void updateIsDeleteByUserId(Long user_id);
    
    //查找登录用户
    public SysUser selectLoginUser(String user_name, String user_pwd);
    
    //通过token查找用户
    public SysUser findByToken(String token);
    
    //通过用户名查找用户
    public SysUser findByUserName(String user_name);
    
    //通过用户ids查询用户
    List<SysUser> getUserListByIds(String user_ids);
    
    //停用启用组织下的用户
    void updateEnabled(Integer enabled, String org_ids);

    //保存用户信息及用户角色关系
	public SysUser saveUserAndRel(SysUser sysUser);

	//更新用户信息及用户角色关系
	public SysUser updateUserAndRel(SysUser sysUser);

    //检查是否包含其他机构用户
    List<SysUser> getOtherUserByIds(String user_ids, Long org_id);

	//通过ids查询系统管理员用户
	public List<SysUser> getAdminUserByIds(String user_ids);

	//通过机构ids查询系统管理员用户
	public List<SysUser> getUsersByOrgIds(String orgIds);

    //通过组织ids查询系统管理员用户
    public List<SysUser> getUsersByDeptIds(String orgIds);

}
