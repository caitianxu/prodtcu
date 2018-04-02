package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.system.service.ProOrgExtendService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class BookreviewController {

    @Autowired
    ProOrgExtendService proOrgExtendService;

    @RequestMapping("/web/bookreview")
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
            mv.setViewName(tempName + "/bookreview");
        } else {
            mv.setViewName("web/404");
        }
        return mv;
    }
}
