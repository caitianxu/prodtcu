package com.cjsz.tech.system.domain;


import javax.persistence.*;

import com.alibaba.fastjson.annotation.JSONField;
import org.omg.PortableInterceptor.INACTIVE;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *  组织机构
 * Created by Administrator on 2016/10/25.
 */
@Entity
@Table(name = "sys_organization")
public class Organization implements Serializable {
	
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    org_id;
    private Long    pid;        //父id
    private String  full_path;        //层次路径
    private String  org_name;   //机构名
    private String  org_code;   //机构编码
    private String  remark;     //备注
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date    create_time;
    private Integer enabled;    //1: 启用  2: 停用
	private Integer pwd_lock;	//机构会员密码是否锁定（1：是，2：否）
    private Integer is_delete;  //是否删除（1：是  2：否）
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date    update_time; //更新时间
//	@Transient
//	private Long  project_id;      	//机构所属项目ID
	@Transient
	private String  project_name;      	//机构所属项目名称

	@Transient
	private List<Organization> children;
    
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
	public String getFull_path() {
		return full_path;
	}
	public void setFull_path(String full_path) {
		this.full_path = full_path;
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
	public Integer getEnabled() {
		return enabled;
	}
	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
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

	/*public Long getProject_id() {
		return project_id;
	}

	public void setProject_id(Long project_id) {
		this.project_id = project_id;
	}*/

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public Integer getPwd_lock() {
		return pwd_lock;
	}

	public void setPwd_lock(Integer pwd_lock) {
		this.pwd_lock = pwd_lock;
	}

	public List<Organization> getChildren() {
		return children;
	}

	public void setChildren(List<Organization> children) {
		this.children = children;
	}
}
