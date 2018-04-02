package com.cjsz.tech.meb.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.meb.domain.TbMember;
import org.apache.ibatis.annotations.Select;

/**
 * Created by daixiaofeng on 2017/7/7.
 */
public interface TbMemberMapper extends BaseMapper<TbMember> {

    @Select("select * from tb_member where token=#{0} AND token_type=#{1}")
    TbMember findByToken(String token, String token_type);
}
