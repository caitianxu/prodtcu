package com.cjsz.tech.web.ctrl.web;

import com.cjsz.tech.cms.beans.ArticleCatBean;
import com.cjsz.tech.cms.beans.FindArticleBean;
import com.cjsz.tech.cms.domain.Article;
import com.cjsz.tech.cms.domain.ArticleCat;
import com.cjsz.tech.cms.service.ArticleCatService;
import com.cjsz.tech.cms.service.ArticleService;
import com.cjsz.tech.journal.domain.PeriodicalCat;
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
public class InformationController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleCatService articleCatService;

    @Autowired
    private AdvCatService advCatService;

    @Autowired
    private AdvService advService;

    @Autowired
    ProOrgExtendService proOrgExtendService;

    @RequestMapping("/web/inf")
    public ModelAndView search(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null) {
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            FindArticleBean bean = new FindArticleBean();
            bean.setPageNum(1);
            bean.setPageSize(15);
            bean.setOrg_id(Long.valueOf(org_id));
            bean.setArticle_cat_id(0L);
            Object articleList = articleService.sitePageQuery(sort, bean);
            mv.addObject("articleList", articleList);
            //获取机构所有分类转为树形结构
            List<ArticleCatBean> cats = articleCatService.getAllCats(Long.parseLong(org_id));
            List<ArticleCatBean> articleCatList = articleCatService.selectTree(cats);
            request.setAttribute("cat_id", "");
            mv.addObject("articleCatList", articleCatList);
            //分类下广告
            AdvCat advCat = advCatService.selectByCatCode("00006");
            List<Adv> advModuleList = advService.selectAdvsByOrgIdAndCatIdNum(advCat.getAdv_cat_id(), Long.parseLong(org_id));
            mv.addObject("advModuleList", advModuleList);
            mv.setViewName(tempName + "/information");
        } else {
            mv.setViewName("web/404");
        }
        return mv;
    }

    @RequestMapping("/web/inf/{act_id}/{article_id}")
    public ModelAndView searchInfo(HttpServletRequest request, @PathVariable String act_id, @PathVariable String article_id) {
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

            Article articleInfo = articleService.selectById(Long.valueOf(article_id));
            if (articleInfo != null) {
                mv.addObject("newsinfo", articleInfo);
                mv.setViewName(tempName + "/infbyid");
            } else {
                mv.setViewName("web/err");
            }
        } else {
            mv.setViewName("web/404");
        }
        return mv;
    }

    @RequestMapping("/web/inf/{cat_id}")
    public ModelAndView search(HttpServletRequest request, @PathVariable String cat_id) {
        ModelAndView mv = new ModelAndView();
        if (request.getAttribute("org_id") != null) {
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            String org_id = request.getAttribute("org_id").toString();
            String server_name = StringUtils.substringBefore(request.getServerName(), ".");
            String tempName = "/web";
            if (StringUtils.isNotEmpty(server_name)) {
                tempName = proOrgExtendService.getTemple(server_name, org_id);
                if (StringUtils.isEmpty(tempName)) {
                    tempName = "/web";
                }
            }
            FindArticleBean bean = new FindArticleBean();
            bean.setPageNum(1);
            bean.setPageSize(15);
            bean.setOrg_id(Long.valueOf(org_id));
            bean.setArticle_cat_id(Long.valueOf(cat_id));
            Object articleList = articleService.sitePageQuery(sort, bean);
            mv.addObject("articleList", articleList);
            //获取机构所有分类转为树形结构
            List<ArticleCatBean> cats = articleCatService.getAllCats(Long.parseLong(org_id));
            List<ArticleCatBean> articleCatList = articleCatService.selectTree(cats);
            request.setAttribute("cat_id", cat_id);

            //分类下广告
            AdvCat advCat = advCatService.selectByCatCode("00006");
            List<Adv> advModuleList = advService.selectAdvsByOrgIdAndCatIdNum(advCat.getAdv_cat_id(), Long.parseLong(org_id));
            mv.addObject("advModuleList", advModuleList);


            mv.addObject("articleCatList", articleCatList);
            mv.setViewName(tempName + "/information");
        } else {
            mv.setViewName("web/404");
        }
        return mv;
    }


    @RequestMapping(value = "/site/inf/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getInfList(HttpServletRequest request, FindArticleBean bean) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight").and(new Sort(Sort.Direction.DESC, "create_time"));
            String cat_id = request.getParameter("cid");
            if (cat_id != null && StringUtils.isNotEmpty(cat_id)) {
                bean.setArticle_cat_id(Long.valueOf(cat_id));
            } else {
                bean.setArticle_cat_id(0L);
            }
            Object obj = articleService.sitePageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(obj);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    @RequestMapping(value = "/site/infCat/getList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object getInfCatList(HttpServletRequest request) {
        try {
            String org_id = request.getAttribute("org_id").toString();
            List<ArticleCat> articleCats = articleCatService.selectSiteCatsByOrgId(Long.valueOf(org_id));
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(articleCats);
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
    @RequestMapping(value = "/site/articleCats/getCats" , method = RequestMethod.POST)
    @ResponseBody
    public Object getCats(HttpServletRequest request){
        try {
            String cat_id = request.getParameter("cat_id");
            String org_id = request.getParameter("org_id");
            if (StringUtils.isEmpty(org_id)){
                return JsonResult.getError(Constants.EXCEPTION);
            }
            Map<String,Object> map = new HashMap<>();
            ArticleCat cat = articleCatService.selectByCatId(Long.parseLong(cat_id));
            if (cat != null){
                ArticleCat c_cat = articleCatService.selectByCatId(cat.getPid());
                if (c_cat != null) {
                    map.put("cat_name", c_cat.getArticle_cat_name());
                    map.put("cat_name_id", c_cat.getArticle_cat_id());
                    map.put("c_cat_name", cat.getArticle_cat_name());
                } else {
                    map.put("cat_name", cat.getArticle_cat_name());
                    map.put("cat_name_id", cat.getArticle_cat_id());
                    map.put("c_cat_name", null);
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
