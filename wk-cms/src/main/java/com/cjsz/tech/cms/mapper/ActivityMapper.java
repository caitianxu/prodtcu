package com.cjsz.tech.cms.mapper;

import com.cjsz.tech.cms.beans.FindActivityBean;
import com.cjsz.tech.cms.domain.Activity;
import com.cjsz.tech.cms.domain.Article;
import com.cjsz.tech.cms.provider.ActivityProvider;
import com.cjsz.tech.core.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
public interface ActivityMapper extends BaseMapper<Activity> {

    //分页查询本机构的活动
    @SelectProvider(type = ActivityProvider.class, method = "getActivityList")
    List<Activity> getActivityList(@Param("bean") FindActivityBean bean);

    @Select("select * from activity where activity_id = #{0} ")
    public Activity selectById(Long activity_id);

    @Delete("delete from activity where activity_id in (${activity_ids_str})")
    public void deleteByActivityIds(@Param("activity_ids_str") String activity_ids_str);

    @Select("select * from activity where org_id = #{org_id} and activity_id in(${activity_ids})")
    List<Activity> selectByOrgAndActivityIds(@Param("org_id") Long org_id, @Param("activity_ids") String activity_ids_str);

    @Select("select * from activity where activity_status = #{0} ")
    List<Activity> getListByStatus(Integer activity_status);
}
