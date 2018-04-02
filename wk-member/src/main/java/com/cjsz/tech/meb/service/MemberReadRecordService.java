package com.cjsz.tech.meb.service;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.meb.domain.MemberReadRecord;
import com.cjsz.tech.system.beans.SearchBean;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by LuoLi on 2017/4/13 0013.
 */
public interface MemberReadRecordService {

    //添加记录(更新记录)
    public void updateRecord(Long org_id, MemberReadRecord memberReadRecord, String device_type);

    //今日已读时长
    public Integer getTodayReadTime(Long member_id);

    //已读本数
    public Integer getReadBookCount(Long member_id);

    //阅读记录
    public Object pageQuery( SearchBean searchBean,Long org_id);

    //添加阅读记录、更新阅读记录
    public void updateRecord_v2(Long org_id, MemberReadRecord memberReadRecord ,String token_type);

    //查询阅读记录
    public Object queryList(Sort sort, PageConditionBean bean, Long member_id);

    //删除所有阅读记录
    void deleteReadRecord_v2(Long member_id);

    //查询点击记录
    Object clickList(Sort sort , SearchBean bean,Long org_id);

    //大众版查询会员阅读记录
    Object getMemberReadList(Sort sort, PageConditionBean bean, Long member_id);
}
