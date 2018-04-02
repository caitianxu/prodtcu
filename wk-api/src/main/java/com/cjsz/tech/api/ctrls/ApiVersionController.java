package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.api.APIConstants;
import com.cjsz.tech.dev.domain.AppVersion;
import com.cjsz.tech.dev.domain.AppVersionAndroid;
import com.cjsz.tech.dev.domain.AppVersionIos;
import com.cjsz.tech.dev.service.AppVersionAndroidService;
import com.cjsz.tech.dev.service.AppVersionIosService;
import com.cjsz.tech.dev.service.AppVersionService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取版本地址
 * Created by shiaihua on 17/1/5.
 */
@Controller
public class ApiVersionController {

    @Autowired
    private AppVersionService versionService;

    @Autowired
    private AppVersionIosService appVersionIosService;

    @Autowired
    private AppVersionAndroidService appVersionAndroidService;

    @RequestMapping(value = "/api/appVersion/getAppVersion", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getAppVersion(HttpServletRequest request) {
        try {
            AppVersion appVersion = versionService.selectEnabled();
            if (appVersion == null) {
                return JsonResult.getError("版本未发布！");
            }
            String path = request.getContextPath();
            String basePath = request.getScheme() + "://" + request.getServerName();
            if (request.getServerPort() != 80) {
                basePath = basePath + ":" + request.getServerPort();
            }
            basePath += path;
            appVersion.setPackage_url(basePath + appVersion.getPackage_url());
            JsonResult jsonResult = JsonResult.getSuccess(APIConstants.APP_VERSION_REVICED);
            jsonResult.setData(appVersion);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    @RequestMapping(value = "/api/appVersionIos/getAppVersion", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getAppVersionIos(HttpServletRequest request) {
        try {
            AppVersionIos appVersion = appVersionIosService.selectEnabled(1);
            if (appVersion == null) {
                return JsonResult.getError("版本未发布！");
            }
            String path = request.getContextPath();
            String basePath = request.getScheme() + "://" + request.getServerName();
            if (request.getServerPort() != 80) {
                basePath = basePath + ":" + request.getServerPort();
            }
            basePath += path;
            appVersion.setPackage_url(basePath + appVersion.getPackage_url());
            JsonResult jsonResult = JsonResult.getSuccess(APIConstants.APP_VERSION_REVICED);
            jsonResult.setData(appVersion);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    @RequestMapping(value = "/api/appVersionAndroid/getAppVersion", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getAppVersionAndroid(HttpServletRequest request) {
        try {
            AppVersionAndroid appVersion = appVersionAndroidService.selectEnabled(1);
            if (appVersion == null) {
                return JsonResult.getError("版本未发布！");
            }
            String path = request.getContextPath();
            String basePath = request.getScheme() + "://" + request.getServerName();
            if (request.getServerPort() != 80) {
                basePath = basePath + ":" + request.getServerPort();
            }
            basePath += path;
            appVersion.setPackage_url(basePath + appVersion.getPackage_url());
            JsonResult jsonResult = JsonResult.getSuccess(APIConstants.APP_VERSION_REVICED);
            jsonResult.setData(appVersion);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
