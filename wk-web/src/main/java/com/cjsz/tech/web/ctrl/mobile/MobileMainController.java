package com.cjsz.tech.web.ctrl.mobile;

import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MobileMainController {

    @Autowired
    OrganizationService organizationService;

    /**
     * 书架
     * @return
     */
    @RequestMapping(path = {"/mobile", "/mobile/main"})
    public ModelAndView mobileBookshelf(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            Organization org = organizationService.selectById(
                    Long.valueOf(request.getAttribute("org_id").toString()));
            mv.addObject("org_name", org.getOrg_name());
            mv.setViewName("mobile/main");
        }
        else{
            mv.setViewName("mobile/404");
        }
        return mv;
    }

    /**
     * 404
     * @return
     */
    @RequestMapping("/mobile/404")
    public ModelAndView mobile404() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("mobile/404");
        return mv;
    }

    /**
     * err
     * @return
     */
    @RequestMapping("/mobile/err")
    public ModelAndView mobileErr() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("mobile/err");
        return mv;
    }

}
