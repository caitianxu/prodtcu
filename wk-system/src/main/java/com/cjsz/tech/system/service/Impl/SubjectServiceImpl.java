package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.beans.*;
import com.cjsz.tech.system.domain.SubjectArticleRel;
import com.cjsz.tech.system.domain.SubjectBooksRel;
import com.cjsz.tech.system.domain.SubjectManage;
import com.cjsz.tech.system.domain.SubjectOrgRel;
import com.cjsz.tech.system.mapper.SubjectArticleRelMapper;
import com.cjsz.tech.system.mapper.SubjectBooksRelMapper;
import com.cjsz.tech.system.mapper.SubjectManageMapper;
import com.cjsz.tech.system.mapper.SubjectOrgRelMapper;
import com.cjsz.tech.system.service.SubjectService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/9/25 0025.
 */
@Service
public class SubjectServiceImpl implements SubjectService {
    @Autowired
    private SubjectManageMapper subjectManageMapper;
    @Autowired
    private SubjectOrgRelMapper subjectOrgRelMapper;
    @Autowired
    private SubjectArticleRelMapper subjectArticleRelMapper;
    @Autowired
    private SubjectBooksRelMapper subjectBooksRelMapper;



    //获取专题分页列表
    @Override
    public Object getList(SubjectOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        List<SubjectManage> result = subjectManageMapper.getList(bean.getSearchText(),bean.getOrg_id());
        for (SubjectManage subjectManage:result){
            Integer count = subjectOrgRelMapper.selectOrgCount(subjectManage.getSubject_id());
            subjectManage.setOrg_count(count);
        }
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    public void save(SubjectManage bean) {
        bean.setCreate_time(new Date());
        bean.setUpdate_time(new Date());
        bean.setEnabled(1);
        bean.setIs_delete(2);
        subjectManageMapper.insert(bean);
        //创建专题机构关系
        SubjectOrgRel rel = new SubjectOrgRel();
        rel.setCreate_time(new Date());
        rel.setIs_show(1);
        rel.setIs_delete(2);
        rel.setOrder_weight(System.currentTimeMillis());
        rel.setOrg_id(bean.getOrg_id());
        rel.setSubject_id(bean.getSubject_id());
        rel.setUpdate_time(new Date());
        subjectOrgRelMapper.insert(rel);
    }

    @Override
    public void update(SubjectManage bean) {
        bean.setUpdate_time(new Date());
        subjectManageMapper.updateByPrimaryKey(bean);
    }

    @Override
    public void addArticle(SubjectArticleRel bean) {
        bean.setCreate_time(new Date());
        bean.setOrder_weight(System.currentTimeMillis());
        subjectArticleRelMapper.insert(bean);
    }

    @Override
    public SubjectArticleRel getArticle(Long subject_id,Long article_id) {
        return subjectArticleRelMapper.selectById(subject_id,article_id);
    }

    @Override
    public Object selectArticleById(SubjectOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        List<SubjectArticleBean> result = subjectArticleRelMapper.getArticle(bean.getSubject_id());
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    public Object getOrgList(SubjectOrgListBean bean, Sort sort) {
        PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<SubjectOrgListBean> result = subjectOrgRelMapper.getOrgList(bean);
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    public void addOrg(Long subject_id, Long org_id) {
        SubjectOrgRel rel = subjectOrgRelMapper.selectOrgRel(org_id,subject_id);
        if (rel == null){
            rel = new SubjectOrgRel(org_id,subject_id,System.currentTimeMillis(),1,new Date(),new Date(),2);
            subjectOrgRelMapper.insert(rel);
        }else {
            if (rel.getIs_delete() == 1){
                subjectOrgRelMapper.updateIsDelete(rel.getRel_id(),2);
            }
        }
    }

    @Override
    public void removeOrg(Long subject_id, Long org_id) {
        SubjectOrgRel rel = subjectOrgRelMapper.selectOrgRel(org_id,subject_id);
        if ((rel != null && rel.getIs_delete() == 2)){
            subjectOrgRelMapper.updateIsDelete(rel.getRel_id(),1);
        }
    }

    @Override
    public void addBook(SubjectBooksRel bean) {
        bean.setCreate_time(new Date());
        bean.setOrder_weight(System.currentTimeMillis());
        subjectBooksRelMapper.insert(bean);
    }

    @Override
    public Object getBookList(SubjectOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        List<SubjectBookBean> result = subjectBooksRelMapper.getBookList(bean.getSubject_id());
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    public void removeBook(Long subject_id, Long book_id) {
        subjectBooksRelMapper.removeBook(subject_id,book_id);
    }

    @Override
    public void orderBooks(SubjectBooksRel bean) {
        subjectBooksRelMapper.updateBookOrder(bean.getRel_id(),bean.getOrder_weight());
    }

    @Override
    public void orderSubject(Long subject_id, Long order_weight, Long org_id) {
        subjectOrgRelMapper.orderSubjectByOrgId(subject_id,order_weight,org_id);
    }

    @Override
    public SubjectManage findById(Long subject_id) {
        return subjectManageMapper.findById(subject_id);
    }

    @Override
    public void deleteById(Long subject_id) {
        subjectManageMapper.deleteById(subject_id);
    }

    @Override
    public void enabled(SubjectManage bean) {
        bean.setUpdate_time(new Date());
        subjectManageMapper.updateByPrimaryKey(bean);
        //停用则关掉所有显示开关
        if (bean.getEnabled() == 2) {
            subjectOrgRelMapper.updateisShow(bean.getSubject_id());
        }
    }

    @Override
    public void updateIsShow(SubjectManage bean, Long org_id) {
        subjectOrgRelMapper.updateIsShow(bean.getSubject_id(),bean.getIs_show(),org_id);
    }

    @Override
    public void deleteArticle(SubjectArticleRel bean) {
        subjectArticleRelMapper.deleteArticle(bean.getArticle_id(),bean.getSubject_id());
    }

    @Override
    public Object siteList(SubjectOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        List<SubjectManage> result = subjectManageMapper.siteList(bean.getOrg_id());
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    public SubjectBooksRel findBook(Long book_id, Long subject_id) {
        return subjectBooksRelMapper.findBook(book_id,subject_id);
    }

    @Override
    public void updateArticle(SubjectArticleRel bean) {
        subjectArticleRelMapper.updateArticle(bean.getSubject_id(),bean.getArticle_id());
    }

    @Override
    public void orderArticle(SubjectArticleRel bean) {
        subjectArticleRelMapper.orderArticle(bean.getRel_id(),bean.getOrder_weight());
    }
}
