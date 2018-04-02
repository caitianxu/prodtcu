package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.DelProjectOrgsBean;
import com.cjsz.tech.system.beans.OrganizationBean;
import com.cjsz.tech.system.beans.WebOrgExtendBean;
import com.cjsz.tech.system.domain.ProOrgExtend;
import com.cjsz.tech.system.service.BaseService;
import com.cjsz.tech.system.service.ProOrgExtendService;
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
import java.util.Map;

/**
 * 项目设置
 * Created by LuoLi on 2017/3/23 0023.
 */
@Controller
public class ProOrgExtendController {

    @Autowired
    private ProOrgExtendService proOrgExtendService;

    @Autowired
    private BaseService baseService;

    /**
     * 项目的机构————分页
     *
     * @return
     */
    @RequestMapping(value = "/admin/proOrgExtend/json/proOrgExtendList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object proOrgExtendList(HttpServletRequest request, @RequestBody PageConditionBean bean) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "create_time");
            Object data = proOrgExtendService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 项目的机构————新增、修改
     * 二级域名不能重复  server_name
     *
     * @return
     */
    @RequestMapping(value = "/admin/proOrgExtend/json/updateProOrgExtend", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object updateProOrgExtend(HttpServletRequest request, @RequestBody WebOrgExtendBean bean) {
        try {
            JsonResult result = JsonResult.getSuccess("");
            if (StringUtils.isEmpty(bean.getServer_name())) {
                return JsonResult.getError("二级域名不能为空");
            }

            ProOrgExtend proOrgExtend = new ProOrgExtend();
            proOrgExtend.setOrg_id(bean.getOrg_id());
            proOrgExtend.setOrg_logo(bean.getOrg_logo());
            proOrgExtend.setOrg_logo_small(bean.getOrg_logo_small());
            proOrgExtend.setOrg_name(bean.getOrg_name());
            proOrgExtend.setOrg_weixin(bean.getOrg_weixin());
            proOrgExtend.setOrg_weixin_small(bean.getOrg_weixin_small());
            proOrgExtend.setPro_code(bean.getPro_code());
            proOrgExtend.setPro_name(bean.getPro_name());
            proOrgExtend.setServer_name(bean.getServer_name());
            proOrgExtend.setPro_org_extend_id(bean.getPro_org_extend_id());
            proOrgExtend.setIs_registe(bean.getIs_registe());
            proOrgExtend.setEnabled(bean.getEnabled());

//            List<WebConfig> webConfigList = bean.getWebConfigList();
            if (proOrgExtend.getPro_org_extend_id() == null) {
                //新增
                ProOrgExtend orgExtend = proOrgExtendService.selectByServerName(proOrgExtend.getServer_name());
                if (orgExtend != null) {
                    return JsonResult.getError("二级域名不能重复");
                }
                proOrgExtendService.saveProOrg(proOrgExtend);
                ProOrgExtend extend = proOrgExtendService.selectByServerName(bean.getServer_name());
//                try {
//                    for (WebConfig webConfig : webConfigList) {
//                        webConfig.setPro_org_extend_id(extend.getPro_org_extend_id());
//                        webConfig.setOrg_id(bean.getOrg_id());
//                        webConfig.setCreate_time(new Date());
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                    return JsonResult.getException("导航信息错误");
//                }
//                proOrgExtendService.saveWebConf(webConfigList);
                result.setMessage(Constants.ACTION_ADD);
            } else {
                //修改
                ProOrgExtend extend = proOrgExtendService.selectById(proOrgExtend.getPro_org_extend_id());
                if (!extend.getServer_name().equals(proOrgExtend.getServer_name())) {
                    ProOrgExtend orgExtend = proOrgExtendService.selectByServerName(proOrgExtend.getServer_name());
                    if (orgExtend != null) {
                        return JsonResult.getError("二级域名不能重复");
                    }
                }
//                try {
//                    for (WebConfig webConfig : webConfigList) {
//                        webConfig.setPro_org_extend_id(bean.getPro_org_extend_id());
//                        webConfig.setOrg_id(bean.getOrg_id());
//                        webConfig.setCreate_time(new Date());
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                    return JsonResult.getException("导航信息错误");
//                }
//                proOrgExtendService.updateConf(webConfigList,bean.getPro_org_extend_id());
                proOrgExtendService.updateProOrg(proOrgExtend);
                result.setMessage(Constants.ACTION_UPDATE);
            }
            result.setData(bean);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 项目的机构————删除
     *
     * @return
     */
    @RequestMapping(value = "/admin/proOrgExtend/json/delProOrgExtends", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object delProOrgExtends(HttpServletRequest request, @RequestBody DelProjectOrgsBean bean) {
        try {
            proOrgExtendService.delProOrgs(StringUtils.join(bean.getIds(), ","));
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_DELETE);
            result.setData(new ArrayList<>());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 项目的机构————机构
     *
     * @return
     */
    @RequestMapping(value = "/admin/proOrgExtend/json/_pro_orgs", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getProOrgList(HttpServletRequest request, Map map) {
        try {
            String project_code = (String) map.get("project_code");
            List<OrganizationBean> list = proOrgExtendService.getProOrgList(project_code);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(list);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 网站启用停用
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/proOrgExtend/json/enabled",method = RequestMethod.POST)
    @ResponseBody
    public Object enabled(HttpServletRequest request ,@RequestBody ProOrgExtend bean){
        try{
            Long pro_org_extend_id = bean.getPro_org_extend_id();
            if (pro_org_extend_id == null){
                return JsonResult.getError("请选择网站配置!");
            }
            Integer enabled = bean.getEnabled();
            if (enabled == null){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            proOrgExtendService.updateEnabled(pro_org_extend_id,enabled);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_SUCCESS);
            jsonResult.setData(bean);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
