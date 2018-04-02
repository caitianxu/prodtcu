package com.cjsz.tech.cms.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.cms.beans.FindActivityBean;
import com.cjsz.tech.cms.domain.Activity;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
public interface ActivityService {

    /**
     * 分页查询本机构的活动
     *
     * @param sort
     * @param bean
     * @return
     */
    public Object pageQuery(Sort sort, FindActivityBean bean);

    /**
     * 前台查询活动(启用)
     *
     * @param sort
     * @param bean
     * @return
     */
    public Object sitePageQuery(Sort sort, PageConditionBean bean);

    /**
     * 添加活动
     *
     * @param activity
     */
    public Activity saveActivity(Activity activity, Long user_id, Long org_id);

    /**
     * 修改活动
     *
     * @param activity
     */
    public Activity updateActivity(Activity activity, Long user_id);

    /**
     * 通过活动ID查找活动
     *
     * @param activity_id
     * @return
     */
    public Activity selectById(Long activity_id);

    /**
     * 活动Ids查找机构的活动列表
     * @param org_id
     * @param activity_ids_str
     * @return
     */
    List<Activity> selectByOrgAndActivityIds(Long org_id, String activity_ids_str);

    /**
     * 通过活动Ids删除活动
     * @param activity_ids_str
     */
    public void deleteByActivityIds(String activity_ids_str);

}
