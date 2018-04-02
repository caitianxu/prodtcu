package com.cjsz.tech.member.service.impl;

import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.member.beans.MemberSignListBean;
import com.cjsz.tech.member.domain.TbMemberSign;
import com.cjsz.tech.member.mapper.TbMemberSignMapper;
import com.cjsz.tech.member.service.TbMemberSignService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/9/12 0012.
 */
@Service
public class TbMemberSignServiceImpl implements TbMemberSignService {

    @Autowired
    private TbMemberSignMapper tbMemberSignMapper;


    @Override
    public Object getList(MemberSignListBean bean) {
        PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        List<TbMemberSign> result = tbMemberSignMapper.getList(bean);
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    public List<TbMemberSign> monthList(Long member_id, String date_time) {
        return tbMemberSignMapper.getMonthList(member_id,date_time);
    }

    @Override
    public TbMemberSign signIn(TbMemberSign tbMemberSign) {
        List<TbMemberSign> monthList = tbMemberSignMapper.monthList(tbMemberSign.getMember_id(),tbMemberSign.getSign_time());
        Integer count = 0;
        if (monthList != null && monthList.size() > 0){
            count = monthList.size();
        }
        tbMemberSign.setSign_month_count(count + 1);
        tbMemberSign.setCreate_time(new Date());
        tbMemberSignMapper.insert(tbMemberSign);
        return tbMemberSign;
    }

    @Override
    public TbMemberSign selectByMemeberIdAndTime(Long member_id, Date sign_time) {
        return tbMemberSignMapper.selectByMemberId(member_id,sign_time);
    }

    @Override
    public List<TbMemberSign> getMonthList(Long member_id) {
        return tbMemberSignMapper.monthList(member_id,new Date());
    }

    @Override
    public void updateSignGift(TbMemberSign memberSign) {
        tbMemberSignMapper.updateSignGift(memberSign.getSign_gift(), memberSign.getId());
    }
}
