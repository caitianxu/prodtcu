package com.cjsz.tech.web.ctrl.admin;

import com.cjsz.tech.system.domain.HelpCenter;
import com.cjsz.tech.system.service.HelpCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/8/9 0009.
 */
@Controller
public class HelpController {

    @Autowired
    private HelpCenterService helpCenterService;

    //帮助中心跳转
    @RequestMapping("/admin/help/{id}")
    public ModelAndView helpCenter(HttpServletRequest request, @PathVariable String id) {
        ModelAndView mv = new ModelAndView();
        HelpCenter help =  helpCenterService.selectById(Long.parseLong(id));
        HelpCenter lastHelp = helpCenterService.selectLastById(Long.parseLong(id));
        HelpCenter nextHelp = helpCenterService.selectNextById(Long.parseLong(id));

        if (help != null ) {
            mv.addObject("help", help);
        }else{
            mv.setViewName("web/404");
        }
        mv.addObject("lastHelp",lastHelp);
        mv.addObject("nextHelp",nextHelp);




        mv.setViewName("admin/help");
        return mv;
    }
}
