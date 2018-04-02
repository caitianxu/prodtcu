package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.dev.domain.AppTypeOrgRel;
import com.cjsz.tech.dev.domain.AppVersion;
import com.cjsz.tech.dev.domain.AppVersionAndroid;
import com.cjsz.tech.dev.domain.AppVersionIos;
import com.cjsz.tech.dev.service.AppTypeService;
import com.cjsz.tech.dev.service.AppVersionAndroidService;
import com.cjsz.tech.dev.service.AppVersionIosService;
import com.cjsz.tech.dev.service.AppVersionService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.service.OrganizationService;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Controller
public class OtherController {

    @Autowired
    AppVersionService appVersionService;

    @Autowired
    AppTypeService appTypeService;

    @Autowired
    AppVersionIosService appVersionIosService;

    @Autowired
    AppVersionAndroidService appVersionAndroidService;

    @Autowired
    ProOrgExtendService proOrgExtendService;

    @Autowired
    OrganizationService organizationService;

    @RequestMapping("/web/err")
    public ModelAndView search(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("web/err");
        return mv;
    }

    @RequestMapping("/web/404")
    public ModelAndView unpage(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("web/404");
        return mv;
    }



    //每人计统计地址
    @RequestMapping("/web/enter")
    public ModelAndView mobileEnter() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("web/enter");
        return mv;
    }


    //APP下载地址
    @RequestMapping("/pkgApp")
    public ModelAndView pkgApp(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        String org_id = null;
        Long app_type_id = 2L;
        if(request.getAttribute("org_id") != null) {
            org_id = request.getAttribute("org_id").toString();
            //判断机构使用的APP版本
            AppTypeOrgRel rel = appTypeService.selectById(Long.parseLong(org_id));
            if (rel != null){
                app_type_id = rel.getApp_type_id();
            }
        }
        AppVersionIos ios = appVersionIosService.selectEnabled(app_type_id.intValue());
        if (ios != null) {
            mv.addObject("iosURL", ios.getPublish_url());
        }else {
            mv.addObject("iosURL","");
        }
        AppVersionAndroid android = appVersionAndroidService.selectEnabled(app_type_id.intValue());
        if (android != null){
            mv.addObject("androidURL" , android.getPublish_url());
        }else {
            mv.addObject("androidURL","");
        }
        mv.addObject("org_id", org_id);
        mv.addObject("app_type",app_type_id );
        mv.setViewName("pkgApp/main");
        return mv;
    }

    //大屏下载地址
    @RequestMapping("/maxApp")
    public ModelAndView maxApp() {
        ModelAndView mv = new ModelAndView();
        //版本地址
        AppVersion appVersion = appVersionService.selectEnabled();
        mv.addObject("appVersion", appVersion);
        mv.setViewName("maxApp/main");
        return mv;
    }

    //技术支持
    @RequestMapping("/web/tosoft")
    public ModelAndView tosoft(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null) {
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            mv.setViewName(tempName + "/tosoft");
        } else {
            mv.setViewName("web/tosoft");
        }
        return mv;
    }

    //产品与平台
    @RequestMapping("/web/productservice")
    public ModelAndView productservice(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null) {
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            mv.setViewName(tempName + "/productservice");
        } else {
            mv.setViewName("web/productservice");
        }
        return mv;
    }

    //联系人
    //产品与平台
    @RequestMapping("/web/linkme")
    public ModelAndView linkme(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null) {
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            mv.setViewName(tempName + "/linkme");
        } else {
            mv.setViewName("web/linkme");
        }
        return mv;
    }

    //产品介绍
    @RequestMapping("/web/introduction")
    public ModelAndView introduction(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null) {
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            mv.setViewName(tempName + "/introduction");
        } else {
            mv.setViewName("web/introduction");
        }
        return mv;
    }


    /**
     * 注册获取机构下子机构列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/site/memberRegister/getOrgDept",method = RequestMethod.POST)
    @ResponseBody
    public Object getOrgDept(HttpServletRequest request){
        try{
            String org_id = request.getParameter("org_id");
            if (StringUtils.isEmpty(org_id)){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            List<Organization> deptList = organizationService.getOrgDept(Long.parseLong(org_id));
            List<Organization> deptTree = new ArrayList<>();
            if (deptList != null && deptList.size() > 0) {
                deptTree = organizationService.selectOrgTree(deptList);
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(deptTree);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
