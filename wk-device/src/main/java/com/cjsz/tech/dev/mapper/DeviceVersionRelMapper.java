package com.cjsz.tech.dev.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.domain.DeviceVersionRel;
import com.cjsz.tech.dev.provider.DeviceProvider;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface DeviceVersionRelMapper extends BaseMapper<DeviceVersionRel> {
	
	@Select("select * from device_version_rel where version_id in (${ids})")
	List<DeviceVersionRel> getRelByVersionIds(@Param("ids") String ids);

	@SelectProvider(type = DeviceProvider.class, method = "getDevVersionList")
	List<Device> getList(@Param("keyword") String keyword);

	@Delete("delete from device_version_rel where device_id in (${ids})")
	void deleteRels(@Param("ids") String ids);
}
