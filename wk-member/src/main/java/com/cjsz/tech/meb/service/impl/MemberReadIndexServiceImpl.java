package com.cjsz.tech.meb.service.impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.meb.beans.MemberReadIndexBean;
import com.cjsz.tech.meb.domain.MemberReadIndex;
import com.cjsz.tech.meb.mapper.MemberReadIndexMapper;
import com.cjsz.tech.meb.service.MemberReadIndexService;
import com.cjsz.tech.system.beans.SearchBean;
import com.cjsz.tech.utils.DateUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;

/**
 * Created by LuoLi on 2017/4/13 0013.
 */
@Service
public class MemberReadIndexServiceImpl implements MemberReadIndexService {

    @Autowired
    private MemberReadIndexMapper memberReadIndexMapper;

    @Override
    public void addReadIndex() {
        List<MemberReadIndex> memberReadIndexList = new ArrayList<MemberReadIndex>();
        //每天0点更新数据：查询前一天所有用户阅读记录，统计总阅读时常、阅读总章节数，计算阅读指数：总时长*总章节数

        List<Map<String, Object>> dataList = memberReadIndexMapper.selectYesterdayData();
        for(Map<String, Object> data : dataList){
            MemberReadIndex memberReadIndex = new MemberReadIndex(Long.valueOf(data.get("member_id").toString()), Long.valueOf(data.get("total_time").toString()),
                    Long.valueOf(data.get("total_chapter").toString()), DateUtils.getDaysTime(new Date(), -1), Long.valueOf(data.get("read_index").toString()));
            memberReadIndexList.add(memberReadIndex);
        }
        if(memberReadIndexList.size()>0){
            memberReadIndexMapper.insertList(memberReadIndexList);
        }
    }

    @Override
    public Object sitePageQuery(Sort sort, PageConditionBean bean, String type,Long org_id) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<MemberReadIndexBean> readIndexList = new ArrayList<MemberReadIndexBean>();
        if(type.equals("week")){
            readIndexList = memberReadIndexMapper.getListByWeek(org_id);
        }else if(type.equals("month")){
            readIndexList = memberReadIndexMapper.getListByMonth(org_id);
        }else if(type.equals("year")){
            readIndexList = memberReadIndexMapper.getListByYear(org_id);
        }
        PageList pageList = new PageList(readIndexList, null);
        return pageList;
    }

    @Override
    public Map<String, Object> getMemberOrder(Long member_id) {
        return memberReadIndexMapper.getMemberOrder(member_id);
    }

    @Override
    public Object pageQuery(Sort sort, SearchBean searchBean) {
        PageHelper.startPage(searchBean.getPageNum(), searchBean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<MemberReadIndex> list = memberReadIndexMapper.pageQuery(searchBean);
        PageList pageList = new PageList(list, null);
        return pageList;
    }
}
