package com.cjsz.tech.dev.ctrl;

import com.cjsz.tech.dev.beans.AppTypeOrgRelBean;
import com.cjsz.tech.dev.beans.DelBean;
import com.cjsz.tech.dev.domain.AppType;
import com.cjsz.tech.dev.domain.AppTypeOrgRel;
import com.cjsz.tech.dev.service.AppTypeService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@Controller
public class AppTypeController {

    @Autowired
    private AppTypeService appTypeService;
    @Autowired
    private UserService userService;


    /**
     * 获取app类型列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/admin/appType/getList" , method = RequestMethod.POST)
    @ResponseBody
    public Object getList(HttpServletRequest request){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            List<AppType> result = appTypeService.getAllList();
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增、修改app类型
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/appType/addUpdate" , method = RequestMethod.POST)
    @ResponseBody
    public Object addUpdate(HttpServletRequest request, @RequestBody AppType bean){
        try{
            if (StringUtils.isEmpty(bean.getApp_name())){
                return JsonResult.getError("请输入app名称");
            }
            if (StringUtils.isEmpty(bean.getPackage_name())){
                return JsonResult.getError("请输入包名");
            }
            if (StringUtils.isEmpty(bean.getApp_type_name())){
                return JsonResult.getError("请输入app类型名称");
            }
            if (bean.getApp_type_id() == null){
                bean.setCreate_time(new Date());
                bean.setUpdate_time(new Date());
                bean.setIs_delete(2);
                appTypeService.add(bean);
            }else{
                bean.setUpdate_time(new Date());
                appTypeService.update(bean);
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 批量删除app类型
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/appType/delete" , method = RequestMethod.POST)
    @ResponseBody
    public Object delete(HttpServletRequest request , @RequestBody DelBean bean){
        try{
            if (bean.getIds() == null || bean.getIds().length <1){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            String ids = StringUtils.join(bean.getIds(),",");
            appTypeService.deleteByIds(ids);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_DELETE);
            jsonResult.setData(bean.getIds());
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 添加机构app类型关系
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/appType/addOrg" , method = RequestMethod.POST)
    @ResponseBody
    public Object delete(HttpServletRequest request , @RequestBody AppTypeOrgRel bean){
        try{
            if (bean.getOrg_id() == null){
                return JsonResult.getError("请选择机构");
            }
            if (bean.getApp_type_id() == null){
                return JsonResult.getError("请选择app类型");
            }
            appTypeService.addOrg(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 获取该app类型机构列表
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/appType/orgList" , method = RequestMethod.POST)
    @ResponseBody
    public Object orgList(HttpServletRequest request , @RequestBody AppTypeOrgRelBean bean){
        try{
            if (bean.getApp_type_id() == null){
                return JsonResult.getError("请选择app类型");
            }
            Object result = appTypeService.getOrgList(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 获取app类型列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/admin/appType/getAllList" , method = RequestMethod.POST)
    @ResponseBody
    public Object getAllList(HttpServletRequest request){
        try{
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null){
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            List<AppType> result = appTypeService.getAllList();
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(result);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
