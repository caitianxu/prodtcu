package com.cjsz.tech.cms.ctrl;

import com.cjsz.tech.cms.beans.ArticleCatBean;
import com.cjsz.tech.cms.beans.DelArticleCatBean;
import com.cjsz.tech.cms.beans.FindCatOrgBean;
import com.cjsz.tech.cms.beans.UpdateCatOrgBean;
import com.cjsz.tech.cms.domain.Article;
import com.cjsz.tech.cms.domain.ArticleCat;
import com.cjsz.tech.cms.service.ArticleCatService;
import com.cjsz.tech.cms.service.ArticleService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.StringUtil;
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
import java.util.List;


/**
 * Created by Administrator on 2016/10/25.
 */
@Controller
public class ArticleCatController {

    @Autowired
    ArticleCatService articleCatService;

    @Autowired
    ArticleService articleService;

    @Autowired
    UserService userService;


    /**
     * 资讯分类树(资讯分类列表用)
     *
     * @return
     */
    @RequestMapping(value = "/admin/articleCat/getTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getTree(HttpServletRequest request) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //获取当前用户机构全部资讯分类，转化为树形结构
            List<ArticleCatBean> articleCats = articleCatService.getCats(sysUser.getOrg_id());
            List<ArticleCatBean> cats = articleCatService.selectTree(articleCats);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(cats);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 资讯分类树(本机构自建，资讯分类详情用)
     *
     * @return
     */
    @RequestMapping(value = "/admin/articleCat/getOrgTree", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object getOrgTree(HttpServletRequest request) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //获取当前用户机构全部资讯分类，转化为树形结构
            List<ArticleCatBean> articleCats = articleCatService.getOrgCats(sysUser.getOrg_id());
            List<ArticleCatBean> cats = articleCatService.selectTree(articleCats);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.LOAD_SUCCESS);
            jsonResult.setData(cats);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新增,修改资讯分类
     *
     * @return
     */
    @RequestMapping(value = "/admin/articleCat/save_article_cat", method = RequestMethod.POST)
    @ResponseBody
    public Object save_article_cat(HttpServletRequest request, @RequestBody ArticleCatBean bean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            JsonResult jsonResult = JsonResult.getSuccess("");
            ArticleCatBean articleCatBean = null;
            if (bean.getArticle_cat_id() == null) {
                //新增
                //分类名称重复(分类名查找)
                List<ArticleCat> articleCats = articleCatService.selectByCatName(sysUser.getOrg_id(), bean.getArticle_cat_name());
                if (articleCats.size() > 0) {
                    return JsonResult.getError(Constants.CATNAME_REPETITION);
                }
                articleCatBean = articleCatService.saveCat(bean, sysUser.getOrg_id());
                jsonResult.setData(articleCatBean);
                jsonResult.setMessage(Constants.ACTION_ADD);
                return jsonResult;
            } else {
                //修改
                //分类名称重复(分类名查找不包括本身)
                List<ArticleCat> articleCats = articleCatService.selectOtherByCatName(sysUser.getOrg_id(), bean.getArticle_cat_name(), bean.getArticle_cat_id());
                if (articleCats.size() > 0) {
                    return JsonResult.getError(Constants.CATNAME_REPETITION);
                }
                //是否自建(1:自建;2:分配)
                if (bean.getSource_type() == 2) {
                    //分配只更新关系表数据
                    ArticleCat cat = articleCatService.selectByCatId(bean.getArticle_cat_id());

                    //不可修改总部数据的层次、名称、备注信息
                    if ((!cat.getPid().equals(bean.getPid())) || (!cat.getArticle_cat_name().equals(bean.getArticle_cat_name()))) {
                        return JsonResult.getError("分配数据只能修改排序！");
                    }
                    String catRemark = cat.getRemark();
                    if (cat.getRemark() == null) {
                        catRemark = "";
                    }
                    if (!bean.getRemark().equals(catRemark)) {
                        return JsonResult.getError("分配数据只能修改排序！");
                    }
                }
                articleCatBean = articleCatService.updateCat(bean, sysUser.getOrg_id());
                jsonResult.setData(articleCatBean);
                jsonResult.setMessage(Constants.ACTION_UPDATE);
                return jsonResult;
            }
        } catch (Exception e) {
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新闻分类 启用
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/articleCat/json/updateEnabled", method = RequestMethod.POST)
    @ResponseBody
    public Object updateEnabled(HttpServletRequest request, @RequestBody ArticleCatBean bean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            if (sysUser.getOrg_id() == 1) {
                //是否启用(1:启用,2:不启用)
                articleCatService.updateEnabled(bean);
            } else {
                return JsonResult.getError("您没有权限！");
            }
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(bean);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 新闻分类 显示
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/articleCat/json/updateShow", method = RequestMethod.POST)
    @ResponseBody
    public Object updateShow(HttpServletRequest request, @RequestBody ArticleCatBean bean) {
        try {
            if (bean.getEnabled() == 2) {
                return JsonResult.getError("资讯分类未启用！");
            }
            articleCatService.updateShow(bean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(bean);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 资讯分类列表 删除数据
     *
     * @param request
     * @param delCatBean
     */
    @RequestMapping(value = "/admin/articleCat/delete_article_cat", method = {RequestMethod.POST})
    @ResponseBody
    public Object delete_article_cat(HttpServletRequest request, @RequestBody DelArticleCatBean delCatBean) {
        try {
            SysUser sysUser = userService.findByToken(request.getHeader("token"));
            //需要删除的资讯分类（1,2,3）
            Long[] article_cat_ids = delCatBean.getArticle_cat_ids();

            //只能删除本机构的分类：is_delete
            for (int i = 0; i < article_cat_ids.length; i++) {
                List<Long> catids = articleCatService.selectOrgCatIds(sysUser.getOrg_id());
                if (!catids.contains(article_cat_ids[i])) {
                    return JsonResult.getException("只能删除本机构的分类！");
                }
            }
            String cat_ids = StringUtils.join(article_cat_ids, ",");
            //通过cat_ids找到full_path合集
            List<String> full_paths = articleCatService.getFullPathsByCatIds(cat_ids);
            //需要删除的所有资讯分类catId合集
            List<Long> catIdList = new ArrayList<Long>();
            for (String full_path : full_paths) {
                //通过full_path获取自身和所有子集的catId合集
                catIdList.addAll(articleCatService.getCatIdsByFullPath(full_path));
            }
            //自身和所有子集的catIds
            String catIds = StringUtils.join(catIdList, ",");
            //通过cat_ids查询资讯详情
            List<Article> articles = articleService.selectByCatIds(catIds);
            if (StringUtil.isEmpty(delCatBean.getMark()) && articles.size() > 0) {
                return JsonResult.getError("分类下存在关联的资讯，是否删除？");
            }
            articleCatService.deleteAllByCatIds(catIds);
            JsonResult jsonResult = JsonResult.getSuccess(Constants.ACTION_DELETE);
            jsonResult.setData(new ArrayList());
            return jsonResult;

        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 资讯分类添加机构
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/articleCat/json/addOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object addOrg(HttpServletRequest request, @RequestBody UpdateCatOrgBean bean) {
        try {
            Long org_id = bean.getOrg_id();
            if (org_id == 1) {
                return JsonResult.getError("不能为长江科技分配资讯分类！");
            }
            articleCatService.addOrg(bean.getArticle_cat_id(), org_id);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 资讯分类移除机构
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/articleCat/json/removeOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object removeOrg(HttpServletRequest request, @RequestBody UpdateCatOrgBean bean) {
        try {
            if (bean.getOrg_id() == 1) {
                return JsonResult.getError("不能移除长江科技的资讯分类！");
            }
            articleCatService.removeOrg(bean.getArticle_cat_id(), bean.getOrg_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(new ArrayList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 资讯分类机构列表
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/articleCat/json/orgList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object orgList(HttpServletRequest request, @RequestBody FindCatOrgBean bean) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "is_delete").and(new Sort(Sort.Direction.ASC, "org_id"));
            Object obj = articleCatService.getOrgsPageQuery(sort, bean);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(obj);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

    /**
     * 修改机构资讯分类排序
     *
     * @param request
     * @param bean
     * @return
     */
    @RequestMapping(value = "/admin/articleCat/json/orderByOrg", method = RequestMethod.POST)
    @ResponseBody
    public Object orderByOrg(HttpServletRequest request, @RequestBody ArticleCatBean bean) {
        try {
            SysUser user = userService.findByToken(request.getHeader("token"));
            if (user == null) {
                return JsonResult.getExpire(Constants.TOKEN_FAILED);
            }
            if (bean.getArticle_cat_id() == null) {
                return JsonResult.getError("请选择分类");
            }
            articleCatService.orderByOrg(bean.getArticle_cat_id(), bean.getOrder_weight(), user.getOrg_id());
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
            result.setData(bean);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
