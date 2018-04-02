package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.core.page.BaseWrapper;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.domain.SysResource;
import com.cjsz.tech.system.mapper.SysResourceMapper;
import com.cjsz.tech.system.service.SysResourceService;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/11/8 0008.
 */
@Service
public class SysResourceServiceImpl implements SysResourceService {

    @Autowired
    private SysResourceMapper sysResourceMapper;

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "资源")
    public void addSysMenu(SysResource sysResource) {
    	sysResource.setUpdate_time(new Date());
        sysResourceMapper.insert(sysResource);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "资源")
    public void addSysMenus(List<SysResource> sysResources) {
        sysResourceMapper.insertList(sysResources);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "资源")
    public void deleteSysMenu(Long res_id) {
        sysResourceMapper.deleteById(res_id);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "资源")
    public void deleteSysMenuByFullPath(String full_path) {
        sysResourceMapper.deleteByFullPath(full_path);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "资源")
    public void deleteSysMenuByFullPaths(String full_paths) {
        String[] full_path_arr = full_paths.split(",");
        for(String full_path : full_path_arr){
            sysResourceMapper.deleteByFullPath(full_path);
        }
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资源")
    public void updateSysMenu(SysResource sysResource) {
        SysResource org_sysResource = sysResourceMapper.findById(sysResource.getRes_id());
        BeanUtils.copyProperties(sysResource, org_sysResource);
        sysResource.setUpdate_time(new Date());
        sysResourceMapper.updateByPrimaryKey(sysResource);
    }

    @Override
    public SysResource selectByResId(Long res_id) {
        return sysResourceMapper.findById(res_id);
    }

    @Override
    public List<SysResource> selectByResIds(String res_ids) {
        return sysResourceMapper.selectByResIds(res_ids);
    }

    @Override
    public List<SysResource> getAllRes() {
        return sysResourceMapper.getAllRes();
    }

    @Override
    public List<SysResource> selectAllSysMenu() {
        return sysResourceMapper.findAll();
    }

    @Override
    public List<SysResource> selectAllSysMenuByOrgId(Long org_id){
        return sysResourceMapper.selectAllSysMenuByOrgId(org_id);
    }

    @Override
    public List<SysResource> selectAllSysMenuByRoleId(String role_id) {
        return sysResourceMapper.findAllByRoleId(role_id);
    }

    @Override
    public List<SysResource> selectAllSysMenuTree(List<SysResource> menuList) {
        List<SysResource> rstList  = new ArrayList<SysResource>();
        while(menuList.size()>0){
            SysResource curMenu = menuList.get(0);
            if(!rstList.contains(curMenu)) {
                rstList.add(curMenu);
            }
            menuList.remove(0);
            List<SysResource> children = this.getChildren(menuList, curMenu.getRes_id());
            curMenu.setChildren(children);
        }
        return rstList;
    }

    @Override
    public List<SysResource> selectSysMenuByFullPath(String full_path) {
        return sysResourceMapper.findByFullPath(full_path);
    }

    @Override
    public List<SysResource> selectSysMenuByFullPaths(String full_paths) {
        String[] full_path_arr = full_paths.split(",");
        List<SysResource> resources = new ArrayList<SysResource>();
        for(String full_path : full_path_arr){
            resources.addAll(sysResourceMapper.findByFullPath(full_path));
        }
        return resources;
    }
    
    @Override
    public List<SysResource> selectMenuTree() {
    	List<SysResource> menuList =  sysResourceMapper.findAll();
        List<SysResource> rstList  = new ArrayList<SysResource>();
        while(menuList.size()>0){
            SysResource curMenu = menuList.get(0);
            if(!rstList.contains(curMenu)) {
                rstList.add(curMenu);
            }
            menuList.remove(0);
            List<SysResource> children = this.getChildren(menuList, curMenu.getRes_id());
            curMenu.setChildren(children);
        }
        return rstList;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资源")
    public void updateSysMenuByFullPath(String old_full_path, String new_full_path) {
        sysResourceMapper.updateSysMenuByFullPath(old_full_path, new_full_path);
    }

    @Override
    public List<SysResource> selectByOrg_id(Long org_id) {
        return sysResourceMapper.selectByOrg_id(org_id);
    }

    @Override
    public SysResource selectByResKey(String res_key, Long org_id) {
        return sysResourceMapper.selectByResKey(res_key, org_id);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "资源")
    public void deleteByOrg_id(Long org_id) {
        sysResourceMapper.deleteByOrg_id(org_id);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "资源分配")
    public void allotSysResourceByOrgId(Long org_id) {
        //获取超级管理员机构所有资源(未被禁止的)
        List<SysResource> sysResources = sysResourceMapper.selectByOrgIdAndForbid(1L, 2);//是否禁止(1: 禁止 2: 不禁止 null:不禁止)
        //删除推送机构下的所有资源
        sysResourceMapper.deleteByOrg_id(org_id);
        //新增所有资源给推送机构，设为启用（enabled=1）
        for(SysResource sysResource : sysResources){
            SysResource new_resource = new SysResource();
            new_resource.setPid(sysResource.getPid());
            new_resource.setOrg_id(org_id);
            new_resource.setFull_path(sysResource.getFull_path());
            new_resource.setOrder_weight(sysResource.getOrder_weight());
            new_resource.setRes_name(sysResource.getRes_name());
            new_resource.setRes_key(sysResource.getRes_key());
            new_resource.setRes_url(sysResource.getRes_url());
            new_resource.setRes_icon(sysResource.getRes_icon());
            new_resource.setRes_type(sysResource.getRes_type());
            new_resource.setEnabled(1);
            new_resource.setCreate_time(new Date());
            new_resource.setSource_id(sysResource.getRes_id());
            new_resource.setUpdate_time(new Date());
            sysResourceMapper.insert(new_resource);
            //更改pid、full_path
            if(new_resource.getPid() == 0){
                new_resource.setFull_path("0|" + new_resource.getRes_id() + "|");
            }else{
                //查找上级资源
                SysResource resource = sysResourceMapper.selectBySourceIdAndOrgId(new_resource.getPid(), org_id);
                new_resource.setPid(resource.getRes_id());
                new_resource.setFull_path(resource.getFull_path() + new_resource.getRes_id()+ "|");
            }
            sysResourceMapper.updateByPrimaryKey(new_resource);
        }
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资源")
    public void updateEnabledByFullPath(Integer enabled, String full_path) {
        sysResourceMapper.updateEnabledByFullPath(enabled, full_path);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资源")
    public void updateEnabledByResId(Integer enabled, String res_ids) {
        sysResourceMapper.updateEnabledByResId(enabled, res_ids);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资源")
    public void updateForbidByFullPath(Integer is_forbid, String full_path) {
        sysResourceMapper.updateForbidByFullPath(is_forbid, full_path);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资源")
    public void updateForbidByResId(Integer is_forbid, String res_ids) {
        sysResourceMapper.updateForbidByResId(is_forbid, res_ids);
    }

    @Override
    //获取当前资源本身和下级资源
    public List<SysResource> getOwnerResources(Long org_id, String full_path) {
        return sysResourceMapper.getOwnerResources(org_id, full_path);
    }

    private List<SysResource> getChildren(List<SysResource> allList, Long pid){
        List<SysResource> children =new ArrayList<SysResource>();
        List<SysResource> copyList = new ArrayList<SysResource>();
        copyList.addAll(allList);
        for(SysResource curMenu :copyList){
            if(curMenu.getPid().equals(pid)){
                curMenu.setChildren(this.getChildren(allList, curMenu.getRes_id()));
                children.add(curMenu);
                allList.remove(curMenu);
            }
        }
        return children;
    }

    @Override
    public List<SysResource> selectTopMenuList(String roleid) {
        return sysResourceMapper.selectTopMenuList(roleid);
    }

    @Override
    public List<SysResource> selectLeftMenuList(Long pid, String full_path, String roleid) {
        return sysResourceMapper.selectLeftMenuList(pid, full_path, roleid);
    }

    @Override
    public Object pageQuery(Sort sort, Integer page, Integer rows, String searchText) {
    	List<SysResource> result = selectMenuTree();
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(page, rows);
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        
        
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "资源分配")
    public void pushResourceAllOrg() {
        //获取超级管理员机构所有资源(未被禁止的)
        List<SysResource> sysResources = sysResourceMapper.selectByOrgIdAndForbid(1L, 2);//是否禁止(1: 禁止 2: 不禁止 null:不禁止)
        //删除推送机构下的所有资源
        sysResourceMapper.deleteAllByOrg_id();
        //获取除超级管理员机构外所有机构ID
        List<Long> org_ids = sysResourceMapper.getOrgIds();
        //新增所有资源给推送机构，设为启用（enabled=1）
        for (Long org_id : org_ids) {
            for (SysResource sysResource : sysResources) {
                SysResource new_resource = new SysResource();
                new_resource.setPid(sysResource.getPid());
                new_resource.setOrg_id(org_id);
                new_resource.setFull_path(sysResource.getFull_path());
                new_resource.setOrder_weight(sysResource.getOrder_weight());
                new_resource.setRes_name(sysResource.getRes_name());
                new_resource.setRes_key(sysResource.getRes_key());
                new_resource.setRes_url(sysResource.getRes_url());
                new_resource.setRes_icon(sysResource.getRes_icon());
                new_resource.setRes_type(sysResource.getRes_type());
                new_resource.setEnabled(1);
                new_resource.setCreate_time(new Date());
                new_resource.setSource_id(sysResource.getRes_id());
                new_resource.setUpdate_time(new Date());
                sysResourceMapper.insert(new_resource);
                //更改pid、full_path
                if (new_resource.getPid() == 0) {
                    new_resource.setFull_path("0|" + new_resource.getRes_id() + "|");
                } else {
                    //查找上级资源
                    SysResource resource = sysResourceMapper.selectBySourceIdAndOrgId(new_resource.getPid(), org_id);
                    if (resource != null) {
                        new_resource.setPid(resource.getRes_id());
                        new_resource.setFull_path(resource.getFull_path() + new_resource.getRes_id() + "|");
                    }
                }
                sysResourceMapper.updateByPrimaryKey(new_resource);
            }
        }
    }
}
