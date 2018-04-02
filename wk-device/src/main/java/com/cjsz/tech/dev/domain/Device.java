package com.cjsz.tech.dev.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 设备信息
 * Created by Administrator on 2016/10/25.
 */
@Entity
@Table(name = "device")
public class Device implements Serializable {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    device_id;		//设备id
    private String  device_code;	//设备编码
    private Long    org_id;			//机构id
    private Long    user_id;		//用户id
    private String  province;      	//所在省份
    private String  province_id;	//省份code
    private String  city;     		//所在城市
    private String  city_id;		//城市code
    private String  area;     		//所在区域
    private String  area_id;		//区域code
    private String  street;     	//所在街道
    private String  location;     	//所在位置
    private Integer enabled;		//1:启用， 2:停用
	private String off_line;		//文件同步状态
	private String  memory;			//剩余内存
	private String version;		//版本信息
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date    sync_time;	//同步时间
	@JSONField(format="yyyy-MM-dd")
	private Date    create_time;	//创建时间
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date    update_time;	//更新时间
    @Transient
    private String  org_name;       //机构名称
    @Transient
    private String  user_name;      //用户名称
    @Transient
    private String  address;      	//地址（省市区）
	@Transient
	private String conf_name;       //配置名称
	@Transient
    private Integer status;		////是否离线，1：是，2：否
	@Transient
	private Long conf_id;		//配置ID

    private Integer is_sync;    //是否开启同步（1：是， 2：否）

	public Long getConf_id() {
		return conf_id;
	}

	public void setConf_id(Long conf_id) {
		this.conf_id = conf_id;
	}

	public Long getDevice_id() {
		return device_id;
	}
	public void setDevice_id(Long device_id) {
		this.device_id = device_id;
	}
	public String getDevice_code() {
		return device_code;
	}
	public void setDevice_code(String device_code) {
		this.device_code = device_code;
	}
	public Long getOrg_id() {
		return org_id;
	}
	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getProvince_id() {
		return province_id;
	}
	public void setProvince_id(String province_id) {
		this.province_id = province_id;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCity_id() {
		return city_id;
	}
	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getArea_id() {
		return area_id;
	}
	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Integer getEnabled() {
		return enabled;
	}
	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public String getOff_line() {
		return off_line;
	}

	public void setOff_line(String off_line) {
		this.off_line = off_line;
	}

	public String getMemory() {
		return memory;
	}
	public void setMemory(String memory) {
		this.memory = memory;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public Date getSync_time() {
		return sync_time;
	}
	public void setSync_time(Date sync_time) {
		this.sync_time = sync_time;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public String getOrg_name() {
		return org_name;
	}
	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	public String getConf_name() {
		return conf_name;
	}

	public void setConf_name(String conf_name) {
		this.conf_name = conf_name;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

    public Integer getIs_sync() {
        return is_sync;
    }

    public void setIs_sync(Integer is_sync) {
        this.is_sync = is_sync;
    }
}
