package com.cjsz.tech.meb.service.impl;

import com.cjsz.tech.meb.domain.TbMember;
import com.cjsz.tech.meb.mapper.TbMemberMapper;
import com.cjsz.tech.meb.service.TbMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by daixiaofeng on 2017/7/7.
 */
@Service
public class TbMemberServiceImpl implements TbMemberService{

    @Autowired
    private TbMemberMapper tbmemberMapper;

    @Override
    public TbMember findByToken(String token, String token_type) {
        return tbmemberMapper.findByToken(token, token_type);
    }

    @Override
    public void updateMember(TbMember member) {
        tbmemberMapper.updateByPrimaryKey(member);
    }
}
