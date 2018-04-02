package com.cjsz.tech.web.ctrl.mobile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


@Controller
public class MobileShelfController {

    /**
     * 书架
     * @return
     */
    @RequestMapping("/mobile/shelf")
    public ModelAndView mobileBookshelf(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            if(request.getSession().getAttribute("member_token") != null) {
                mv.setViewName("mobile/shelf");
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
