package com.cjsz.tech.web.ctrl.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;

@Controller
public class WebLoginController {

    //网站登录地址
    @RequestMapping("/web/login")
    public ModelAndView login(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            mv.setViewName("web/login");
        }
        else {
            mv.setViewName("web/404");
        }
        return mv;
    }

}
