package com.cjsz.tech.web.ctrl.mobile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


@Controller
public class MobileMemberSetingController {

    /**
     * 设置
     * @return
     */
    @RequestMapping("/mobile/mySetting")
    public ModelAndView mobileBookshelf(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            if(request.getSession().getAttribute("member_token") != null) {
                mv.setViewName("mobile/mySetting");
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

    /**
     * 设置-昵称
     * @return
     */
    @RequestMapping("/mobile/mySetting_name")
    public ModelAndView mobileSetingName(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            if(request.getSession().getAttribute("member_token") != null) {
                mv.setViewName("mobile/mySetting_name");
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
    /**
     * 设置-性别
     * @return
     */
    @RequestMapping("/mobile/mySetting_sex")
    public ModelAndView mobileSetingSex(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            if(request.getSession().getAttribute("member_token") != null) {
                mv.setViewName("mobile/mySetting_sex");
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
    /**
     * 设置-密码
     * @return
     */
    @RequestMapping("/mobile/mySetting_pwd")
    public ModelAndView mobileSetingPwd(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            if(request.getSession().getAttribute("member_token") != null) {
                mv.setViewName("mobile/mySetting_pwd");
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
