package com.cjsz.tech.web.ctrl.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class forgetController {

    //网站忘记密码地址
    @RequestMapping("/web/forget")
    public ModelAndView register(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            mv.setViewName("web/forget");
        }
        else {
            mv.setViewName("web/404");
        }
        return mv;
    }

}
