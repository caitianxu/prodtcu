package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.beans.*;
import com.cjsz.tech.system.conditions.ProjectCondition;
import com.cjsz.tech.system.domain.OrgExtend;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.OrgExtendService;
import com.cjsz.tech.system.service.OrganizationService;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.system.service.UserService;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
@Controller
@RequestMapping("/admin/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrgExtendService orgExtendService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProOrgExtendService proOrgExtendService;


    /**
     * 组织机构树
     * 组织管理中通过org_id切换组织。
     * 用户管理下拉的组织树
     *
     * @return
     */
    @RequestMapping("/getTree")
    @ResponseBody
    public Object getTree(HttpServletRequest request, @RequestBody DelOrgBean delOrg) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        Long org_id;
        if (delOrg.getOrg_id() != null) {
            org_id = delOrg.getOrg_id();
        } else {
            org_id = sysUser.getOrg_id();
        }
        OrganizationBean org = organizationService.selecOrgById(org_id);
        if (org == null) {
            return JsonResult.getError(Constants.ORG_NOT_EXIST);
        }
        try {
            List<OrganizationBean> orgBeans = organizationService.getAll();
            if (orgBeans.size() > 0) {
                organizationService.getOrgTree(org, orgBeans);
            }
            List<OrganizationBean> list = new ArrayList<OrganizationBean>();
            list.add(org);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(list);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增, 修改组织
     *
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public Object saveOrganization(HttpServletRequest request, @RequestBody Organization org) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        if (org.getOrg_name() == null) {
            return JsonResult.getError(Constants.ORG_NAME_NULL);
        }
        try {
            if (org.getOrg_id() == null) {
                organizationService.saveOrganization(org);
                Long orgId = organizationService.getMaxOrgId();
                Organization data = organizationService.selectById(orgId);
                JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_ADD);
                jsonResult.setData(data);
                return jsonResult;
            } else {
                Organization organ = organizationService.selectById(org.getOrg_id());
                if (organ == null) {
                    return JsonResult.getError(Constants.ORG_NOT_EXIST);
                }
                //获取当前修改的组织本身和下级组织
                List<Organization> ownerOrgs = organizationService.getOwnerOrgs(org.getFull_path());
                for (Organization ownerOrg : ownerOrgs) {
                    if (ownerOrg.getOrg_id().equals(org.getPid())) {
                        return JsonResult.getError(Constants.CLASS_PID_ERROR);
                    }
                }
                organ.setPid(org.getPid());
                organ.setOrg_name(org.getOrg_name());
                organ.setOrg_code(org.getOrg_code());
                organ.setRemark(org.getRemark());
                organizationService.updateOrganization(organ);
                Organization data = organizationService.selectById(organ.getOrg_id());
                JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_UPDATE);
                jsonResult.setData(data);
                return jsonResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }

    }

    /**
     * 删除组织
     *
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object delOrganization(HttpServletRequest request, @RequestBody DelRoleBean delOrg) {
//    	String ids = Arrays.toString(delOrg.getIds());
//    	String org_ids = ids.substring(1, ids.length() - 1);
        String org_ids = StringUtils.join(delOrg.getIds(), ",");
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            String msg = organizationService.deleteOrgs(org_ids, delOrg.getMark());
            if (msg == "failed") {
                return JsonResult.getError(Constants.DELETE_USER_REL);
            } else {
                JsonResult result = JsonResult.getSuccess(msg);
                result.setData(new ArrayList());
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增、修改机构
     * 超级管理员新增机构时，自动给该机构创建一个管理员用户及管理员角色。
     *
     * @return
     */
    @RequestMapping("/init")
    @ResponseBody
    public Object initOrganization(HttpServletRequest request, @RequestBody OrgExtendBean orgextend) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        if (orgextend.getOrg_name() == null) {
            return JsonResult.getError(Constants.ORGEXTEND_NAME_NULL);
        }
        if (orgextend.getShort_name() == null) {
            return JsonResult.getError(Constants.SHORT_NAME_NULL);
        }
        try {
            if (orgextend.getOrg_id() == null) {
                //检测机构名称和简称是否唯一
                Organization org = organizationService.getOrgByName(orgextend.getOrg_name());
                if (org != null) {
                    return JsonResult.getError(Constants.ORGEXTEND_NAME_EXIST);
                }
                OrgExtend orge = orgExtendService.getByShortName(orgextend.getShort_name());
                if (orge != null) {
                    return JsonResult.getError(Constants.SHORT_NAME_EXIST);
                }
                //添加机构, 机构扩展属性
                organizationService.saveOrgExtend(orgextend);
                Long orgId = organizationService.getMaxOrgId();
                OrgExtendBean orgext = organizationService.getExtendByOrgId(orgId);
                //创建用户，角色，关联角色
                organizationService.initOrganizationRel(orgext);
                //创建机构——app配置关系表
                organizationService.insertAppOrgRel(orgId, orgextend.getOrg_name());
                JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_ADD);
                jsonResult.setData(orgext);
                return jsonResult;
            } else {
                Organization organ = organizationService.selectById(orgextend.getOrg_id());
                if (organ == null) {
                    return JsonResult.getError(Constants.ORG_NOT_EXIST);
                } else {
                    //修改机构名称
                    if(!organ.getOrg_name().equals(orgextend.getOrg_name())){
                        Organization organ_new = organizationService.getOrgByName(orgextend.getOrg_name());
                        if(organ_new != null){
                            return JsonResult.getError(Constants.ORGEXTEND_NAME_EXIST);
                        }else{
                            organizationService.updateOrgName(orgextend.getOrg_name(), organ.getOrg_id());
                        }
                    }
                    organizationService.updateOrgExtend(orgextend, organ);
                    proOrgExtendService.updateProjectByOrgId(orgextend.getProject_code(), orgextend.getProject_name(), orgextend.getOrg_id(),orgextend.getOrg_name());
                    OrgExtendBean orgext = organizationService.getExtendByOrgId(organ.getOrg_id());
                    JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_UPDATE);
                    jsonResult.setData(orgext);
                    return jsonResult;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 机构管理列表
     *
     * @return
     */
    @RequestMapping("/getOrgExtendList")
    @ResponseBody
    public Object getOrgExtendList(HttpServletRequest request, @RequestBody ProjectCondition projectCondition) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "create_time");
            PageList result = (PageList) organizationService.pageQuery(sort, projectCondition, "myself");
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 机构管理列表
     *
     * @return
     */
    @RequestMapping("/getRootOrgList")
    @ResponseBody
    public Object getRootOrgList(HttpServletRequest request) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            List<OrgExtendBean> list = organizationService.getOrgExtend(null, null, null, null);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(list);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除机构
     *
     * @return
     */
    @RequestMapping("/deleteExtend")
    @ResponseBody
    public Object deleteExtend(HttpServletRequest request, @RequestBody DelOrgBean delOrg) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            organizationService.deleteOrgExtendRel(delOrg.getOrg_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 资源管理中的组织，显示全部根组织,机构
     * 组织管理中选择机构的列表
     *
     * @return
     */
    @RequestMapping("/getList")
    @ResponseBody
    public Object getList(HttpServletRequest request, @RequestBody ProjectCondition projectCondition) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "create_time");
            PageList result = (PageList) organizationService.pageQuery(sort, projectCondition, null);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     *
     * @param request
     * @return
     */
    @RequestMapping("/isLock")
    @ResponseBody
    public Object isLock(HttpServletRequest request,@RequestBody Organization organization ){
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            if (organization.getOrg_id() == null){
                return JsonResult.getError("请选择结构");
            }
            if (organization.getPwd_lock() == null){
                organization.setPwd_lock(2);
            }
            organizationService.updatePwdLock(organization.getOrg_id(),organization.getPwd_lock());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(organization);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 注册获取机构下子机构列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/getOrgDept",method = RequestMethod.POST)
    @ResponseBody
    public Object getOrgDept(HttpServletRequest request , @RequestBody Organization bean){
        try{
            Long org_id = bean.getOrg_id();
            if (org_id == null){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            List<Organization> deptList = organizationService.getOrgDept(org_id);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(deptList);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
