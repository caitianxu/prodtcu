package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.beans.SubjectOrgListBean;
import com.cjsz.tech.system.domain.SubjectOrgRel;
import com.cjsz.tech.system.provider.SubjectProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Administrator on 2017/9/25 0025.
 */
public interface SubjectOrgRelMapper extends BaseMapper<SubjectOrgRel>{

    //获取专题分配机构列表
    @SelectProvider(type = SubjectProvider.class,method = "orgList")
    List<SubjectOrgListBean> getOrgList(@Param("bean") SubjectOrgListBean bean);

    @Select("select * from subject_org_rel where org_id = #{0} and subject_id = #{1} limit 1")
    SubjectOrgRel selectOrgRel(Long org_id, Long subject_id);

    @Update("update subject_org_rel set is_delete = #{1} ,update_time = now() where rel_id = #{0}")
    void updateIsDelete(Long rel_id, Integer is_delete);

    @Update("update subject_org_rel set order_weight = #{1} , update_time = now() where subject_id = #{0} and org_id = #{2}")
    void orderSubjectByOrgId(Long subject_id, Long order_weight, Long org_id);

    @Update("update subject_org_rel set is_show = #{1} , update_time = now() where subject_id = #{0} and org_id = #{2}")
    void updateIsShow(Long subject_id, Integer is_show, Long org_id);

    //查询专题已分配机构数量
    @Select("select count(*) from subject_org_rel where subject_id = #{0} and is_delete = 2")
    Integer selectOrgCount(Long subject_id);

    @Update("update subject_org_rel set is_show = 2,update_time = now() where subject_id = #{0}")
    void updateisShow(Long subject_id);
}
