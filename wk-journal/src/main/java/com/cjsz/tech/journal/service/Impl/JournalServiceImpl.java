package com.cjsz.tech.journal.service.Impl;

import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.journal.beans.FindJournalBean;
import com.cjsz.tech.journal.domain.Journal;
import com.cjsz.tech.journal.mapper.JournalCatMapper;
import com.cjsz.tech.journal.mapper.JournalMapper;
import com.cjsz.tech.journal.service.JournalService;
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
import java.util.Map;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
@Service
public class JournalServiceImpl implements JournalService {

    @Autowired
    private JournalMapper journalMapper;

    @Autowired
    private JournalCatMapper journalCatMapper;

    @Override
    public Object pageQuery(Sort sort, FindJournalBean bean) {
        List<String> catPaths = journalCatMapper.getFullPathsByCatIds(bean.getJournal_cat_id().toString());
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
        List<Journal> result = journalMapper.getJournalList(bean.getOrg_id(), catPath, bean.getSearchText());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public Object sitePageQuery(Sort sort, FindJournalBean bean) {
        List<String> catPaths = journalCatMapper.getFullPathsByCatIds(bean.getJournal_cat_id().toString());
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
        List<Journal> result = journalMapper.sitePageQuery(bean.getOrg_id(), catPath);
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "期刊")
    @Transactional
    public Journal saveJournal(Journal journal, Long user_id, Long org_id) {
        journal.setOrg_id(org_id);
        journal.setUser_id(user_id);
        journal.setJournal_status(2);// (1:启用；2:停用)
        journal.setHeadline(2);// 头条(1:是;2:否)
        journal.setRecommend(2);// 推荐(1:是;2:否)
        journal.setCreate_time(new Date());
        journal.setUpdate_time(new Date());
        journalMapper.insert(journal);
        return journal;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "期刊")
    @Transactional
    public Journal updateJournal(Journal journal, Long user_id) {
        Journal org_journal = journalMapper.selectById(journal.getJournal_id());
        journal.setUser_id(user_id);
        BeanUtils.copyProperties(journal, org_journal);
        journal.setUpdate_time(new Date());
        journalMapper.updateByPrimaryKey(journal);
        return journal;
    }

    @Override
    public Journal selectById(Long journal_id) {
        return journalMapper.selectById(journal_id);
    }

    @Override
    public List<Journal> selectByOrgId(Long org_id) {
        return journalMapper.getJournalList(org_id, null, null);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "期刊")
    public void deleteByCatIds(String cat_ids) {
        journalMapper.deleteByCatIds(cat_ids);
    }

    @Override
    public List<Journal> selectByCatIds(String cat_ids) {
        return journalMapper.selectByCatIds(cat_ids);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "期刊")
    public void deleteByJournalIds(String journal_ids_str){
        journalMapper.deleteByJournalIds(journal_ids_str);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "期刊")
    public Object updateStatus(Journal journal, Long user_id) {
        Journal art = journalMapper.selectById(journal.getJournal_id());

        //发布状态改变
        if(art.getJournal_status() != journal.getJournal_status()){
            if(journal.getJournal_status() == 2){
                //修改为不发布
                art.setPublish_time(null);
                art.setHeadline(2);
                art.setRecommend(2);
            }else{
                //修改为发布
                art.setPublish_time(new Date());
            }
            art.setJournal_status(journal.getJournal_status());
        }else{
            //如果当前发布状态为未发布，提示需要先发布
            if(journal.getJournal_status() == 2){
                return JsonResult.getError(Constants.JOURNAL_NOT_PUBLISH);
            }
            //推荐
            if(art.getRecommend() != journal.getRecommend()){
                art.setRecommend(journal.getRecommend());
            }
            //头条
            else if(art.getHeadline() != journal.getHeadline()){
                //仅一条头条：更新该条期刊头条状态，将其他设为非头条
                if(journal.getHeadline() == 1){
                    //不是推荐，则修改所有该分类下的为非
                    journalMapper.setNotHeadLineByCatId(journal.getJournal_cat_id());
                }
                art.setHeadline(journal.getHeadline());
            }
        }
        art.setUser_id(user_id);
        art.setUpdate_time(new Date());
        journalMapper.updateByPrimaryKey(art);
        JsonResult result = JsonResult.getSuccess(Constants.ACTION_UPDATE);
        result.setData(art);
        return result;
    }

    @Override
    public List<Map<String, Object>> selectRecommendListByCount(Long org_id, Integer count) {
        return journalMapper.selectRecommendListByCount(org_id, count);
    }

    @Override
    public Integer getCountByOrgId(Long org_id) {
        return journalMapper.getCountByOrgId(org_id);
    }

    @Override
    public Integer hasOffLine(Long orgid,String time,Object...otherparam) {
        Integer checknum = journalMapper.checkOffLineNum(orgid,time);
        if(checknum==null ) {
            checknum =0;
        }
        return checknum;
    }

    @Override
    public List<Journal> getOffLineNumList(Long orgid,String timev,Object...otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        return journalMapper.getOffLineNumList(orgid,timev, num, size);
    }

    @Override
    public Long getCatId(Long journal_id){
        return journalMapper.getCatId(journal_id);
    }
}
