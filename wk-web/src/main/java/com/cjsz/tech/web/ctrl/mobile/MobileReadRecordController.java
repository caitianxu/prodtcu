package com.cjsz.tech.web.ctrl.mobile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


@Controller
public class MobileReadRecordController {

    /**
     * 阅读记录
     * @return
     */
    @RequestMapping("/mobile/readRecord")
    public ModelAndView mobileReadRecord(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            if(request.getSession().getAttribute("member_token") != null) {
                mv.setViewName("mobile/readRecord");
            }
            else{
                mv.setViewName("mobile/login");
            }
        }
        else{
            mv.setViewName("mobile/404");
        }
        return mv;
    }

}
