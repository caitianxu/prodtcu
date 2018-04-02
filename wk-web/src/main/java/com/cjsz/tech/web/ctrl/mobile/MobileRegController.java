package com.cjsz.tech.web.ctrl.mobile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


@Controller
public class MobileRegController {

    /**
     * 注册
     * @return
     */
    @RequestMapping("/mobile/reg")
    public ModelAndView mobileBookshelf(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("mobile/register");
        return mv;
    }

}
