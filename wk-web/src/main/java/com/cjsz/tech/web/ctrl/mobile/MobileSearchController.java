package com.cjsz.tech.web.ctrl.mobile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MobileSearchController {

    /**
     * 搜索
     * @return
     */
    @RequestMapping("/mobile/search")
    public ModelAndView mobileSearchKey(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if(request.getParameter("searchText") != null){
            mv.addObject("searchText",request.getParameter("searchText"));
        }
        else{
            mv.addObject("searchText","");
        }
        mv.setViewName("mobile/search");
        return mv;
    }

}
