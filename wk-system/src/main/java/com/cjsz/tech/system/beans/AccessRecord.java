package com.cjsz.tech.system.beans;

import com.cjsz.tech.beans.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "access_log")
public class AccessRecord   extends BaseEntity implements Serializable {

	
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    
    private String access_ip;
    
    private String access_url;
    
    private Long org_id;
    
    private Long dev_id;
    
    private Long uid;
    
    private Integer biz_type;
    
    private Long biz_id;
    
    private Integer biz_act;
    
    private Date access_time;
    
    private Integer stay_time;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccess_ip() {
		return access_ip;
	}

	public void setAccess_ip(String access_ip) {
		this.access_ip = access_ip;
	}

	public String getAccess_url() {
		return access_url;
	}

	public void setAccess_url(String access_url) {
		this.access_url = access_url;
	}

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public Long getDev_id() {
		return dev_id;
	}

	public void setDev_id(Long dev_id) {
		this.dev_id = dev_id;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Integer getBiz_type() {
		return biz_type;
	}

	public void setBiz_type(Integer biz_type) {
		this.biz_type = biz_type;
	}

	public Long getBiz_id() {
		return biz_id;
	}

	public void setBiz_id(Long biz_id) {
		this.biz_id = biz_id;
	}

	public Integer getBiz_act() {
		return biz_act;
	}

	public void setBiz_act(Integer biz_act) {
		this.biz_act = biz_act;
	}

	public Date getAccess_time() {
		return access_time;
	}

	public void setAccess_time(Date access_time) {
		this.access_time = access_time;
	}

	public Integer getStay_time() {
		return stay_time;
	}

	public void setStay_time(Integer stay_time) {
		this.stay_time = stay_time;
	}
    
    
}
