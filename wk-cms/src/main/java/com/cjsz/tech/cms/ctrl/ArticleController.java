package com.cjsz.tech.cms.ctrl;

import com.cjsz.tech.cms.beans.*;
import com.cjsz.tech.cms.domain.Article;
import com.cjsz.tech.cms.domain.ArticleCat;
import com.cjsz.tech.cms.domain.ArticleCatOrgRel;
import com.cjsz.tech.cms.service.ArticleCatService;
import com.cjsz.tech.cms.service.ArticleService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.BaseService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.system.utils.RequestContextUtil;
import com.cjsz.tech.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
@Controller
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private BaseService baseService;

    @Autowired
    UserService userService;

    @Autowired
    private ArticleCatService articleCatSerice;




    /**
     * 新闻页面 分页查询
     *
     * @return
     */
    @RequestMapping(value = "/admin/article/json/list", method = RequestMethod.POST)
    @ResponseBody
    public Object article_list(HttpServletRequest request, @RequestBody FindArticleBean bean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            bean.setOrg_id(user.getOrg_id());
            if(bean.getArticle_cat_id() == null){
                bean.setArticle_cat_id(0L);
            }
            Sort sort = new Sort(Sort.Direction.DESC, "order_weight");
            Object data = articleService.pageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            result.setData(data);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增/修改 新闻
     *
     * @param request
     * @param article
     * @return
     */
    @RequestMapping(value = "/admin/article/json/updateArticle", method = RequestMethod.POST)
    @ResponseBody
    public Object updateArticle(HttpServletRequest request, @RequestBody Article article) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            JsonResult result = JsonResult.getSuccess("");
            //查询分类是否自建
            ArticleCat articleCat = articleCatSerice.selectSourceByCatId(article.getArticle_cat_id(),user.getOrg_id());
            if (articleCat == null) {
               return JsonResult.getOther("不是自建分类不能进行操作！！！");
            } else {
                if (article.getArticle_id() == null) {
                    //新闻表数据添加
                    //图片处理，压缩图片
                    String cover_url_small = baseService.getImgScale(article.getCover_url(), "small", 0.5);
                    article.setCover_url_small(cover_url_small);

                    article = articleService.saveArticle(article, user.getUser_id(), user.getOrg_id());
                    result.setMessage(Constants.ACTION_ADD);
                } else {
                    if (!user.getOrg_id().equals(article.getOrg_id())) {
                        return JsonResult.getError("没有修改权限！");
                    }
                    //新闻表数据修改
                    Article art = articleService.selectById(article.getArticle_id());
                    if (art == null) {
                        return JsonResult.getError(Constants.NEW_NOT_EXIST);
                    }
                    if (!article.getCover_url().equals(art.getCover_url())) {
                        //图片处理，压缩图片
                        String cover_url_small = baseService.getImgScale(article.getCover_url(), "small", 0.5);
                        article.setCover_url_small(cover_url_small);
                    }
                    article = articleService.updateArticle(article, user.getUser_id());
                    result.setMessage(Constants.ACTION_UPDATE);
                }

                result.setData(article);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新闻详情 发布、取消发布，推荐，头条
     *
     * @param request
     * @param article
     * @return
     */
    @RequestMapping(value = "/admin/article/json/updateStatus", method = RequestMethod.POST)
    @ResponseBody
    public Object updateStatus(HttpServletRequest request, @RequestBody Article article) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (!user.getOrg_id().equals(article.getOrg_id())) {
                return JsonResult.getError("没有修改权限！");
            }
            Article art = articleService.selectById(article.getArticle_id());
            if (art == null) {
                return JsonResult.getError(Constants.NEW_NOT_EXIST);
            }
            Object object = articleService.updateStatus(article, user.getUser_id());
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 删除新闻
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/article/json/deleteArticles", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteArticles(HttpServletRequest request, @RequestBody DelArticleBean bean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            JsonResult result = JsonResult.getSuccess("");
            //查询分类是否自建
            for (Long article_id:bean.getArticle_ids()) {
                Long cat_id = articleService.getCatId(article_id);
                ArticleCat articleCat = articleCatSerice.selectSourceByCatId(cat_id, user.getOrg_id());
                if (articleCat == null) {
                    return JsonResult.getOther("不是自建分类不能进行操作！！！");
                } else {
                    Long[] article_ids = bean.getArticle_ids();
                    String article_ids_str = StringUtils.join(article_ids, ",");
                    if (user.getOrg_id() != 1) {
                        List<Article> articles = articleService.selectByOrgAndArticleIds(1L, article_ids_str);
                        if (articles.size() > 0) {
                            return JsonResult.getException("总部资讯不可删除！");
                        }
                    }
                    articleService.deleteByArticleIds(article_ids_str);

                    result.setMessage(Constants.ACTION_DELETE);
                    result.setData(new ArrayList());
                    return result;
                }
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }


}
