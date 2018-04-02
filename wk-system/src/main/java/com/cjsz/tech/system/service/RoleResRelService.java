package com.cjsz.tech.system.service;

import com.cjsz.tech.system.beans.RoleResRelBean;
import com.cjsz.tech.system.domain.SysRoleResRel;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * 角色资源关系
 * Created by Administrator on 2016/10/25.
 */
public interface RoleResRelService {

	// 新增用户角色关系--单条
    public void saveRel(SysRoleResRel rel);

    // 新增用户角色关系--多条
    public void saveRels(List<SysRoleResRel> rels);

    //更新
    public void updateRel(SysRoleResRel rel);
    
    //根据id删除关系
    public void deleteById(Long rel_id);
    
    //根据id查询关系
    public SysRoleResRel selectById(Long rel_id);
    
    //根据角色id和资源id删除关系
    public void deleteByRoleAndUser(Long role_id, Long res_id);
	
    //根据资源id删除关系
    public void deteleByRes(Long res_id);

    //根据 多条资源id字符串 删除关系
    public void deteleByResIds(String res_ids);

    //根据角色ids删除关系
    public void deteleByRoles(String role_id);

    //根据角色id删除关系
    public void deteleByRole(Long role_id);
    
    //根据角色id查询关系
    public List<SysRoleResRel> getRelListByRole(String role_id);

    //根据角色id查询权限——书
    public List<RoleResRelBean> selectAllRelTree(List<RoleResRelBean> relBeans);

    //根据权限表ID更改启用状态
    public void updateRelEnabled(Long rel_id, Integer enabled);

    //权限列表 分页查询
    public Object pageQuery(Sort sort, RoleResRelBean relBean, Long org_id,Long user_id);

    //根据权限表ID更改数据权限
    public void updateRelDataType(Long role_res_rel_id, Integer data_type_id);

    //取消权限
    public void deleteByRoleIdAndResIds(String res_ids, Long role_id);
}
