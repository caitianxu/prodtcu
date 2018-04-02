package com.cjsz.tech.dev.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.dev.beans.DevicePageBean;
import com.cjsz.tech.dev.beans.FindDeviceSettingBean;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.domain.DeviceSetting;
import com.cjsz.tech.dev.provider.DeviceSettingProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface DeviceSettingMapper extends BaseMapper<DeviceSetting> {

    @SelectProvider(type = DeviceSettingProvider.class, method = "pageQuery")
    List<FindDeviceSettingBean> pageQuery(@Param("searchText") String searchText);

    //根据设备ID和更新时间查询设备的配置
    @Select("select conf.* from device_conf_rel rel left join device_setting conf on conf.conf_id = rel.conf_id and UNIX_TIMESTAMP(conf.update_time)>${1} where rel.device_id = #{0}")
    DeviceSetting getDeviceConfig(Long device_id, Long update_time);

    @Select("select * from device_setting where conf_name=#{0} ")
    DeviceSetting findByName(String conf_name);

    @Select("select d.*, org.org_name, concat(d.province, d.city, d.area) as address from device_conf_rel dc" +
                " left join device d on d.device_id = dc.device_id" +
            " LEFT JOIN sys_organization org on org.org_id = dc.org_id" +
            " where dc.conf_id = #{0}")
    List<Device> getDeviceConfRel(Long conf_id);

    @Select("select d.*, org.org_name, concat(d.province, d.city, d.area) as address from device_conf_rel dc" +
            " left join device d on d.device_id = dc.device_id" +
            " LEFT JOIN sys_organization org on org.org_id = dc.org_id" +
            " where dc.conf_id = #{0} and d.device_code like concat('%',#{1},'%')")
    List<Device> getDeviceConfRelByIdAndSearchText(Long conf_id, String searchText);

    @Select("select count(*) as num from device_setting where  update_time >#{1}   and conf_id in (select conf_id from device_conf_rel where  device_id = #{2} )  ")
    public Integer checkOffLineNum(Long orgid, String timev, Long devid);

    @Select("select * from device_setting where  update_time >#{1}    and conf_id in (select conf_id from device_conf_rel where  device_id = #{2} ) limit #{3}, #{4}")
    public List<DeviceSetting> getOffLineNumList(Long orgid, String timev, Long devid, Integer pageNum, Integer pageSize);

    @SelectProvider(type = DeviceSettingProvider.class, method = "getDeviceList")
    public List<Device> getDeviceList (@Param("bean") DevicePageBean bean);

    @Update("update device_setting set update_time = now() where conf_id= #{0}")
    void updateTime(Long conf_id);
}
