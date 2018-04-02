package com.cjsz.tech.system.beans;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Transient;

/**
 *  组织机构
 * Created by Administrator on 2016/10/25.
 */
public class OrganizationBean implements Serializable {
	
	private Long    org_id;		//组织编码
    private Long    pid;        //父id
    private String  org_name;   //机构名
    private String  org_code;   //机构编码
    private String  remark;     //备注
    private Integer is_delete;  //是否删除（1：是  2：否）
    
    @JSONField(format="yyyy-MM-dd")
    private Date    create_time;
    
    @JSONField(format="yyyy-MM-dd")
    private Date    update_time;

	@Transient
	private Long  project_id;      	//机构所属项目ID
	@Transient
	private String  project_name;      	//机构所属项目名称
    
	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getOrg_name() {
		return org_name;
	}

	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}

	public String getOrg_code() {
		return org_code;
	}

	public void setOrg_code(String org_code) {
		this.org_code = org_code;
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

	public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	public Integer getIs_delete() {
		return is_delete;
	}

	public void setIs_delete(Integer is_delete) {
		this.is_delete = is_delete;
	}

	public List<OrganizationBean> getChildren() {
		return children;
	}

	public void setChildren(List<OrganizationBean> children) {
		this.children = children;
	}

	private List<OrganizationBean> children=new ArrayList<OrganizationBean>();

	public Long getProject_id() {
		return project_id;
	}

	public void setProject_id(Long project_id) {
		this.project_id = project_id;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
}
