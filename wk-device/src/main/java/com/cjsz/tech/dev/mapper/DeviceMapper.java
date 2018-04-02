package com.cjsz.tech.dev.mapper;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.dev.beans.DeviceInfoBean;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.provider.DeviceProvider;
import com.cjsz.tech.system.domain.Organization;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface DeviceMapper extends BaseMapper<Device> {

	@SelectProvider(type = DeviceProvider.class, method = "selectAll")
	List<Device> getList(@Param("keyword") String keyword, @Param("org_id") Long org_id, @Param("enabled") Integer enabled);

	@Select("select device_id from device order by device_id desc limit 1")
	Long getMaxId();

	@Update("update device set enabled = #{status} , update_time = now() where device_id in (${ids})")
	void updateStatusByIds(@Param("status") Integer status, @Param("ids") String ids);

	@Select("select * from device where org_id = #{0} and user_id = #{1} and device_code = #{2}")
	Device getDevice(Long org_id, Long user_id, String device_code);

	@Select("SELECT o.* FROM device d LEFT JOIN sys_organization o on o.org_id = d.org_id where o.is_delete = 2 and d.enabled = 1 GROUP BY org_id")
	List<Organization> getDeviceOrg();

	@Select("select * from device where device_code = #{0}")
	Device getDeviceByCode(String device_code);

	@Select("select * from device where enabled = 1")
	List<Device> getAllList();

	@Select("select * from device where device_id in (${ids})")
	List<Device> getListByIds(@Param("ids") String ids);

	//图书离线管理获取机构启用的设备当前信息（在线离线、剩余内存、设备离线图书数量）
	@Select("select d.device_id, d.device_code, d.off_line, d.memory, " +
			"(select count(*) from book_device_rel bdr where bdr.org_id = #{0} and bdr.device_id = d.device_id and bdr.`status` = 2 ) book_count " +
			"from device d " +
			"where d.org_id = #{0} and d.enabled = 1")
	List<DeviceInfoBean> getDeviceInfoBean(Long org_id);

	@Select("select count(*) as num from device where org_id=#{0} and  update_time >#{1} and device_id = #{2}")
    public Integer checkOffLineNum(Long orgid, String timev, Long device_id);

    @Select("select * from device where org_id=#{0} and  update_time >#{1} and device_id = #{2} limit #{3}, #{4}")
    public List<Device> getOffLineNumList(Long orgid, String timev, Long device_id, Integer pageNum, Integer pageSize);

	@Select("select * from device where org_id = #{0}")
	public List<Device> getDevicesByOrgId(Long org_id);

	//所有设备
	@Select("select * from device")
	public List<Device> findAll();

	//查询终端数量
	@Select("select COUNT(*) from device WHERE org_id=#{0}")
	Integer getCountByOrgId(Long org_id);

	//数据同步时：更新设备离线状态、剩余内存、同步时间、版本信息
	@Update("UPDATE device SET memory=#{0},sync_time = now(),version = #{2} where device_code =#{1}")
	void updeatOfflineParam(String memory,String deviceCode,String version);

	//开始下载更改状态为文件同步中
	@Update("update device set off_line = '文件同步中' where device_code = #{0}")
	void updateStatusStart(String device_code);

	//开始下载更改状态为文件同步完成
	@Update("update device set off_line = '文件同步完成' where device_code = #{0}")
	void updateStatusEnd(String device_code);

	@Update("update device set update_time = now() where device_id = #{0}")
	void updateDeviceTime(Long device_id);

	@Select("select org_id from device where device_code=#{0} and enabled=1 limit 1")
    Long selectByIds(String device_code);

    @Update("update device set is_sync = #{1}, update_time = now() where device_id = #{0}")
    void updateIsSyncById(Long device_id, Integer is_sync);

    @Select("select device_code from device where is_sync = 1")
    List<String> getSyncDeviceCodes();

}
