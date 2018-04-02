package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.domain.AdvCat;
import com.cjsz.tech.system.mapper.AdvCatMapper;
import com.cjsz.tech.system.service.AdvCatService;
import com.cjsz.tech.system.service.AdvService;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/22 0022.
 */
@Service
public class AdvCatServiceImpl implements AdvCatService {

    @Autowired
    private AdvCatMapper advCatMapper;

    @Autowired
    private AdvService advService;

    @Override
    //广告分类列表————分页
    public Object pageQuery(Sort sort, Long org_id, PageConditionBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<AdvCat> advCats = advCatMapper.getAdvCatList(org_id);
        PageList pageList = new PageList(advCats, null);
        return pageList;
    }

    @Override
    //广告分类列表
    public List<AdvCat> getAllAdvCat(Long org_id) {
        return advCatMapper.getAdvCatList(org_id);
    }

    @Override
    //广告分类添加
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "广告分类")
    public void saveAdvCat(AdvCat advCat) {
        advCatMapper.insert(advCat);
    }

    @Override
    //广告分类修改
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "广告分类")
    public void updateAdvCat(AdvCat advCat) {
        advCatMapper.updateByPrimaryKey(advCat);
    }

    @Override
    @Transactional
    //删除同时删除分类下广告
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "广告分类")
    public void deleteAdvCats(String catIdsStr) {
        //删除广告
        advService.deleteAdvByCats(catIdsStr);
        //删除分类
        advCatMapper.deleteAdvCats(catIdsStr);
    }

    @Override
    //分类名查找
    public List<AdvCat> selectByCatName(String adv_cat_name) {
        return advCatMapper.selectByCatName(adv_cat_name);
    }

    @Override
    //分类名查找不包括本身
    public List<AdvCat> selectOtherByCatName(String adv_cat_name, Long adv_cat_id) {
        return advCatMapper.selectOtherByCatName(adv_cat_name, adv_cat_id);
    }

    @Override
    public AdvCat selectByCatCode(String adv_cat_code) {
        return advCatMapper.selectByCatCode(adv_cat_code);
    }

    @Override
    public AdvCat selectOtherByCatCode(String adv_cat_code, Long adv_cat_id) {
        return advCatMapper.selectOtherByCatCode(adv_cat_code, adv_cat_id);
    }

    @Override
    public List<AdvCat> getAdvCatByProjectCode(String project_code) {
        return advCatMapper.getAdvCatByProjectCode(project_code);
    }

    @Override
    public List<AdvCat> getOffLineNumList(Long orgid, String oldtimestamp, Object... otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        //分类共用org_id = 1
        return advCatMapper.getOffLineNumList(1L, oldtimestamp, num, size);
    }

    @Override
    public Integer hasOffLine(Long orgid, String timev, Object... otherparam) {
        //分类共用org_id = 1
        Integer checknum = advCatMapper.checkOffLineNum(1L, timev);
        if (checknum == null) {
            checknum = 0;
        }
        return checknum;
    }
}