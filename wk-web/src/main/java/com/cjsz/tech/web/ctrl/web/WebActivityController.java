package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.cms.beans.FindActivityBean;
import com.cjsz.tech.cms.domain.Activity;
import com.cjsz.tech.cms.service.ActivityService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WebActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    ProOrgExtendService proOrgExtendService;

    @RequestMapping("/web/act")
    public ModelAndView search(HttpServletRequest request) {
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
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            FindActivityBean bean = new FindActivityBean();
            bean.setPageNum(1);
            bean.setPageSize(5);
            Object activityList = activityService.sitePageQuery(sort, bean);
            mv.addObject("activityList", activityList);
            mv.setViewName(tempName + "/activity");
        } else {
            mv.setViewName("web/404");
        }
        return mv;
    }

    @RequestMapping("/web/act/{activity_id}")
    public ModelAndView searchInfo(HttpServletRequest request, @PathVariable String activity_id) {
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
            Activity activityInfo = activityService.selectById(Long.valueOf(activity_id));
            if (activityInfo == null) {
                mv.setViewName("web/err");
            } else {
                mv.addObject("newsinfo", activityInfo);
                mv.setViewName(tempName + "/actbyid");
            }
        } else {
            mv.setViewName("web/404");
        }
        return mv;
    }

    @RequestMapping(value = "/site/act/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getActList(HttpServletRequest request, PageConditionBean bean) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            Object data = activityService.sitePageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


}
