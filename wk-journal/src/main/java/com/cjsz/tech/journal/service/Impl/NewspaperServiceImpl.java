package com.cjsz.tech.journal.service.Impl;

import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.journal.beans.FindNewspaperBean;
import com.cjsz.tech.journal.domain.Newspaper;
import com.cjsz.tech.journal.mapper.NewspaperCatMapper;
import com.cjsz.tech.journal.mapper.NewspaperMapper;
import com.cjsz.tech.journal.service.NewspaperService;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Jason on 16/3/3.
 */
@Service
public class NewspaperServiceImpl implements NewspaperService {

    @Autowired
    private NewspaperMapper newspaperMapper;

    @Autowired
    private NewspaperCatMapper newspaperCatMapper;

    @Override
    public Object pageQuery(Sort sort, FindNewspaperBean bean) {
        List<String> catPaths = newspaperCatMapper.getFullPathsByCatIds(bean.getNewspaper_cat_id().toString());
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
        List<Newspaper> result = newspaperMapper.pageQuery(bean.getOrg_id(), catPath, bean.getSearchText());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public Object pageSiteQuery(Sort sort, FindNewspaperBean bean) {
        String ids = "0";
        if(bean.getNewspaper_cat_id() != null){
            ids = bean.getNewspaper_cat_id().toString();
        }
        List<String> catPaths = newspaperCatMapper.getFullPathsByCatIds(ids);
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
        List<Newspaper> result = newspaperMapper.pageSiteQuery(bean.getOrg_id(), catPath, bean.getSearchText());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "报纸")
    @Transactional
    public Newspaper saveNewspaper(Newspaper newspaper) {
        newspaper.setCreate_time(new Date());
        newspaper.setUpdate_time(new Date());
        newspaperMapper.insert(newspaper);
        return newspaper;
    }

    @Override
    @SysActionLogAnnotation(action_type =SysActionLogType.UPDATE, action_log_module_name = "报纸")
    @Transactional
    public Newspaper updateNewspaper(Newspaper newspaper) {
        Newspaper org_newspaper = newspaperMapper.selectById(newspaper.getNewspaper_id());
        BeanUtils.copyProperties(newspaper, org_newspaper);
        newspaper.setUpdate_time(new Date());
        newspaperMapper.updateByPrimaryKey(newspaper);
        return newspaper;
    }

    @Override
    public Newspaper selectById(Long newspaper_id) {
        return newspaperMapper.selectById(newspaper_id);
    }

    @Override
    public List<Newspaper> selectByCatIds(String cat_ids) {
        return newspaperMapper.selectByCatIds(cat_ids);
    }

    @Override
    @SysActionLogAnnotation(action_type =SysActionLogType.DELETE, action_log_module_name = "报纸")
    @Transactional
    public void deleteByIds(String newspaper_ids) {
        newspaperMapper.deleteByNewspaperIds(newspaper_ids);
    }

    @Override
    public Integer getCountByOrgId(Long org_id) {
        return newspaperMapper.getCountByOrgId(org_id);
    }

    @Override
    @SysActionLogAnnotation(action_type =SysActionLogType.UPDATE, action_log_module_name = "报纸")
    public void updateStatus(Integer is_recommend, Long newspaper_id) {
        newspaperMapper.updateStatus(is_recommend, newspaper_id);
    }

    @Override
    public List getOffLineNumList(Long org_id, String oldTimeStamp,Object...otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        return newspaperMapper.getOffLineNumList(org_id, oldTimeStamp, num, size);
    }

    @Override
    public Integer hasOffLine(Long org_id, String oldTimeStamp,Object...otherparam) {
        Integer checknum = newspaperMapper.checkOffLineNum(org_id, oldTimeStamp);
        if(checknum == null ) {
            checknum = 0;
        }
        return checknum;
    }

    @Override
    public Long getNewsCatId(Long newspaper_id){
        return newspaperMapper.getNewsCatId(newspaper_id);
    }

    @Override
    public void updateOrder(Long newspaper_id, Long order_weight) {
        newspaperMapper.updateOrder(newspaper_id,order_weight);
    }
}
