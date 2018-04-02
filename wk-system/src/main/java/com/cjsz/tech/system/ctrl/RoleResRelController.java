package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.RoleResRelBean;
import com.cjsz.tech.system.beans.SysRoleResRelBean;
import com.cjsz.tech.system.domain.SysResource;
import com.cjsz.tech.system.domain.SysRoleResRel;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.RoleResRelService;
import com.cjsz.tech.system.service.SysResourceService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.system.utils.ListUtils;
import com.cjsz.tech.system.utils.RequestContextUtil;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 权限管理控制器
 * Created by Administrator on 2016/11/9 0009.
 */
@Controller
public class RoleResRelController {

    @Autowired
    private RoleResRelService roleResRelService;

    @Autowired
    private SysResourceService sysResourceService;

    @Autowired
    private UserService userService;


    /**
     * 权限列表 页面跳转
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/admin/sysRoleResRel/list", method = {RequestMethod.GET})
    public String sysRoleResRelList(HttpServletRequest request,Map<String, Object> model) {
        return "admin/pages/sysRoleResRel";
    }

    /**
     * 权限列表 列表数据
     *
     * @param request
     */
    @RequestMapping(value = "/admin/sysRoleResRel/json/list", method = {RequestMethod.POST})
    @ResponseBody
    public Object sysRoleResRel_list(HttpServletRequest request,@RequestBody RoleResRelBean relBean ) {
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            Sort sort = new Sort(Sort.Direction.ASC, "path_length").and(new Sort(Sort.Direction.DESC, "order_weight"));
            PageList data = (PageList)roleResRelService.pageQuery(sort, relBean, sysUser.getOrg_id(),sysUser.getUser_id());
            JsonResult result = JsonResult.getSuccess("获取数据成功！");
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResult.getError("获取数据失败！");
    }

    /**
     * 增加/删除菜单权限——单条
     * @param request
     * @param relBean
     * @return
     */
    @RequestMapping(value = "/admin/sysRoleResRel/json/update_sysRoleResRel", method = {RequestMethod.POST})
    @ResponseBody
    public Object update_sysRoleResRel(HttpServletRequest request, @RequestBody RoleResRelBean relBean) {
        try{
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if(relBean.getRole_res_rel_id() == null){
                SysResource resource = sysResourceService.selectByResId(relBean.getRes_id());
                String full_path = resource.getFull_path();
                String[] res_ids = full_path.substring(0, full_path.length()).split("\\|");
                List<String> res_id_list = Arrays.asList(res_ids);

                List<String> own_res_id_list = new ArrayList<String>();
                List<SysRoleResRel> roleResRels = roleResRelService.getRelListByRole(relBean.getRole_id().toString());
                for(SysRoleResRel roleResRel : roleResRels){
                    own_res_id_list.add(roleResRel.getRes_id().toString());
                }
                res_id_list = ListUtils.diff(res_id_list, own_res_id_list);

                List<SysRoleResRel> sysRoleResRels = new ArrayList<SysRoleResRel>();
                for(int i = 1; i < res_id_list.size(); i++ ){
                    SysRoleResRel sysRoleResRel = new SysRoleResRel();
                    sysRoleResRel.setRole_id(relBean.getRole_id());
                    sysRoleResRel.setRes_id(Long.valueOf(res_id_list.get(i)));
                    if(i != (res_ids.length-1)){
                        sysRoleResRel.setData_type_id(1);
                    }else{
                        sysRoleResRel.setData_type_id(1);
                    }
                    sysRoleResRel.setEnabled(1);
                    sysRoleResRel.setCreate_time(new Date());
                    sysRoleResRel.setUpdate_time(new Date());
                    sysRoleResRels.add(sysRoleResRel);
                }
                roleResRelService.saveRels(sysRoleResRels);
            }else{
                SysRoleResRel roleResRel = roleResRelService.selectById(relBean.getRole_res_rel_id());
                SysResource resource = sysResourceService.selectByResId(roleResRel.getRes_id());
                String full_path = resource.getFull_path();
                List<SysResource> resources = sysResourceService.selectSysMenuByFullPath(full_path);
                String res_ids = "";
                for(int i = 0; i<resources.size(); i++){
                    if(i != resources.size()-1){
                        res_ids += resources.get(i).getRes_id() + ",";
                    }else{
                        res_ids += resources.get(i).getRes_id();
                    }
                }
                roleResRelService.deleteByRoleIdAndResIds(res_ids, roleResRel.getRole_id());
            }
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResult.getError("更新数据失败！");
    }

    /**
     * 修改菜单权限的数据权限——单条
     * @param request
     * @param relBean
     * @return
     */
    @RequestMapping(value = "/admin/sysRoleResRel/json/update_data_type", method = {RequestMethod.POST})
    @ResponseBody
    public Object update_data_type(HttpServletRequest request, @RequestBody RoleResRelBean relBean) {
        try{
            roleResRelService.updateRelDataType(relBean.getRole_res_rel_id(), relBean.getData_type_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResult.getError("更新数据失败！");
    }

    /**
     * 启用/停用 菜单权限——单条
     * @param request
     * @param relBean
     * @return
     */
    @RequestMapping(value = "/admin/sysRoleResRel/json/update_relEnabled", method = {RequestMethod.POST})
    @ResponseBody
    public Object update_relEnabled(HttpServletRequest request, @RequestBody RoleResRelBean relBean) {
        try{
            if(relBean.getEnabled() == 1){
                relBean.setEnabled(2);
            }else{
                relBean.setEnabled(1);
            }
            roleResRelService.updateRelEnabled(relBean.getRole_res_rel_id(),relBean.getEnabled());
            JsonResult result = JsonResult.getSuccess("更新数据成功");
            result.setData(relBean);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResult.getError("更新数据失败！");
    }

    /**
     * 增加/删除菜单权限——多条
     * @param request
     * @param relBean
     * @return
     */
    @RequestMapping(value = "/admin/sysRoleResRel/json/update_sysRoleResRels", method = {RequestMethod.POST})
    @ResponseBody
    public Object update_sysRoleResRels(HttpServletRequest request, @RequestBody SysRoleResRelBean relBean) {
        try{
            roleResRelService.deteleByRole(relBean.getRole_id());
            List<SysRoleResRelBean.RelIds> relIdsList = relBean.getRel_ids();
            List<SysRoleResRel> roleResRels = new ArrayList<SysRoleResRel>();
            for(int i = 0; i < relIdsList.size(); i++){
                SysRoleResRel sysRoleResRel = new SysRoleResRel();
                sysRoleResRel.setRole_id(relBean.getRole_id());
                sysRoleResRel.setRes_id(relIdsList.get(i).res_id);
                sysRoleResRel.setData_type_id(relIdsList.get(i).data_type_id);
                sysRoleResRel.setEnabled(1);
                sysRoleResRel.setCreate_time(new Date());
                sysRoleResRel.setUpdate_time(new Date());
                roleResRels.add(sysRoleResRel);
            }
            roleResRelService.saveRels(roleResRels);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResult.getError("更新数据失败！");
    }

}
