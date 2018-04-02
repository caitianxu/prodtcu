package com.cjsz.tech.meb.service;

import com.cjsz.tech.meb.domain.TbMember;
import org.springframework.stereotype.Service;

/**
 * Created by daixiaofeng on 2017/7/7.
 */
public interface TbMemberService {
    //通过token查找会员
    public TbMember findByToken(String token, String token_type);//1:web;2:移动端

    //更新会员
    public void updateMember(TbMember member);
}
