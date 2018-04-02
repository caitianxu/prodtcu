package com.cjsz.tech.web.ctrl.web;

import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.journal.domain.PeriodicalChild;
import com.cjsz.tech.journal.service.PeriodicalRepoService;
import com.cjsz.tech.member.domain.UnifyMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class PerinfoController {

    @Autowired
    PeriodicalRepoService periodicalRepoService;

    @RequestMapping("/web/perinfo/{p_id}")
    public ModelAndView search(HttpServletRequest request, @PathVariable String p_id) {
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null) {
            String token_type = request.getParameter("token_type");

            if (token_type != null && token_type.equals("max") ) {
                mv.addObject("pid", p_id);
                List<PeriodicalChild> result = periodicalRepoService.getImg(Long.valueOf(p_id));
                mv.addObject("imgList", JSONObject.toJSONString(result));
                mv.setViewName("web/perinfo");
            } else {
                UnifyMember member = (UnifyMember) request.getSession().getAttribute("member_info");
                if (member != null) {
                    mv.addObject("pid", p_id);
                    List<PeriodicalChild> result = periodicalRepoService.getImg(Long.valueOf(p_id));
                    mv.addObject("imgList", JSONObject.toJSONString(result));
                    mv.setViewName("web/perinfo");
                } else {
                    mv.setViewName("web/login");
                }
            }

        } else {
            mv.setViewName("web/404");
        }
        return mv;
    }

    @RequestMapping("/web/perinfo")
    public ModelAndView searcherr(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("web/404");
        return mv;
    }
}
