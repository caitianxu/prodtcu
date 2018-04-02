package com.cjsz.tech.system.domain;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 *  角色
 * Created by Administrator on 2016/10/25.
 */
@Entity
@Table(name = "sys_role")
public class Role implements Serializable {
	
    @Id
    private Long    role_id;
    private Long    org_id;      //所属组织id
    private Integer role_type;   //标记是否为管理员角色（1：是，2：否）
    private String  role_name;   //角色名称
    private String  remark;      //角色描述
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date    create_time; //创建时间
    private Integer is_delete;   //是否删除（1：是  2：否）
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date    update_time; //更新时间
    
	public Long getRole_id() {
		return role_id;
	}
	public void setRole_id(Long role_id) {
		this.role_id = role_id;
	}
	public Long getOrg_id() {
		return org_id;
	}
	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}
	public Integer getRole_type() {
		return role_type;
	}
	public void setRole_type(Integer role_type) {
		this.role_type = role_type;
	}
	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public Integer getIs_delete() {
		return is_delete;
	}
	public void setIs_delete(Integer is_delete) {
		this.is_delete = is_delete;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
}
