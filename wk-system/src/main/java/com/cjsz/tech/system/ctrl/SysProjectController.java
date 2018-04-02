package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysProject;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.system.service.SysProjectService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by pc on 2017/3/10.
 */
@Controller
public class SysProjectController {

    @Autowired
    private SysProjectService sysProjectService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProOrgExtendService proOrgExtendService;

    /**
     * 项目列表(有分页)
     *
     * @return
     */
    @RequestMapping(value = "/admin/project/listAll", method = RequestMethod.POST)
    @ResponseBody
    public Object listAll(HttpServletRequest request, @RequestBody PageConditionBean pageCondition) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "create_time");
            PageList result = (PageList) sysProjectService.pageQuery(sort, pageCondition);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 项目列表(无分页)
     *
     * @return
     */
    @RequestMapping(value = "/admin/project/list", method = RequestMethod.POST)
    @ResponseBody
    public Object list(HttpServletRequest request, @RequestBody PageConditionBean pageConditionBean) {
        try {
            Object object = sysProjectService.getAllProjectsBySearchText(pageConditionBean.getSearchText());
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(object);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 新增项目 project_id:null
     * 修改项目 project_id:非null
     *
     * @return
     */
    @RequestMapping("/admin/project/save")
    @ResponseBody
    public Object saveProject(HttpServletRequest request, @RequestBody SysProject sysProject) {
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if (sysUser == null) {
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try {
            if (sysProject.getProject_id() == null) {
                //同机构下的项目名不能重复
                SysProject pro = sysProjectService.getProjectByName(sysProject.getProject_name());
                if (pro != null) {
                    return JsonResult.getError(Constants.PROJECT_NAME_EXIST);
                }
                //同机构下的项目编号不能重复
                pro = sysProjectService.getProjectByCode(sysProject.getProject_code());
                if (pro != null) {
                    return JsonResult.getError(Constants.PROJECT_CODE_EXIST);
                }
                sysProjectService.saveProject(sysProject);
                Long projectId = sysProjectService.getMaxProjectId();
                SysProject data = sysProjectService.selectById(projectId);
                JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_ADD);
                jsonResult.setData(data);
                return jsonResult;
            } else {
                SysProject project_exist = sysProjectService.selectById(sysProject.getProject_id());
                //检查项目是否存在
                if (project_exist == null) {
                    return JsonResult.getError(Constants.PROJECT_NOT_EXIST);
                } else {
                    if (!sysProject.getProject_name().equals(project_exist.getProject_name())) {
                        //检查项目名称是否存在
                        SysProject pro = sysProjectService.getProjectByName(sysProject.getProject_name());
                        if (pro != null) {
                            return JsonResult.getError(Constants.PROJECT_NAME_EXIST);
                        }
                    }
                    if (!sysProject.getProject_code().equals(project_exist.getProject_code())) {
                        //检查项目编号是否存在
                        SysProject pro = sysProjectService.getProjectByCode(sysProject.getProject_code());
                        if (pro != null) {
                            return JsonResult.getError(Constants.PROJECT_CODE_EXIST);
                        }
                    }
                    project_exist.setProject_name(sysProject.getProject_name());
                    project_exist.setRemark(sysProject.getRemark());
                    sysProjectService.updateProject(project_exist);
                    proOrgExtendService.updateProName(sysProject.getProject_code(), sysProject.getProject_name());
                    SysProject data = sysProjectService.selectById(sysProject.getProject_id());
                    JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_UPDATE);
                    jsonResult.setData(data);
                    return jsonResult;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 项目机构树
     *
     * @return
     */
    @RequestMapping(value = "/admin/project/getProOrgTree", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getProOrgTree() {
        try {
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            Object obj = sysProjectService.getProOrgTree();
            result.setData(obj);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
