package com.cjsz.tech.system.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.system.beans.OrganizationBean;
import com.cjsz.tech.system.domain.ProOrgExtend;
import com.cjsz.tech.system.domain.WebConfig;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 项目设置
 * Created by LuoLi on 2017/3/23 0023.
 */
public interface WebConfigMapper extends BaseMapper<WebConfig> {

    @Delete("delete from web_config where pro_org_extend_id = #{0}")
    void deleteById(Long pro_org_extend_id);

    @Delete("delete from web_config where pro_org_extend_id in (${ids})")
    void deleteByIds(@Param("ids") String ids);

    @Select("select * from web_config where pro_org_extend_id = #{0}")
    List<WebConfig> selectById(Long pro_org_extend_id);

}
