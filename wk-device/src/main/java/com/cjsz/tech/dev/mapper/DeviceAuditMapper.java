package com.cjsz.tech.dev.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import com.cjsz.tech.core.BaseMapper;
import com.cjsz.tech.dev.domain.DeviceAudit;
import com.cjsz.tech.dev.provider.DeviceAuditProvider;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface DeviceAuditMapper extends BaseMapper<DeviceAudit> {

	@SelectProvider(type = DeviceAuditProvider.class, method = "selectAll")
	List<DeviceAudit> getList(@Param("keyword") String keyword);
	
	@Select("select device_audit_id from device_audit order by device_audit_id desc limit 1")
	Long getMaxId();
	
	@Select("select distinct audit_status from device_audit where device_audit_id in (${ids})")
	List<Integer> getAuditStatus(@Param("ids") String ids);
	
	@Update("update device_audit set audit_status = #{status} where device_audit_id in (${ids})")
	void updateStatusByIds(@Param("status") Integer status, @Param("ids") String ids);
	
	@Select("select * from device_audit where device_audit_id in (${ids}) and audit_status = #{status}")
	List<DeviceAudit> getListByIds(@Param("ids") String ids, @Param("status") Integer status);
	
	@Select("select * from device_audit where device_code = #{0} and org_id = #{1} and user_id = #{2} and audit_status = 3 limit 1")
	DeviceAudit getByParam(String device_code, Long org_id, Long user_id);

	@Select("select * from device_audit where device_code = #{0} and audit_status = 3 limit 1")
	DeviceAudit getWaitAudit(String device_code);
}
