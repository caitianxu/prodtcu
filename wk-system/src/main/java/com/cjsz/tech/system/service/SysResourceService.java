package com.cjsz.tech.system.service;

import com.cjsz.tech.system.domain.SysResource;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Administrator on 2016/11/8 0008.
 */
public interface SysResourceService {

    /**
     * 新增菜单
     * @param sysResource
     */
    public void addSysMenu(SysResource sysResource);

    /**
     * 新增菜单
     * @param sysResources
     */
    public void addSysMenus(List<SysResource> sysResources);

    /**
     * 删除菜单
     * @param res_id
     */
    public void deleteSysMenu(Long res_id);

    /**
     * 通过full_path删除菜单
     * @param full_path
     */
    public void deleteSysMenuByFullPath(String full_path);

    /**
     * 通过full_paths删除菜单
     * @param full_paths
     */
    public void deleteSysMenuByFullPaths(String full_paths);

    /**
     * 更新菜单内容
     * @param sysResource
     */
    public void updateSysMenu(SysResource sysResource);

    /**
     * 通过 res_id 查询菜单
     * @param res_id
     * @return
     */
    public SysResource selectByResId(Long res_id);


    /**
     * 通过 res_ids 查询菜单
     * @param res_ids
     * @return
     */
    public List<SysResource> selectByResIds(String res_ids);

    /**
     * 查询超级管理员菜单
     * @return
     */
    public List<SysResource> getAllRes();

    /**
     * 查询所有启用菜单
     * @return
     */
    public List<SysResource> selectAllSysMenu();

    /**
     * 通过用户的根org_id 查询所有菜单
     * @return
     */
    public List<SysResource> selectAllSysMenuByOrgId(Long org_id);

    /**
     * 通过用户的role_id（可能多个，以'，'隔开）查询所有菜单
     * @return
     */
    public List<SysResource> selectAllSysMenuByRoleId(String role_id);

    /**
     * 菜单_树结构化
     * @param sysResources
     * @return
     */
    public List<SysResource> selectAllSysMenuTree(List<SysResource> sysResources);

    /**
     * 通过full_path查询菜单
     * @param full_path
     * @return
     */
    public List<SysResource> selectSysMenuByFullPath(String full_path);

    /**
     * 通过full_paths查询菜单
     * @param full_paths
     * @return
     */
    public List<SysResource> selectSysMenuByFullPaths(String full_paths);

    /**
     * 通过 roleid 查询顶部菜单
     * @param roleid
     * @return
     */
    public List<SysResource> selectTopMenuList(String roleid);

    /**
     * 通过 roleid 查询左侧菜单
     * @param pid
     * @param full_path
     * @param roleid
     * @return
     */
    public List<SysResource> selectLeftMenuList(Long pid, String full_path, String roleid);

    /**
     * 后台页面菜单分页列表展示
     * @param sort
     * @param page
     * @param rows
     * @param searchText
     * @return
     */
    public Object pageQuery(Sort sort, Integer page, Integer rows, String searchText);

	List<SysResource> selectMenuTree();

    /**
     * 资源换上级时 更新full_path
     * @param old_full_path
     * @param new_full_path
     */
    public void updateSysMenuByFullPath(String old_full_path, String new_full_path);

    /**
     * 根据组织ID查询资源
     * @param org_id
     * @return
     */
    public List<SysResource> selectByOrg_id(Long org_id);

    /**
     * 根据res_key,org_id查询资源
     * @param res_key
     * @param org_id
     * @return
     */
    public SysResource selectByResKey(String res_key, Long org_id);

    /**
     * 删除机构下所有资源
     * @param org_id
     */
    public void deleteByOrg_id(Long org_id);

    /**
     * 一键推送资源
     * @param org_id
     */
    public void allotSysResourceByOrgId(Long org_id);

    /**
     * 上级不变，自身包括下级全部更新启用状态
     * @param enabled
     * @param full_path
     */
    public void updateEnabledByFullPath(Integer enabled, String full_path);

    /**
     * 自身包括上级全部启用，下级不变
     * @param enabled
     * @param res_ids
     */
    public void updateEnabledByResId(Integer enabled, String res_ids);

    /**
     * 上级不变，自身包括下级全部更新为禁止
     * @param is_forbid
     * @param full_path
     */
    public void updateForbidByFullPath(Integer is_forbid, String full_path);

    /**
     * 自身包括上级全部不禁止，下级不变
     * @param is_forbid
     * @param join
     */
    public void updateForbidByResId(Integer is_forbid, String join);

    /**
     * 获取当前资源本身和下级资源
     * @param org_id
     * @param full_path
     * @return
     */
    public List<SysResource> getOwnerResources(Long org_id, String full_path);

    /**
     * 一键推送资源
     */
    public void pushResourceAllOrg();
}
