package com.cjsz.tech.dev.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.dev.beans.DelVersionBean;
import com.cjsz.tech.dev.domain.AppVersionAndroid;
import com.cjsz.tech.dev.service.AppVersionAndroidService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
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
import java.util.Date;
import java.util.List;

/**
 * Created by Li Yi on 2016/12/20.
 */
@Controller
@RequestMapping("/admin/appVersionAndroid")
public class AppVersionAndroidController {

    @Autowired
    private AppVersionAndroidService appVersionAndroidService;

    @Autowired
    UserService userService;

    /**
     * 分页查询版本信息列表
     *
     * @param request
     * @param pageConditionBean
     * @return
     */
    @RequestMapping(value = "/pageList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object appVersionPageList(HttpServletRequest request, @RequestBody PageConditionBean pageConditionBean){
        try{
            Sort sort = new Sort(Sort.Direction.DESC, "create_time").and(new Sort(Sort.Direction.DESC, "version_id"));
            Object data = appVersionAndroidService.pageQuery(sort, pageConditionBean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 不分页查询所有版本列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/allList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object appVersionList(HttpServletRequest request){
        SysUser sysUser = userService.findByToken(request.getHeader("token"));// 当前登录用户
        if(sysUser == null){
            return JsonResult.getExpire(Constants.OVER_TIME);
        }
        try{
            List<AppVersionAndroid> appVersions = appVersionAndroidService.getAllVersions();
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(appVersions);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增/修改版本信息
     *
     * @param request
     * @param appVersion
     * @return
     */
    @RequestMapping(value = "/update_appVersion", method = {RequestMethod.POST})
    @ResponseBody
    public Object update_appNav(HttpServletRequest request, @RequestBody AppVersionAndroid appVersion){
        try{
            JsonResult result = JsonResult.getSuccess("");
            String path = request.getContextPath();
            String basePath = request.getScheme()+"://"+request.getServerName();
            if(request.getServerPort() != 80) {
                basePath = basePath + ":" + request.getServerPort() ;
            }
            basePath += path;
            if (appVersion.getApp_type_id() == null){
                return JsonResult.getError("请选择app类型");
            }
            if (appVersion.getVersion_id() != null) {
                AppVersionAndroid version = appVersionAndroidService.selectByVersionIds(appVersion.getVersion_id().toString()).get(0);
                if(!version.getPackage_url().equals(appVersion.getPackage_url())){
                    appVersion.setPublish_url(basePath  + appVersion.getPackage_url());
                }
                appVersion.setUpdate_time(new Date());
                appVersion.setIs_delete(2);
                appVersionAndroidService.updateAppVersion(appVersion);
                result.setMessage(Constants.ACTION_UPDATE);
            } else {
                //是否使用（1：是；2：否）
                appVersion.setPublish_url(basePath  + appVersion.getPackage_url());
                appVersion.setEnabled(2);
                appVersion.setCreate_time(new Date());
                appVersion.setUpdate_time(new Date());
                appVersion.setIs_delete(2);
                appVersionAndroidService.saveAppVersion(appVersion);
                result.setMessage(Constants.ACTION_ADD);
            }
            result.setData(appVersion);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 更换当前使用版本
     *
     * @param request
     * @param appVersion
     * @return
     */
    @RequestMapping(value = "/updateEnabled", method = {RequestMethod.POST})
    @ResponseBody
    public Object update_enabled(HttpServletRequest request, @RequestBody AppVersionAndroid appVersion){
        try{
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            //是否使用（1：是；2：否）
            if(appVersion.getEnabled() == 1){
                //如果当前的已经在使用，不能更改状态
                return JsonResult.getError("至少有一个正在使用的版本");
            }else{
                appVersion.setEnabled(1);
            }
            appVersion.setUpdate_time(new Date());
            appVersionAndroidService.updateEnabled(appVersion);
            result.setData(appVersion);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除版本信息
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/delete_appVersions", method = {RequestMethod.POST})
    @ResponseBody
    public Object delete_appVersions(HttpServletRequest request, @RequestBody DelVersionBean bean){
        try{
            String appVersion_ids_str = StringUtils.join(bean.getIds(), ",");
            List<AppVersionAndroid> appVersions = appVersionAndroidService.selectByVersionIds(appVersion_ids_str);
            for(AppVersionAndroid appVersion : appVersions){
                if(appVersion.getEnabled() == 1){
                    return JsonResult.getError("版本正在使用，不能删除");
                }
            }
            appVersionAndroidService.deleteByVersionIds(appVersion_ids_str);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
