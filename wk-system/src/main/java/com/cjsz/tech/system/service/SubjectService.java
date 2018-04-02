package com.cjsz.tech.system.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.system.beans.ArticleBean;
import com.cjsz.tech.system.beans.SubjectOrgBean;
import com.cjsz.tech.system.beans.SubjectOrgListBean;
import com.cjsz.tech.system.domain.SubjectArticleRel;
import com.cjsz.tech.system.domain.SubjectBooksRel;
import com.cjsz.tech.system.domain.SubjectManage;
import org.springframework.data.domain.Sort;

/**
 * Created by Administrator on 2017/9/25 0025.
 */
public interface SubjectService {

    //获取专题分页列表
    Object getList(SubjectOrgBean bean);

    //新增专题
    void save(SubjectManage bean);

    //更新专题
    void update(SubjectManage bean);

    //专题添加新闻
    void addArticle(SubjectArticleRel bean);

    //查询专题是否添加新闻资讯
    SubjectArticleRel getArticle(Long subject_id,Long article_id);

    //查询专题新闻
    Object selectArticleById(SubjectOrgBean bean);

    //获取专题分配机构列表
    Object getOrgList(SubjectOrgListBean bean, Sort sort);

    //专题分配机构
    void addOrg(Long subject_id, Long org_id);

    //专题移除机构
    void removeOrg(Long subject_id, Long org_id);

    //专题添加书籍
    void addBook(SubjectBooksRel bean);

    //获取专题书籍列表
    Object getBookList(SubjectOrgBean bean);

    //专题移除图书
    void removeBook(Long subject_id, Long book_id);

    //更改专题书籍排序
    void orderBooks(SubjectBooksRel bean);

    //机构修改专题排序
    void orderSubject(Long subject_id, Long order_weight, Long org_id);

    //根据Id查找专题
    SubjectManage findById(Long subject_id);

    //删除专题
    void deleteById(Long subject_id);

    //专题启用、停用
    void enabled(SubjectManage bean);

    //机构专题是否显示
    void updateIsShow(SubjectManage bean, Long org_id);

    //删除专题新闻
    void deleteArticle(SubjectArticleRel bean);

    //网站获取专题列表
    Object siteList(SubjectOrgBean bean);

    //验证图书是否存在
    SubjectBooksRel findBook(Long book_id, Long subject_id);

    //专题有新闻则更换新闻
    void updateArticle(SubjectArticleRel bean);

    //机构修改专题新闻排序
    void orderArticle(SubjectArticleRel bean);
}
