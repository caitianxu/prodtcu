package com.cjsz.tech.member.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.member.domain.MemberDept;
import org.apache.ibatis.annotations.Select;

public interface MemberDeptMapper extends BaseMapper<MemberDept>{

    @Select("select * from tb_member_dept where account = #{0} limit 1")
    MemberDept selectByAccount(String account);
}
