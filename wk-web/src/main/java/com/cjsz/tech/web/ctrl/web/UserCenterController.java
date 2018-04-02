package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.system.service.ProOrgExtendService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class UserCenterController {

    @Autowired
    ProOrgExtendService proOrgExtendService;

    @RequestMapping("/web/center")
    public ModelAndView search(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            mv.setViewName(tempName + "/center");
        }
        else {
            mv.setViewName("web/404");
        }
        return mv;
    }

    /**
     * 前台账号账号注销
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/site/loginUp", method = {RequestMethod.POST, RequestMethod.GET})
    public void webLoginUp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(true);
        session.invalidate();
        String path = request.getContextPath();
        response.sendRedirect(path + "/web");
    }

}
