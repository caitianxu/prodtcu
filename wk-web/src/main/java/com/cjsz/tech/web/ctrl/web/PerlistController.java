package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.journal.domain.PeriodicalRepo;
import com.cjsz.tech.journal.service.PeriodicalRepoService;
import com.cjsz.tech.system.service.ProOrgExtendService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class PerlistController {

    @Autowired
    PeriodicalRepoService periodicalRepoService;

    @Autowired
    ProOrgExtendService proOrgExtendService;

    @RequestMapping("/web/perlist/{p_id}")
    public ModelAndView search(HttpServletRequest request, @PathVariable String p_id) {
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null) {
            mv.addObject("pid", p_id);
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            Long ps_id = Long.parseLong(p_id);
            List<PeriodicalRepo> result = periodicalRepoService.getSuggestList(ps_id, Long.parseLong(org_id));
            if (result.size() > 0) {
                PeriodicalRepo nowRepo = null;
                for (PeriodicalRepo repo : result) {
                    if(repo.getPeriodical_id() == ps_id.longValue()){
                        nowRepo = repo;
                    }
                }
                mv.addObject("nowRepo", nowRepo);
                mv.addObject("PeriodicalList", result);
                mv.setViewName(tempName + "/perlist");
            } else {
                mv.setViewName("web/404");
            }
        } else {
            mv.setViewName("web/404");
        }
        return mv;
    }

    @RequestMapping("/web/perlist")
    public ModelAndView searcherr(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("web/404");
        return mv;
    }
}
