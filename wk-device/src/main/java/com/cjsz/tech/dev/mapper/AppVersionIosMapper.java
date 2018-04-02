package com.cjsz.tech.dev.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.dev.domain.AppVersionIos;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Li Yi on 2016/12/20.
 */
public interface AppVersionIosMapper extends BaseMapper<AppVersionIos>{

    @Select("select a.*,t.app_type_name from app_version_ios a left join app_type t on a.app_type_id = t.app_type_id where a.is_delete = 2 ")
    List<AppVersionIos> getAppVersionList();

    @Update("update app_version_ios set is_delete = 1,update_time = now() where version_id in(${appVersion_ids_str})")
    void deleteByVersionIds(@Param("appVersion_ids_str") String appVersion_ids_str);

    @Update("update app_version_ios set enabled = #{0},update_time = now() where enabled != #{0} and app_type_id = #{1} ")
    void updateEnabled(Integer enabled,Long app_type_id);

    @Select("select * from app_version_ios where is_delete =2 and version_id in(${version_ids})")
    List<AppVersionIos> selectByVersionIds(@Param("version_ids") String appVersion_ids_str);

    @Select("select * from app_version_ios where is_delete = 2 and enabled = 1 and app_type_id = #{0} limit 1")
    AppVersionIos selectEnabled(Integer app_type);

}
