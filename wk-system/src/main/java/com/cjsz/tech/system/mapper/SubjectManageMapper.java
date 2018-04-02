package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.domain.SubjectManage;
import com.cjsz.tech.system.provider.SubjectProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/9/25 0025.
 */
public interface SubjectManageMapper extends BaseMapper<SubjectManage>{

    @SelectProvider(type = SubjectProvider.class,method = "getList")
    List<SubjectManage> getList(@Param("searchText") String searchText,@Param("org_id") Long org_id);

    @Select("select * from subject_manage where subject_id = #{0} limit 1")
    SubjectManage findById(Long subject_id);

    @Update("update subject_manage set is_delete = 1 , update_time = now() where subject_id = #{0}")
    void deleteById(Long subject_id);

    //查询新增的专题
    @Select("select * from subject_manage where subject_name = #{0} order by create_time desc limit 1")
    SubjectManage selectByName(String subject_name);

    @Select("select s.*,r.is_show,r.order_weight from subject_org_rel r left join subject_manage s " +
            " on s.subject_id = r.subject_id where s.is_delete = 2 and r.is_delete =2 and r.is_show = 1 " +
            " and s.enabled = 1 and  r.org_id = #{0} order by r.order_weight desc")
    List<SubjectManage> siteList( Long org_id);
}
