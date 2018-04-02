package com.cjsz.tech.system.domain;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.io.Serializable;
import java.util.Date;

/**
 *  系统用户
 * Created by Administrator on 2016/10/25.
 */
@Entity
@Table(name = "sys_user")
public class SysUser implements Serializable {
    @Id
    private Long    user_id;
    private String  user_name;      //系统用户帐号
    private String  user_real_name; //真实姓名
    private String  user_pwd;       //密码
    private Long    org_id;			//所属机构id
	private Long    dept_id;		//所属部门id
    private String  sex;            //性别
    private Date    birth;          //会员生日
    private String  icon;           //用户头像
    private String  email;          //用户邮箱
    private String  phone;          //联系电话
    private String  address;        //联系地址
    private Integer enabled;        //1: 启用  2: 停用
    private Integer is_delete;      //是否删除（1：是  2：否）
    private String  token;          
    @Transient
    private String  org_name;       //机构名称
	@Transient
	private String  dept_name;       //组织名称
    @Transient
    private String  role_id;        //角色id
    @Transient
    private String  role_type;      //角色类型
    @Transient
    private String  role_name;      //角色名称

    @Transient
    private String  root_org_name;      //根组织名称
    
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_real_name() {
		return user_real_name;
	}
	public void setUser_real_name(String user_real_name) {
		this.user_real_name = user_real_name;
	}
	public String getUser_pwd() {
		return user_pwd;
	}
	public void setUser_pwd(String user_pwd) {
		this.user_pwd = user_pwd;
	}
	public Long getOrg_id() {
		return org_id;
	}
	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}
	public Long getDept_id() {
		return dept_id;
	}
	public void setDept_id(Long dept_id) {
		this.dept_id = dept_id;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getOrg_name() {
		return org_name;
	}
	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}
	public String getDept_name() {
		return dept_name;
	}
	public void setDept_name(String dept_name) {
		this.dept_name = dept_name;
	}
	public String getRole_id() {
		return role_id;
	}
	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}
	public String getRole_type() {
		return role_type;
	}
	public void setRole_type(String role_type) {
		this.role_type = role_type;
	}
	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	public String getRoot_org_name() {
		return root_org_name;
	}
	public void setRoot_org_name(String root_org_name) {
		this.root_org_name = root_org_name;
	}
}
