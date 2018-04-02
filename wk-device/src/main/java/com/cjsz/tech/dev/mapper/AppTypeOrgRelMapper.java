package com.cjsz.tech.dev.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.dev.beans.OrgListBean;
import com.cjsz.tech.dev.domain.AppTypeOrgRel;
import com.cjsz.tech.dev.provider.AppTypeProvider;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
public interface AppTypeOrgRelMapper extends BaseMapper<AppTypeOrgRel>{

    @Select("select count(*) from app_type_org_rel where app_type_id = #{0}")
    Integer selectCountById(Long app_type_id);

    @Select("select * from app_type_org_rel where org_id = #{0} limit 1 ")
    AppTypeOrgRel selectById(Long org_id);

    @SelectProvider(type = AppTypeProvider.class,method = "getOrgList")
    List<OrgListBean> orgList(@Param("app_type_id") Long app_type_id,@Param("searchText") String searchText);

    @Select("select * from app_type_org_rel where org_id = #{0} limit 1")
    AppTypeOrgRel selectByOrgId(Long org_id);

}
