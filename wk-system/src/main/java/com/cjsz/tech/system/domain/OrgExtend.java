package com.cjsz.tech.system.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 *  机构扩展属性
 * Created by Administrator on 2016/10/25.
 */
@Entity
@Table(name = "sys_org_extend")
public class OrgExtend implements Serializable {
	
    @Id
    private Long    org_extend_id;      //机构扩展id
    private Long    org_id;		    	//机构id
    private String  short_name;   		//机构简称
    private String  extend_code;   		//机构编码
    private String  province;      		//所在省份
    private String  province_id;		//省份code
    private String  city;     			//所在城市
    private String  city_id;			//城市code
    private String  area;     			//所在区域
    private String  area_id;			//区域code
    private String  street;     		//所在街道
    private String  contacts_name;      //联系人姓名
    private String  contacts_phone;     //联系方式
    private String  admin_logo;     	//后台首页logo
    private String  big_screen_logo;    //大屏logo
    private String  big_screen_bg;      //大屏背景
    private String  admin_login_logo;   //后台登录logo
    private Integer is_delete;  		//是否删除（1：是  2：否）
	private String  project_code;      	//机构所属项目编号
	@Transient
	private Long  project_id;      	//机构所属项目ID
	@Transient
	private String  project_name;      	//机构所属项目名称
    
	public Long getOrg_extend_id() {
		return org_extend_id;
	}
	public void setOrg_extend_id(Long org_extend_id) {
		this.org_extend_id = org_extend_id;
	}
	public Long getOrg_id() {
		return org_id;
	}
	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}
	public String getShort_name() {
		return short_name;
	}
	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}
	public String getExtend_code() {
		return extend_code;
	}
	public void setExtend_code(String extend_code) {
		this.extend_code = extend_code;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getArea() {
		return area;
	}
	public String getProvince_id() {
		return province_id;
	}
	public void setProvince_id(String province_id) {
		this.province_id = province_id;
	}
	public String getCity_id() {
		return city_id;
	}
	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}
	public String getArea_id() {
		return area_id;
	}
	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getContacts_name() {
		return contacts_name;
	}
	public void setContacts_name(String contacts_name) {
		this.contacts_name = contacts_name;
	}
	public String getContacts_phone() {
		return contacts_phone;
	}
	public void setContacts_phone(String contacts_phone) {
		this.contacts_phone = contacts_phone;
	}
	public String getAdmin_logo() {
		return admin_logo;
	}
	public void setAdmin_logo(String admin_logo) {
		this.admin_logo = admin_logo;
	}
	public String getBig_screen_logo() {
		return big_screen_logo;
	}
	public void setBig_screen_logo(String big_screen_logo) {
		this.big_screen_logo = big_screen_logo;
	}
	public String getBig_screen_bg() {
		return big_screen_bg;
	}
	public void setBig_screen_bg(String big_screen_bg) {
		this.big_screen_bg = big_screen_bg;
	}
	public String getAdmin_login_logo() {
		return admin_login_logo;
	}
	public void setAdmin_login_logo(String admin_login_logo) {
		this.admin_login_logo = admin_login_logo;
	}
	public Integer getIs_delete() {
		return is_delete;
	}
	public void setIs_delete(Integer is_delete) {
		this.is_delete = is_delete;
	}

	public String getProject_code() {
		return project_code;
	}

	public void setProject_code(String project_code) {
		this.project_code = project_code;
	}

	public String getProject_name() {

		return project_name;
	}

	public Long getProject_id() {
		return project_id;
	}

	public void setProject_id(Long project_id) {
		this.project_id = project_id;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
}
