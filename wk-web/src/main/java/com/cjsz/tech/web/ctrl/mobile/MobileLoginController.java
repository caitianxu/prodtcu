package com.cjsz.tech.web.ctrl.mobile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


@Controller
public class MobileLoginController {

    /**
     * 登录
     * @return
     */
    @RequestMapping("/mobile/login")
    public ModelAndView mobileBookshelf(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            mv.setViewName("mobile/login");
        }
        else{
            mv.setViewName("mobile/404");
        }
        return mv;
    }

}
