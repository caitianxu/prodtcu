package com.cjsz.tech.cms.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.cms.beans.FindActivityBean;
import com.cjsz.tech.cms.domain.Activity;
import com.cjsz.tech.cms.domain.Article;
import com.cjsz.tech.cms.mapper.ActivityMapper;
import com.cjsz.tech.cms.service.ActivityService;
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
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public Object pageQuery(Sort sort, FindActivityBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<Activity> result = activityMapper.getActivityList(bean);
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public Object sitePageQuery(Sort sort, PageConditionBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        List<Activity> result = activityMapper.getListByStatus(1);//1: 启用；2:停用
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "活动")
    @Transactional
    public Activity saveActivity(Activity activity, Long user_id, Long org_id) {
        activity.setOrg_id(org_id);
        activity.setUser_id(user_id);
        activity.setActivity_status(2);//1: 启用；2:停用
        activity.setActivity_content(activity.getActivity_content().toString());
        activity.setCreate_time(new Date());
        activity.setUpdate_time(new Date());
        activityMapper.insert(activity);
        return activity;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "活动")
    @Transactional
    public Activity updateActivity(Activity activity, Long user_id) {
        Activity org_activity = activityMapper.selectById(activity.getActivity_id());
        activity.setUser_id(user_id);
        activity.setActivity_content(activity.getActivity_content().toString());
        activity.setUpdate_time(new Date());
        BeanUtils.copyProperties(activity, org_activity);
        activityMapper.updateByPrimaryKey(activity);
        return activity;
    }

    @Override
    public Activity selectById(Long article_id) {
        return activityMapper.selectById(article_id);
    }

    @Override
    public List<Activity> selectByOrgAndActivityIds(Long org_id, String activity_ids_str) {
        return activityMapper.selectByOrgAndActivityIds(org_id, activity_ids_str);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "活动")
    public void deleteByActivityIds(String activity_ids_str){
        activityMapper.deleteByActivityIds(activity_ids_str);
    }

}
