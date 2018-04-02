package com.cjsz.tech.dev.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.dev.domain.DeviceConfRel;
import com.cjsz.tech.dev.domain.DeviceSetting;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface DeviceConfRelMapper extends BaseMapper<DeviceConfRel> {

    @Select("select * from device_conf_rel where conf_id = #{0}")
    List<DeviceConfRel> selectDeviceConfRel(Long conf_id);

    @Select("select * from device_conf_rel where device_id = #{0}")
    DeviceConfRel selectByDeviceId(Long device_id);

    @Delete("delete from device_conf_rel where device_id in(${device_ids})")
    void deleteByDeviceIds(@Param("device_ids") String device_ids);

    @Select("select * from device_conf_rel where device_id = #{0} ")
    DeviceConfRel selectByDeviceOrgConf (Long device_id);

    @Select("select ds.* from device_conf_rel dcr left join device_setting ds on ds.conf_id= dcr.conf_id where  " +
            " dcr.update_time >#{1} and dcr.device_id= #{2}")
    public List<DeviceSetting> getOffLineNumList(Long orgid, String timev, Long devid);
}
