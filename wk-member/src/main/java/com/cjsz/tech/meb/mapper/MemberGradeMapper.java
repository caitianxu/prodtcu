package com.cjsz.tech.meb.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.meb.domain.MemberGrade;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 会员等级
 * Created by Administrator on 2017/3/15 0015.
 */
public interface MemberGradeMapper extends BaseMapper<MemberGrade> {

    @Select("select * from member_grade")
    public List<MemberGrade> getAllList();

    @Select("select * from member_grade where grade_name like concat('%',#{0},'%') or grade_title like concat('%',#{0},'%')")
    public List<MemberGrade> getListBySearchText(String searchText);

    @Select("select * from member_grade where grade_id = #{0}")
    public MemberGrade selectById(Long grade_id);

    @Select("select * from member_grade where grade_name = #{0}")
    public MemberGrade selectByGradeName(Integer grade_name);

    @Select("select * from member_grade where grade_point <= #{0} order by grade_point desc limit 1")
    public MemberGrade selectByPoints(Long cur_points);

    @Select("select * from member_grade where grade_id = #{0}")
    public MemberGrade selectByGradeId(Long grade_id);
}
