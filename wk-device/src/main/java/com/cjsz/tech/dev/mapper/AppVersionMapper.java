package com.cjsz.tech.dev.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.dev.domain.AppVersion;
import com.cjsz.tech.dev.domain.Device;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Li Yi on 2016/12/20.
 */
public interface AppVersionMapper extends BaseMapper<AppVersion>{

    @Select("select * from app_version")
    List<AppVersion> getAppVersionList();

    @Select("select * from app_version where version_id = #{0}")
    AppVersion selectByVersionId(Long version_id);

    @Update("update  app_version set is_delete = 2,update_time = now() where version_id in(${appVersion_ids_str})")
    void deleteByVersionIds(@Param("appVersion_ids_str") String appVersion_ids_str);

    @Select("select d.*, org.org_name, concat(d.province, d.city, d.area) as address from device_version_rel dv" +
            " left join device d on d.device_id = dv.device_id" +
            " LEFT JOIN sys_organization org on org.org_id = dv.org_id" +
            " where dv.version_id = #{0}")
    List<Device> getDeviceVersionRel(Long version_id);

    @Update("update app_version set enabled = #{0},update_time = now() where enabled != #{0}")
    void updateEnabled(Integer enabled);

    @Select("select * from app_version where is_delete = 2 and version_id in(${version_ids})")
    List<AppVersion> selectByVersionIds(@Param("version_ids") String appVersion_ids_str);

    @Select("select * from app_version where is_delete = 2 and enabled = 1")
    AppVersion selectEnabled();

}
