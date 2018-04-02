package com.cjsz.tech.system.service;

import java.util.List;

import com.cjsz.tech.system.domain.SysUserRoleRel;

/** 
 * 用户角色关系
 * Created by Administrator on 2016/10/25.
 */
public interface UserRoleRelService {

	// 新增用户角色关系
    public void saveRel(SysUserRoleRel rel);

    //更新
    public void updateRel(SysUserRoleRel rel);
    
    //根据id删除关系
    public void deleteById(Long rel_id);
    
    //根据id查询关系
    public SysUserRoleRel selectById(Long rel_id);
    
    //根据角色id和用户id删除关系
    public void deleteByRoleAndUser(String role_id, Long user_id);
	
    //根据用户id删除关系
    public void deteleByUser(Long user_id);
    
    //根据角色id删除关系
    public void deteleByRole(Long role_id);
    
    //根据角色ids查询关系
    public List<SysUserRoleRel> getRelListByRole(String role_id);
    
    //根据角色id删除
    public void updateIsDeleteByRole(String role_id);
    
    //根据角色id和用户id查询关系
    SysUserRoleRel getRelByRoleAndUser(Long role_id, Long user_id);
    
    //根据用户id删除
    public void updateIsDeleteByUser(Long user_id);
    
    //根据用户ids删除
    public void updateIsDeleteByUsers(String user_ids);
    
    //查询新增数据的id
    public Long getMaxRelId();
    
   //根据用户ids查询关系
    public List<SysUserRoleRel> getRelListByUser(String user_ids);
    
    //根据用户和角色查询关系是否存在，添加，更新用户角色关系
    public void saveUserRoleRel(Long role_id, Long userId);
}
