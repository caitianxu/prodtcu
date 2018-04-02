package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.journal.beans.FindPeriodicalBean;
import com.cjsz.tech.journal.beans.PeriodicalCatBean;
import com.cjsz.tech.journal.domain.NewspaperCat;
import com.cjsz.tech.journal.domain.PeriodicalCat;
import com.cjsz.tech.journal.service.PeriodicalCatService;
import com.cjsz.tech.journal.service.PeriodicalRepoService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.Adv;
import com.cjsz.tech.system.domain.AdvCat;
import com.cjsz.tech.system.service.AdvCatService;
import com.cjsz.tech.system.service.AdvService;
import com.cjsz.tech.system.service.ProOrgExtendService;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PeriodicalController {


    @Autowired
    private PeriodicalRepoService periodicalRepoService;

    @Autowired
    private PeriodicalCatService periodicalCatService;

    @Autowired
    private AdvCatService advCatService;

    @Autowired
    private AdvService advService;

    @Autowired
    private ProOrgExtendService proOrgExtendService;

    @RequestMapping("/web/per/{cat_id}")
    public ModelAndView search(HttpServletRequest request,@PathVariable String cat_id){
        ModelAndView mv = new ModelAndView();
        String org_id = request.getAttribute("org_id").toString();
        String server_name = StringUtils.substringBefore(request.getServerName(), ".");
        String tempName = "/web";
        if (StringUtils.isNotEmpty(server_name)) {
            tempName = proOrgExtendService.getTemple(server_name, org_id);
            if (StringUtils.isEmpty(tempName)) {
                tempName = "/web";
            }
        }
        FindPeriodicalBean bean = new FindPeriodicalBean();
        bean.setPageNum(1);
        bean.setPageSize(25);
        bean.setOrg_id(Long.parseLong(org_id));
        bean.setPeriodical_cat_id(Long.valueOf(cat_id));
        Sort sort = new Sort(Sort.Direction.DESC, "order_weight");
        Object perList = periodicalRepoService.webGetList( bean,sort);
        mv.addObject("perList", perList);

        List<PeriodicalCatBean> cats = periodicalCatService.getAllCats(bean.getOrg_id());
        List<PeriodicalCatBean> perCatList = periodicalCatService.getCatTree(cats);
        mv.addObject("perCatList",perCatList);


        //分类下广告
        AdvCat advCat = advCatService.selectByCatCode("00006");
        List<Adv> advModuleList = advService.selectAdvsByOrgIdAndCatIdNum(advCat.getAdv_cat_id(), Long.parseLong(org_id));
        mv.addObject("advModuleList",advModuleList);

        request.setAttribute("cat_id",cat_id);
        mv.setViewName(tempName + "/periodical");
        return mv;
    }





    @RequestMapping("/web/per")
    public ModelAndView search(HttpServletRequest request){
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
            FindPeriodicalBean bean = new FindPeriodicalBean();
            bean.setPageNum(1);
            bean.setPageSize(25);
            bean.setOrg_id(Long.parseLong(org_id));
            bean.setPeriodical_cat_id(0L);
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight");
            Object perList = periodicalRepoService.webGetList(bean,sort);
            mv.addObject("perList", perList);

            List<PeriodicalCatBean> cats = periodicalCatService.getAllCats(bean.getOrg_id());
            List<PeriodicalCatBean> perCatList = periodicalCatService.getCatTree(cats);
            mv.addObject("perCatList", perCatList);

            request.setAttribute("cat_id", "");
            mv.setViewName(tempName + "/periodical");
            //分类下广告
            AdvCat advCat = advCatService.selectByCatCode("00006");
            List<Adv> advModuleList = advService.selectAdvsByOrgIdAndCatIdNum(advCat.getAdv_cat_id(), Long.parseLong(org_id));
            mv.addObject("advModuleList",advModuleList);
        }
        else {
            mv.setViewName("web/404");
        }
        return mv;
    }

    @RequestMapping(value = "/site/periodical/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getPerList(HttpServletRequest request, FindPeriodicalBean bean) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            String cat_id = request.getParameter("cid");
            if(cat_id != null && StringUtils.isNotEmpty(cat_id)){
                bean.setPeriodical_cat_id(Long.valueOf(cat_id));
            }
            else{
                bean.setPeriodical_cat_id(0L);
            }
            Object obj = periodicalRepoService.stiePageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(obj);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    @RequestMapping(value = "/site/periodicalCat/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getPerCatList(HttpServletRequest request) {
        try {
            if (request.getAttribute("org_id")==null){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            String org_id = request.getAttribute("org_id").toString();
            List<PeriodicalCat> perCatList = periodicalCatService.selectSiteCatsByOrgId(Long.parseLong(org_id));
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(perCatList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


    /**
     * 获取面包屑分类
     * @param request
     * @return
     */
    @RequestMapping(value = "/site/periodicalCats/getCats" , method = RequestMethod.POST)
    @ResponseBody
    public Object getCats(HttpServletRequest request){
        try {
            String cat_id = request.getParameter("cat_id");
            String org_id = request.getParameter("org_id");
            if (StringUtils.isEmpty(org_id)){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            Map<String,Object> map = new HashMap<>();
            if (StringUtils.isNotEmpty(cat_id)) {
                PeriodicalCat cat = periodicalCatService.selectByCatId(Long.parseLong(cat_id));
                if (cat != null) {
                    PeriodicalCat c_cat = periodicalCatService.selectByCatId(cat.getPeriodical_cat_pid());
                    if (c_cat != null) {
                        map.put("cat_name", c_cat.getPeriodical_cat_name());
                        map.put("cat_name_id", c_cat.getPeriodical_cat_id());
                        map.put("c_cat_name", cat.getPeriodical_cat_name());
                    } else {
                        map.put("cat_name", cat.getPeriodical_cat_name());
                        map.put("cat_name_id", cat.getPeriodical_cat_id());
                        map.put("c_cat_name", null);
                    }
                }
            }
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(map);
            return jsonResult;
        }catch (Exception e){
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }
}
