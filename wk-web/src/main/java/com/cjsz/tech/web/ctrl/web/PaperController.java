package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.journal.beans.FindNewspaperBean;
import com.cjsz.tech.journal.beans.NewspaperCatBean;
import com.cjsz.tech.journal.domain.NewspaperCat;
import com.cjsz.tech.journal.service.NewspaperCatService;
import com.cjsz.tech.journal.service.NewspaperService;
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
public class PaperController {

    @Autowired
    private NewspaperService newspaperService;
    @Autowired
    private ProOrgExtendService proOrgExtendService;
    @Autowired
    private NewspaperCatService newspaperCatService;
    @Autowired
    private AdvCatService advCatService;
    @Autowired
    private AdvService advService;

    @RequestMapping("/web/paper")
    public ModelAndView search(HttpServletRequest request){
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            FindNewspaperBean bean = new FindNewspaperBean();
            bean.setPageNum(1);
            bean.setPageSize(20);
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            String cat_id = request.getParameter("cat_id");
            bean.setOrg_id(Long.parseLong(org_id));
            if (StringUtils.isNotEmpty(cat_id)) {
                bean.setNewspaper_cat_id(Long.parseLong(cat_id));
            }else{
                bean.setNewspaper_cat_id(0L);
            }
            Object newspaperList = newspaperService.pageSiteQuery(sort, bean);
            mv.addObject("newspaperList", newspaperList);
            //获取机构全部分类转化为树形结构
            List<NewspaperCatBean> cats = newspaperCatService.getAllCats(Long.parseLong(org_id));
            List<NewspaperCatBean> paperCatList = newspaperCatService.selectTree(cats);
            mv.addObject("paperCatList",paperCatList);
            request.setAttribute("cat_id","");
            mv.setViewName(tempName + "/newspaper");
            //分类下广告
            AdvCat advCat = advCatService.selectByCatCode("00006");
            List<Adv> advModuleList = advService.selectAdvsByOrgIdAndCatIdNum(advCat.getAdv_cat_id(), Long.parseLong(org_id));
            mv.addObject("advModuleList",advModuleList);
        }
        else{
            mv.setViewName("web/404");
        }
        return mv;
    }


    @RequestMapping("/web/paper/{cat_id}")
    public ModelAndView search(HttpServletRequest request,@PathVariable String cat_id){
        ModelAndView mv = new ModelAndView();
        if(request.getAttribute("org_id") != null) {
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            FindNewspaperBean bean = new FindNewspaperBean();
            bean.setPageNum(1);
            bean.setPageSize(20);
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            bean.setOrg_id(Long.parseLong(org_id));
            if (StringUtils.isNotEmpty(cat_id)) {
                bean.setNewspaper_cat_id(Long.parseLong(cat_id));
            }
            Object newspaperList = newspaperService.pageSiteQuery(sort, bean);
            mv.addObject("newspaperList", newspaperList);
            //获取机构全部分类转化为树形结构
            List<NewspaperCatBean> cats = newspaperCatService.getAllCats(Long.parseLong(org_id));
            List<NewspaperCatBean> paperCatList = newspaperCatService.selectTree(cats);
            request.setAttribute("cat_id", cat_id);
            mv.addObject("paperCatList",paperCatList);
            mv.setViewName(tempName + "/newspaper");
            //分类下广告
            AdvCat advCat = advCatService.selectByCatCode("00006");
            List<Adv> advModuleList = advService.selectAdvsByOrgIdAndCatIdNum(advCat.getAdv_cat_id(), Long.parseLong(org_id));
            mv.addObject("advModuleList",advModuleList);
        }
        else{
            mv.setViewName("web/404");
        }
        return mv;
    }


    /**
     * 获取面包屑分类
     * @param request
     * @return
     */
    @RequestMapping(value = "/site/paper/getCats" , method = RequestMethod.POST)
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
                NewspaperCat cat = newspaperCatService.selectByCatId(Long.parseLong(cat_id));
                if (cat != null){
                    NewspaperCat c_cat = newspaperCatService.selectByCatId(cat.getPid());
                    if (c_cat != null) {
                        map.put("cat_name", c_cat.getNewspaper_cat_name());
                        map.put("cat_name_id", c_cat.getNewspaper_cat_id());
                        map.put("c_cat_name", cat.getNewspaper_cat_name());
                    } else {
                        map.put("cat_name", cat.getNewspaper_cat_name());
                        map.put("cat_name_id", cat.getNewspaper_cat_id());
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



    @RequestMapping(value = "/site/newsPaper/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getList(HttpServletRequest request, FindNewspaperBean bean){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            Object data = newspaperService.pageSiteQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
