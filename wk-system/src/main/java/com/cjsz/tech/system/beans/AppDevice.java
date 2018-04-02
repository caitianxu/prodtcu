package com.cjsz.tech.system.beans;

import com.cjsz.tech.beans.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by yunke on 16/3/3.
 */
@Table(name = "app_device")
public class AppDevice   extends BaseEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long org_id;
    
    private Long uid;

    private Integer dev_type;

    private String dev_name;

    private String dev_sn;

    private String province;

    private String city;

    private String area;

    private String address;

    private String dev_address;

    private Date create_time;

    private Integer enabled;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Integer getDev_type() {
        return dev_type;
    }

    public void setDev_type(Integer dev_type) {
        this.dev_type = dev_type;
    }

    public String getDev_name() {
        return dev_name;
    }

    public void setDev_name(String dev_name) {
        this.dev_name = dev_name;
    }

    public String getDev_sn() {
        return dev_sn;
    }

    public void setDev_sn(String dev_sn) {
        this.dev_sn = dev_sn;
    }

    public String getDev_address() {
        return dev_address;
    }

    public void setDev_address(String dev_address) {
        this.dev_address = dev_address;
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

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}
    
    
}
