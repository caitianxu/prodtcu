package com.cjsz.tech.member.service;

import com.cjsz.tech.member.beans.MemberSignListBean;
import com.cjsz.tech.member.domain.TbMemberSign;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/9/12 0012.
 */
public interface TbMemberSignService {
    //查询会员签到记录
    Object getList(MemberSignListBean bean);

    //查询会员月签到记录
    List<TbMemberSign> monthList(Long member_id, String date_time);

    //会员签到
    TbMemberSign signIn(TbMemberSign tbMemberSign);

    //验证会员今日是否签到
    TbMemberSign selectByMemeberIdAndTime(Long member_id, Date sign_time);

    //查询会员本月签到记录
    List<TbMemberSign> getMonthList(Long member_id);

    void updateSignGift(TbMemberSign memberSign);
}
