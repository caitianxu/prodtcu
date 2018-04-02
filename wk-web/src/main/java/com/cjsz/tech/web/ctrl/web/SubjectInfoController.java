package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.beans.SubjectOrgBean;
import com.cjsz.tech.system.domain.Adv;
import com.cjsz.tech.system.domain.AdvCat;
import com.cjsz.tech.system.domain.SubjectManage;
import com.cjsz.tech.system.service.AdvCatService;
import com.cjsz.tech.system.service.AdvService;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.system.service.SubjectService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Administrator on 2017/9/27 0027.
 */
@Controller
public class SubjectInfoController {

    @Autowired
    private SubjectService subjectService;
    @Autowired
    private AdvCatService advCatService;
    @Autowired
    private AdvService advService;
    @Autowired
    ProOrgExtendService proOrgExtendService;

    @RequestMapping("/web/subInfo/{subject_id}")
    public ModelAndView search(HttpServletRequest request,@PathVariable String subject_id){
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
            mv.addObject("subject_id",subject_id);
            SubjectOrgBean bean = new SubjectOrgBean();
            bean.setPageNum(1);
            bean.setPageSize(20);
            Long orgId;
            Long sub_id;
            try{
                orgId = Long.parseLong(org_id);
                sub_id = Long.parseLong(subject_id);
            }catch (Exception e){
                e.printStackTrace();
                mv.setViewName("web/404");
                return mv;
            }
            bean.setOrg_id(orgId);
            bean.setSubject_id(sub_id);
            SubjectManage sm = subjectService.findById(sub_id);
            mv.addObject("subject_remark", sm.getSubject_remark());
            mv.addObject("subject_name", sm.getSubject_name());
            //获取专题新闻
            Object articleList = subjectService.selectArticleById(bean);
            mv.addObject("articleList",articleList);
            mv.setViewName(tempName + "/subInfo");
            //获取专题图书列表
            Object bookList = subjectService.getBookList(bean);
            mv.addObject("bookList",bookList);
            //广告
            AdvCat advCat = advCatService.selectByCatCode("00006");
            List<Adv> advModuleList = advService.selectAdvsByOrgIdAndCatIdNum(advCat.getAdv_cat_id(), orgId);
            mv.addObject("advModuleList",advModuleList);
        }else{
            mv.setViewName("web/404");
        }
        return mv;
    }

    @RequestMapping("/web/subInfo")
    public ModelAndView searcherr(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("web/404");
        return mv;
    }


    @RequestMapping(value = "/site/subject/bookList" , method = RequestMethod.POST)
    @ResponseBody
    public Object bookList(HttpServletRequest request ,SubjectOrgBean bean){
        try {
            Object bookList = subjectService.getBookList(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(bookList);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    @RequestMapping(value = "/site/subject/articleList" , method = RequestMethod.POST)
    @ResponseBody
    public Object articleList(HttpServletRequest request ,SubjectOrgBean bean){
        try {
            Object articleList = subjectService.selectArticleById(bean);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(articleList);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
