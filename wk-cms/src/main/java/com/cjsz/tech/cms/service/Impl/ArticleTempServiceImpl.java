package com.cjsz.tech.cms.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.cms.domain.ArticleTemp;
import com.cjsz.tech.cms.mapper.*;
import com.cjsz.tech.cms.service.ArticleTempService;
import com.cjsz.tech.cms.util.TempUnZip;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
@Service
public class ArticleTempServiceImpl implements ArticleTempService {

    @Autowired
    private ArticleTempMapper articleTempMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Object pageQuery(Sort sort, PageConditionBean bean) {
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        List<ArticleTemp> result = articleTempMapper.pageQuery();
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    public List<ArticleTemp> getAll() {
        return articleTempMapper.getAll();
    }

    @Override
    @Transactional
    public ArticleTemp saveArticleTemp(ArticleTemp articleTemp) {
        articleTemp.setTemp_status(2);//1:解析;2:未解析;3:解析中
        articleTempMapper.insert(articleTemp);
        //修改资讯状态为未解析
        articleMapper.updateParseStatusByOrgId(articleTemp.getOrg_id(), 2);//1:解析;2:未解析;3:解析中
        //解压模板，根据该模板生成模板所属机构下的全部资讯html文件
        TempUnZip.runParseTemp(articleTemp);
        return articleTemp;
    }

    @Override
    @Transactional
    public ArticleTemp updateArticleTemp(ArticleTemp articleTemp) {
        ArticleTemp org_articleTemp = articleTempMapper.selectById(articleTemp.getArticle_temp_id());
        if(!articleTemp.getTemp_url().equals(org_articleTemp.getTemp_url())){
            //模板内容改变，变更状态为未解析
            articleTemp.setTemp_status(2);//1:解析;2:未解析;3:解析中
            //修改资讯状态为未解析
            articleMapper.updateParseStatusByOrgId(articleTemp.getOrg_id(), 2);
            //解压模板，根据该模板生成模板所属机构下的全部资讯html文件
            TempUnZip.runParseTemp(articleTemp);
        }
        BeanUtils.copyProperties(articleTemp, org_articleTemp);
        articleTempMapper.updateByPrimaryKey(articleTemp);
        return articleTemp;
    }

    @Override
    public ArticleTemp selectById(Integer article_temp_id) {
        return articleTempMapper.selectById(article_temp_id);
    }

    @Override
    public void deleteByTempIds(String temp_ids_str) {
        articleTempMapper.deleteByTempIds(temp_ids_str);
    }

    @Override
    public List<ArticleTemp> selectUnZip() {
        return articleTempMapper.selectUnZip();
    }

    @Override
    public List<ArticleTemp> selectZipByOrgId(Long org_id) {
        return articleTempMapper.selectZipByOrgId(org_id);
    }

    @Override
    public void updateArticleTempStatus(Integer article_temp_id, Integer temp_status) {
        articleTempMapper.updateArticleTempStatus(article_temp_id, temp_status);
    }

}
