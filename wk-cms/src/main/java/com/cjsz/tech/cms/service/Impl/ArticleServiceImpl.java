package com.cjsz.tech.cms.service.Impl;

import com.cjsz.tech.cms.beans.FindArticleBean;
import com.cjsz.tech.cms.domain.Article;
import com.cjsz.tech.cms.mapper.ArticleCatMapper;
import com.cjsz.tech.cms.mapper.ArticleMapper;
import com.cjsz.tech.cms.service.ArticleService;
import com.cjsz.tech.cms.util.TempUnZip;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleCatMapper articleCatMapper;

    @Override
    public Object pageQuery(Sort sort, FindArticleBean bean) {
        List<String> catPaths = articleCatMapper.getFullPathsByCatIds(bean.getArticle_cat_id().toString());
        String catPath = "";
        if(catPaths.size()>0){
            catPath = catPaths.get(0);
        }
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        List<Article> result = articleMapper.getArticleList(bean.getOrg_id(), catPath, bean.getSearchText());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public Object sitePageQuery(Sort sort, FindArticleBean bean) {
        List<String> catPaths = articleCatMapper.getFullPathsByCatIds(bean.getArticle_cat_id().toString());
        String catPath = "";
        if(catPaths.size()>0){
            catPath = catPaths.get(0);
        }
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        List<Article> result = articleMapper.sitePageQuery(bean.getOrg_id(), catPath);
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "新闻")
    @Transactional
    public Article saveArticle(Article article, Long user_id, Long org_id) {
        article.setOrg_id(org_id);
        article.setUser_id(user_id);
        article.setArticle_status(0);//(0:未启用;1:启用;2:停用)
        //类型(0:默认;1:图文;2:外链)
        if(article.getArticle_type() == 1){
            article.setArticle_content(article.getArticle_content().toString());
        }else{
            article.setArticle_content(article.getArticle_content());
        }
        article.setCmt_count(0L);
        article.setClick_count(0L);
        article.setCreate_time(new Date());
        article.setUpdate_time(new Date());
        article.setParse_status(2);//1:解析;2:未解析;3:解析中
        article.setIs_delete(2);
        articleMapper.insert(article);
        //解析html
        TempUnZip.runParseArticle(article);
        return article;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "新闻")
    @Transactional
    public Article updateArticle(Article article, Long user_id) {
        Article org_article = articleMapper.selectById(article.getArticle_id());
        article.setUser_id(user_id);
        //类型(0:默认;1:图文;2:外链)
        if(article.getArticle_type() == 1){
            article.setArticle_content(article.getArticle_content().toString());
        } else{
            article.setArticle_content(article.getArticle_content());
        }
        //内容修改则更改解析状态为未解析
        if(!org_article.getArticle_content().toString().equals(article.getArticle_content().toString())){
            article.setParse_status(2);//1:解析;2:未解析;3:解析中
            //解析html
            TempUnZip.runParseArticle(article);
        }
        article.setUpdate_time(new Date());
        article.setIs_delete(2);
        BeanUtils.copyProperties(article, org_article);
        articleMapper.updateByPrimaryKey(article);
        return article;
    }

    @Override
    public Article selectById(Long article_id) {
        return articleMapper.selectById(article_id);
    }

    @Override
    public List<Article> selectByOrgId(Long org_id) {
        return articleMapper.getArticleList(org_id, null, null);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "新闻")
    public void deleteByCatIds(String cat_ids) {
        articleMapper.deleteByCatIds(cat_ids);
    }

    @Override
    public List<Article> selectByCatIds(String cat_ids) {
        return articleMapper.selectByCatIds(cat_ids);
    }

    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "新闻")
    public void deleteByArticleIds(String article_ids_str){
        articleMapper.deleteByArticleIds(article_ids_str);
    }

    @Override
    //查询未解析成功的资讯
    public List<Article> selectUnParse() {
        return articleMapper.selectUnParse();
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "新闻")
    public void updateParseStatusByOrgId(Long org_id, Integer parse_status) {
        articleMapper.updateParseStatusByOrgId(org_id, parse_status);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "新闻")
    public void updateParseStatusByArticleId(Long article_id, Integer parse_status) {
        articleMapper.updateParseStatusByArticleId(article_id, parse_status);
    }

    @Override
    public Integer hasOffLine(Long orgid,String time,Object...otherparam) {
        Integer checknum = articleMapper.checkOffLineNum(orgid,time);
        if(checknum==null ) {
            checknum =0;
        }
        return checknum;
    }

    @Override
    public List<Article> getOffLineNumList(Long orgid,String timev,Object...otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        return articleMapper.getOffLineNumList(orgid,timev, num, size);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "新闻")
    public Object updateStatus(Article article, Long user_id) {
        Article art = articleMapper.selectById(article.getArticle_id());
        //发布状态改变
        if(art.getArticle_status() != article.getArticle_status()){
            if(article.getArticle_status() == 2){
                //修改为不发布
                art.setPublish_time(null);
                art.setHeadline(2);
                art.setRecommend(2);
            }else{
                //修改为发布
                art.setPublish_time(new Date());
            }
            art.setArticle_status(article.getArticle_status());
        }else{
            //如果当前发布状态为未发布，提示需要先发布
            if(article.getArticle_status() == 2){
                return JsonResult.getError(Constants.NEW_NOT_PUBLISH);
            }
            //推荐
            if(art.getRecommend() != article.getRecommend()){
                art.setRecommend(article.getRecommend());
            }
            //头条
            else if(art.getHeadline() != article.getHeadline()){
                //仅一条头条：更新该条新闻头条状态，将其他设为非头条
                if(article.getHeadline() == 1){
                    //不是推荐，则修改所有该分类下的为非
                    articleMapper.setNotHeadLineByCatId(article.getArticle_cat_id());
                }
                art.setHeadline(article.getHeadline());
            }
        }
        art.setUser_id(user_id);
        art.setUpdate_time(new Date());
        articleMapper.updateByPrimaryKey(art);
        JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
        result.setData(art);
        return result;
    }

    @Override
    public List<Article> selectByOrgAndArticleIds(Long org_id, String article_ids_str) {
        return articleMapper.selectByOrgAndArticleIds(org_id, article_ids_str);
    }

    @Override
    public Integer getCountByOrgId(Long org_id) {
        return articleMapper.getCountByOrgId(org_id);
    }

    @Override
    public Long getCatId(Long article_id){
        return articleMapper.getCatId(article_id);
    }

}
