package com.cjsz.tech.dev.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.dev.domain.AppVersionAndroid;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Li Yi on 2016/12/20.
 */
public interface AppVersionAndroidMapper extends BaseMapper<AppVersionAndroid>{

    @Select("select a.*,t.app_type_name from app_version_android a left join app_type t on a.app_type_id = t.app_type_id where a.is_delete = 2 ")
    List<AppVersionAndroid> getAppVersionList();

    @Update("update app_version_android set is_delete = 1 ,update_time = now() where version_id in(${appVersion_ids_str})")
    void deleteByVersionIds(@Param("appVersion_ids_str") String appVersion_ids_str);

    @Update("update app_version_android set enabled = #{0},update_time = now() where enabled != #{0} and app_type_id = #{1}")
    void updateEnabled(Integer enabled,Long app_type_id);

    @Select("select * from app_version_android where is_delete = 2 and version_id in(${version_ids})")
    List<AppVersionAndroid> selectByVersionIds(@Param("version_ids") String appVersion_ids_str);

    @Select("select * from app_version_android where is_delete = 2 and enabled = 1 and app_type_id = #{0} limit 1")
    AppVersionAndroid selectEnabled(Integer app_type);

}
