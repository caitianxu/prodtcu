package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.Adv;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.utils.JsonResult;
import com.cjsz.tech.web.service.NjswIndexEhcacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    NjswIndexEhcacheService njswIndexEhcacheService;

    @Autowired
    ProOrgExtendService proOrgExtendService;


    @RequestMapping(path = {"/", "/web"})
    public ModelAndView webIndex(HttpServletRequest request) {
        Date date1 = new Date();
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null) {
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            //轮播广告
            List<Adv> advList = njswIndexEhcacheService.getAdvList(Long.valueOf(org_id));
            mv.addObject("advList", advList);
            //banner广告
            List<Adv> advBannerList = njswIndexEhcacheService.getAdvBannerList(Long.valueOf(org_id));
            mv.addObject("advBannerList", advBannerList);
            //书屋动态（活动、资讯）
            List<Map<String, Object>> cmsList = njswIndexEhcacheService.getCmsList(Long.valueOf(org_id));
            mv.addObject("cmsList", cmsList);
            //图书推荐
            Map<String, Object> recommendBookList = njswIndexEhcacheService.getRecommendBookList(Long.valueOf(org_id));
            mv.addObject("recommendBookList", recommendBookList);
            //热榜
            Map<String, Object> hotBookList = njswIndexEhcacheService.getHotBookList(Long.valueOf(org_id));
            mv.addObject("hotBookList", hotBookList);
            //期刊
            Map<String, Object> periodicalList = njswIndexEhcacheService.getPeriodicalList(Long.valueOf(org_id));
            mv.addObject("periodicalList", periodicalList);
            //报纸
            Map<String, Object> newspaperList = njswIndexEhcacheService.getNewspaperList(Long.valueOf(org_id));
            mv.addObject("newspaperList", newspaperList);
            //热词
            Object keys = njswIndexEhcacheService.getKeys(Long.valueOf(org_id));
            mv.addObject("keys", keys);
            //首页广告
            List<Adv> advDataList = njswIndexEhcacheService.advDataList(Long.parseLong(org_id));
            mv.addObject("advDataList", advDataList);
            //期刊下面的广告
            Adv underPeriodicalAdv = njswIndexEhcacheService.underPeriodicalAdv(Long.parseLong(org_id));
            mv.addObject("underPeriodicalAdv", underPeriodicalAdv);
            //报纸下面的广告
            Adv underPaperAdv = njswIndexEhcacheService.underPaperAdv(Long.parseLong(org_id));
            mv.addObject("underPaperAdv", underPaperAdv);
            //首页滚动新闻推荐
            List<Map<String, Object>> recommendCms = njswIndexEhcacheService.getRecommendCmsList(Long.valueOf(org_id));
            mv.addObject("recommendCms", recommendCms);

            mv.setViewName(tempName + "/index");
        } else {
            mv.setViewName("/web/404");
        }
        Date date2 = new Date();
        Long times = date2.getTime() - date1.getTime();
        System.out.println("进入首页耗时：" + times/1000);
        return mv;
    }


    @RequestMapping(path = {"/web/{rel_id}"})
    public ModelAndView webError(HttpServletRequest request, @PathVariable String rel_id) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/web/404");
        return mv;
    }

    @RequestMapping(path = {"/{rel_id}"})
    public ModelAndView indexError(HttpServletRequest request, @PathVariable String rel_id) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/web/404");
        return mv;
    }

    /**
     * 获取首页推荐资讯
     * @param request
     * @return
     */
    @RequestMapping("/site/getRecommendArticle")
    @ResponseBody
    public Object getRecommendArticle(HttpServletRequest request){
        try{
            String org_id = request.getAttribute("org_id").toString();
            if (StringUtils.isEmpty(org_id)){
               return JsonResult.getError(Constants.EXCEPTION);
            }
            List<Map<String, Object>> recommendCms = njswIndexEhcacheService.getRecommendCmsList(Long.valueOf(org_id));
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(recommendCms);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
