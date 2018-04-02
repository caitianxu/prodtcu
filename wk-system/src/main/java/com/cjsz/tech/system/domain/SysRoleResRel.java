package com.cjsz.tech.system.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 权限资源实体类
 * Created by Administrator on 2016/11/8 0008.
 */
@Entity
@Table(name = "sys_role_res_rel")
public class SysRoleResRel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long role_res_rel_id;//唯一编码

    private Long role_id;//角色编码

    private Long res_id;//资源编码

    private Integer data_type_id;//数据权限类型

    private Integer enabled;

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;//创建时间
    
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date update_time;//更新时间
    
    public Long getRole_res_rel_id() {
        return role_res_rel_id;
    }

    public void setRole_res_rel_id(Long role_res_rel_id) {
        this.role_res_rel_id = role_res_rel_id;
    }

    public Long getRole_id() {
        return role_id;
    }

    public void setRole_id(Long role_id) {
        this.role_id = role_id;
    }

    public Long getRes_id() {
        return res_id;
    }

    public void setRes_id(Long res_id) {
        this.res_id = res_id;
    }

    public Integer getData_type_id() {
        return data_type_id;
    }

    public void setData_type_id(Integer data_type_id) {
        this.data_type_id = data_type_id;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

	public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
}
