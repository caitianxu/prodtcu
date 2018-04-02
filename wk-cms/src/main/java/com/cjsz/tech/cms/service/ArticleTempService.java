package com.cjsz.tech.cms.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.cms.domain.ArticleTemp;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
public interface ArticleTempService {

    /**
     * 分页查询 资讯模板
     * @param sort
     * @param bean
     * @return
     */
    public Object pageQuery(Sort sort, PageConditionBean bean);

    /**
     * 全部资讯模板
     * @return
     */
    public Object getAll();

    /**
     * 添加模板
     * @param articleTemp
     */
    public ArticleTemp saveArticleTemp(ArticleTemp articleTemp);

    /**
     * 修改模板
     * @param articleTemp
     */
    public ArticleTemp updateArticleTemp(ArticleTemp articleTemp);

    /**
     * 通过模板ID查找模板
     * @param article_temp_id
     * @return
     */
    public ArticleTemp selectById(Integer article_temp_id);

    /**
     * 通过模板ID 删除模板
     * @param temp_ids_str
     */
    public void deleteByTempIds(String temp_ids_str);

    /**
     * 查询未解析的模板
     */
    public List<ArticleTemp> selectUnZip();

    /**
     * 查询机构已解析的模板
     * @param org_id
     * @return
     */
    public List<ArticleTemp> selectZipByOrgId(Long org_id);

    /**
     * 更新模板解析状态
     * @param article_temp_id
     * @param temp_status
     */
    public void updateArticleTempStatus(Integer article_temp_id, Integer temp_status);
}
