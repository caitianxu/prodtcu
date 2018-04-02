package com.cjsz.tech.web.ctrl.web;

import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.SubjectOrgBean;
import com.cjsz.tech.system.domain.Adv;
import com.cjsz.tech.system.domain.AdvCat;
import com.cjsz.tech.system.service.AdvCatService;
import com.cjsz.tech.system.service.AdvService;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.system.service.SubjectService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Administrator on 2017/9/26 0026.
 */
@Controller
public class SubjectManageController {

    @Autowired
    private SubjectService subjectService;
    @Autowired
    private AdvCatService advCatService;
    @Autowired
    private AdvService advService;
    @Autowired
    ProOrgExtendService proOrgExtendService;

    @RequestMapping("/web/subject")
    public ModelAndView search(HttpServletRequest request){
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null){
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            SubjectOrgBean bean = new SubjectOrgBean();
            bean.setOrg_id(Long.parseLong(org_id));
            bean.setPageNum(1);
            bean.setPageSize(12);
            Object subjectList = subjectService.siteList(bean);
            mv.addObject("subjectList",subjectList);
            mv.setViewName(tempName + "/subject");

            //专题广告
            AdvCat advCat = advCatService.selectByCatCode("00007");
            List<Adv> advModuleList = advService.selectAdvsByOrgIdAndCatIdNum(advCat.getAdv_cat_id(), Long.parseLong(org_id));
            mv.addObject("advModuleList",advModuleList);

            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(advModuleList);
            mv.addObject("jsonResult", JSONObject.toJSONString(jsonResult));

        }else{
            mv.setViewName("web/404");
        }
        return mv;
    }

    @RequestMapping(value = "/site/subject/getList" , method = RequestMethod.POST)
    @ResponseBody
    public Object getList(HttpServletRequest request ,SubjectOrgBean bean){
        try {
            Object obj = subjectService.siteList(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(obj);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
