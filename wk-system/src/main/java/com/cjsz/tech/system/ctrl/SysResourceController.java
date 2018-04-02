package com.cjsz.tech.system.ctrl;



import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.DelResBean;
import com.cjsz.tech.system.domain.Role;
import com.cjsz.tech.system.domain.SysResource;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.RoleResRelService;
import com.cjsz.tech.system.service.RoleService;
import com.cjsz.tech.system.service.SysResourceService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 资源管理控制器
 * Created by Administrator on 2016/11/8 0008.
 */
@Controller
public class SysResourceController {

    @Autowired
    private SysResourceService sysResourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleResRelService roleResRelService;

    @Autowired
    private UserService userService;

    /**
     * 资源列表 页面跳转
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/admin/sysResource/list", method = {RequestMethod.GET})
    public String sysResourceList(HttpServletRequest request,Map<String, Object> model) {
        return "admin/pages/sysResourceList";
    }

    /**
     * 资源列表 列表数据
     *
     * @param request
     */
    @RequestMapping(value = "/admin/sysResource/json/list", method = {RequestMethod.POST})
    @ResponseBody
    public Object sysResource_list(HttpServletRequest request, @RequestBody Map param) {
        String org_id_str = param.get("org_id") + "";
        if(StringUtil.isEmpty(org_id_str)) {
            JsonResult result = JsonResult.getError("请提供组织编码！");
            return result;
        }
        Long org_id = 0L;
        try {
            org_id = Long.valueOf(org_id_str);
        }catch (Exception e) {
            JsonResult result = JsonResult.getError("请提供正确的组织编码！");
            return result;
        }
        try{
            List<SysResource> sysResources = sysResourceService.selectByOrg_id(org_id);
            List<SysResource> data = sysResourceService.selectAllSysMenuTree(sysResources);
            JsonResult result = JsonResult.getSuccess("获取数据成功！");
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResult.getError("获取数据失败！");
    }


    /**
     * 当前用户可见资源
     * @param request
     * @return
     */
    @RequestMapping(value = "/admin/sysResource/json/getMenus", method = {RequestMethod.POST})
    @ResponseBody
    public Object getMenus(HttpServletRequest request) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        try{
            List<SysResource> menuList = new ArrayList<SysResource>();
            List<Role> roles = roleService.getRolesByUserId(sysUser.getUser_id());
            List<Long> role_ids = new ArrayList<Long>();
            List<String> role_names = new ArrayList<String>();
            List<Integer> role_types = new ArrayList<Integer>();
            for(Role role : roles){
                role_ids.add(role.getRole_id());
                role_names.add(role.getRole_name());
                role_types.add(role.getRole_type());
            }
            if(role_types.contains(1) && sysUser.getOrg_id() == 1){
                //超级管理员
                menuList = sysResourceService.selectByOrg_id(sysUser.getOrg_id());
            } else if(role_types.contains(1) && sysUser.getOrg_id() != 1){
                //机构管理员
                menuList = sysResourceService.selectAllSysMenuByOrgId(sysUser.getOrg_id());
            }else{
                //其他用户
                String roleid = "";
                for(int i = 0; i < roles.size(); i++){
                    if(i != roles.size()-1){
                        roleid += roles.get(i).getRole_id() + "," ;
                    }else{
                        roleid += roles.get(i).getRole_id() + "" ;
                    }
                }
                menuList =  sysResourceService.selectAllSysMenuByRoleId(roleid);
            }
            List<SysResource> data = sysResourceService.selectAllSysMenuTree(menuList);
            JsonResult result = JsonResult.getSuccess("获取数据成功！");
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResult.getSuccess("获取数据失败！");
    }

    /**
     * 资源列表 添加、修改数据
     *
     * @param request
     */
    @RequestMapping(value = "/admin/sysResource/json/update_sysResource", method = {RequestMethod.POST})
    @ResponseBody
    public Object add_sysResource(HttpServletRequest request, @RequestBody SysResource sysResource) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        try{
            JsonResult result = JsonResult.getSuccess("更新数据成功！");
            String full_path = "";
            //根据res_key查找资源
            SysResource resource = null;
            if(!StringUtil.isEmpty(sysResource.getRes_key())) {
                resource = sysResourceService.selectByResKey(sysResource.getRes_key(), sysResource.getOrg_id());
            }
            if(sysResource.getRes_id() == null){
                //新增
                if(resource!=null){
                    return JsonResult.getError("资源标识不能重复！");
                }
                if(sysResource.getPid() != null && sysResource.getPid() != 0 ){
                    SysResource p_sysResource = sysResourceService.selectByResId(sysResource.getPid());
                    full_path = p_sysResource.getFull_path();
                }else{
                    sysResource.setPid(0L);
                    full_path = "0|";
                }
                sysResource.setEnabled(1);
                sysResource.setIs_forbid(1);//是否禁止(1: 禁止 2: 不禁止)
                sysResource.setCreate_time(new Date());
                sysResourceService.addSysMenu(sysResource);
                sysResource.setSource_id(sysResource.getRes_id());
                sysResource.setFull_path(full_path + sysResource.getRes_id() + "|");
                sysResourceService.updateSysMenu(sysResource);
            }else {
                //修改
                if( resource!=null && !(resource.getRes_id().equals(sysResource.getRes_id())) ){
                    return JsonResult.getError("资源标识不能重复！");
                }

                //获取当前修改的资源本身和下级资源
                List<SysResource> ownerResources = sysResourceService.getOwnerResources(sysResource.getOrg_id(), sysResource.getFull_path());
                for(SysResource ownerResource : ownerResources){
                    if(ownerResource.getRes_id().equals(sysResource.getPid())){
                        return JsonResult.getError(Constants.CLASS_PID_ERROR);
                    }
                }

                if(sysResource.getPid() != null && sysResource.getPid() != 0 ){
                    SysResource p_sysResource = sysResourceService.selectByResId(sysResource.getPid());
                    full_path = p_sysResource.getFull_path();
                }else{
                    sysResource.setPid(0L);
                    full_path = "0|";
                }
                String old_full_path = sysResource.getFull_path();
                String new_full_path = full_path + sysResource.getRes_id() + "|";
                if(!old_full_path.equals(new_full_path)){
                    sysResourceService.updateSysMenuByFullPath(old_full_path, new_full_path);
                    sysResource.setFull_path(new_full_path);
                }
                sysResourceService.updateSysMenu(sysResource);
            }

            result.setData(sysResource);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResult.getError("更新数据失败！");
    }

    /**
     * 资源列表 修改数据禁止状态(is_forbid(1: 禁止 2: 不禁止))
     * 当前不禁止，上级全部不禁止，下级不变
     * 当前禁止，上级不变，下级全部禁止
     *
     * @param request
     * @param sysResource
     * @return
     */
    @RequestMapping(value = "/admin/sysResource/json/is_forbid", method = {RequestMethod.POST})
    @ResponseBody
    public Object update_is_forbid(HttpServletRequest request, @RequestBody SysResource sysResource) {
        try{
            //是否禁止(1: 禁止 2: 不禁止)
            if(sysResource.getIs_forbid() == null || sysResource.getIs_forbid() == 2){
                //禁止：上级不变，下级全部禁止
                sysResourceService.updateForbidByFullPath(1, sysResource.getFull_path());
            }else{
                //不禁止：上级全部不禁止，下级不变
                String res_ids[] = sysResource.getFull_path().split("\\|");
                sysResourceService.updateForbidByResId(2, StringUtils.join(res_ids, ","));
            }
            JsonResult result = JsonResult.getSuccess("更新数据成功！");
            result.setData(sysResource);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResult.getError("更新数据失败！");
    }

    /**
     * 资源列表 修改数据启用状态(enabled(1: 启用 2: 停用))
     * 当前启用，上级全部启用，下级不变
     * 当前停用，上级不变，下级全部停用
     *
     * @param request
     */
    @RequestMapping(value = "/admin/sysResource/json/update_enabled", method = {RequestMethod.POST})
    @ResponseBody
    public Object update_enabled(HttpServletRequest request, @RequestBody SysResource sysResource) {
        try{
            if(sysResource.getEnabled() == 1){
                //停用：上级不变，下级全部停用
                sysResourceService.updateEnabledByFullPath(2, sysResource.getFull_path());
            }else{
                //启用：上级全部启用，下级不变
                String res_ids[] = sysResource.getFull_path().split("\\|");
                sysResourceService.updateEnabledByResId(1, StringUtils.join(res_ids, ","));
            }
            JsonResult result = JsonResult.getSuccess("更新数据成功！");
            result.setData(sysResource);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResult.getError("更新数据失败！");
    }

    /**
     * 资源列表 一键推送
     *
     * @param request
     */
    @RequestMapping(value = "/admin/sysResource/json/allot_sysResource", method = {RequestMethod.POST})
    @ResponseBody
    public Object allot_sysResource(HttpServletRequest request, @RequestBody Map param) {
        try{
            String org_id = param.get("org_id").toString();
            sysResourceService.allotSysResourceByOrgId(Long.valueOf(org_id));
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResult.getError("更新数据失败！");
    }


    /**
     * 资源列表 删除数据
     *
     * @param request
     */
    @RequestMapping(value = "/admin/sysResource/json/delete_sysResource", method = {RequestMethod.POST})
    @ResponseBody
    public Object delete_sysResource(HttpServletRequest request, @RequestBody DelResBean delResBean) {
        try{
            String res_ids_str = StringUtils.join(delResBean.getRes_ids(),",");
            List<SysResource> resources = sysResourceService.selectByResIds(res_ids_str);
            String full_paths = "";
            for(int i = 0; i < resources.size(); i++){
                if(i != resources.size()-1){
                    full_paths += resources.get(i).getFull_path() + ",";
                }else{
                    full_paths += resources.get(i).getFull_path();
                }
            }
            List<SysResource> sysResources = sysResourceService.selectSysMenuByFullPaths(full_paths);
            if (null == sysResources || sysResources.size() == 0){
                return JsonResult.getError("菜单未启用！");
            }
            if(StringUtils.isEmpty(delResBean.getMark())){
                if(sysResources.size()>1){
                    return JsonResult.getError("是否删除关联资源？");
                }
            }
            String res_ids = "";
            for(int i = 0; i < sysResources.size(); i ++){
                if( i != sysResources.size()-1){
                    res_ids += sysResources.get(i).getRes_id() + ",";
                }else{
                    res_ids += sysResources.get(i).getRes_id();
                }
            }

            roleResRelService.deteleByResIds(res_ids);
            sysResourceService.deleteSysMenuByFullPaths(full_paths);

            JsonResult result = JsonResult.getSuccess("删除数据成功！");
            result.setData(new ArrayList());
            return result;

        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException("删除数据失败！");
        }
    }

    /**
     * 资源列表 一键推送
     *
     * @param request
     */
    @RequestMapping(value = "/admin/sysResource/json/pushAllOrg", method = {RequestMethod.POST})
    @ResponseBody
    public Object pushAllOrg(HttpServletRequest request) {
        try{
            sysResourceService.pushResourceAllOrg();
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResult.getError("更新数据失败！");
    }

}