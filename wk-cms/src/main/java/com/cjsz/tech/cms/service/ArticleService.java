package com.cjsz.tech.cms.service;

import com.cjsz.tech.cms.beans.FindArticleBean;
import com.cjsz.tech.cms.domain.Article;
import com.cjsz.tech.cms.domain.ArticleCatOrgRel;
import com.cjsz.tech.system.service.IOfflineService;
import org.springframework.data.domain.Sort;

import java.util.List;


/**
 * Created by Administrator on 2016/11/22 0022.
 */
public interface ArticleService extends IOfflineService {

    /**
     * 分页查询本机构的新闻
     *
     * @param sort
     * @param bean
     * @return
     */
    public Object pageQuery(Sort sort, FindArticleBean bean);

    /**
     * 获取前台新闻
     * @param sort
     * @param bean
     * @return
     */
    public Object sitePageQuery(Sort sort, FindArticleBean bean);

    /**
     * 添加新闻
     *
     * @param article
     */
    public Article saveArticle(Article article, Long user_id, Long org_id);

    /**
     * 修改新闻
     *
     * @param article
     */
    public Article updateArticle(Article article, Long user_id);

    /**
     * 通过新闻ID查找新闻
     *
     * @param article_id
     * @return
     */
    public Article selectById(Long article_id);

    /**
     * 通过组织ID查找新闻
     *
     * @param org_id
     * @return
     */
    public List<Article> selectByOrgId(Long org_id);

    /**
     * 通过资讯Ids删除资讯
     * @param cat_ids
     */
    public void deleteByCatIds(String cat_ids);

    /**
     * 通过资讯Ids查找资讯
     * @param cat_ids
     * @return
     */
    public List<Article> selectByCatIds(String cat_ids);

    /**
     * 通过资讯Ids删除资讯
     * @param article_ids_str
     */
    public void deleteByArticleIds(String article_ids_str);

    /**
     * 查询未解析成功的资讯
     * @return
     */
    public List<Article> selectUnParse();

    /**
     * 更新资讯解析状态
     * @param org_id
     * @param parse_status
     */
    public void updateParseStatusByOrgId(Long org_id, Integer parse_status);

    /**
     * 更新资讯解析状态
     * @param article_id
     * @param parse_status
     */
    public void updateParseStatusByArticleId(Long article_id, Integer parse_status);

    /**
     *  新闻 发布、取消发布，推荐，头条
     * @param article
     * @param user_id
     * @return
     */
    public Object updateStatus(Article article, Long user_id);

    /**
     * 资讯Ids查找机构的资讯列表
     * @param org_id
     * @param article_ids_str
     * @return
     */
    List<Article> selectByOrgAndArticleIds(Long org_id, String article_ids_str);

    /**
     * 查询资讯数量
     * @param org_id
     * @return
     */
    Integer getCountByOrgId(Long org_id);

    /**
     * 根据资讯ID查找分类ID
     * @param article_id
     * @return
     */
    Long getCatId(Long article_id);
}
