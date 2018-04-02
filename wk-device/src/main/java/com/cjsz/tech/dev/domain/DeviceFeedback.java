package com.cjsz.tech.dev.domain;

import javax.persistence.*;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户反馈
 * Created by Administrator on 2016/10/25.
 */
@Entity
@Table(name = "device_feedback")
public class DeviceFeedback implements Serializable {
	
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    device_feedback_id; //意见反馈id
	private Long    org_id;      		//机构id
	private Long 	dept_id;			//组织id
    private Long    user_id;			//用户Id
	private String  user_name;      	//用户名称
	private Integer  user_type;      	//1:管理员用户;2:会员用户
    @JSONField(format="yyyy-MM-dd")
    private Date    send_time;			//申请时间
    private String  opinion;       		//意见内容
    private String  reply;       		//回复内容
    private Integer reply_status; 		//回复状态，1：未回复， 2已回复
    @JSONField(format="yyyy-MM-dd")
    private Date    reply_time;			//回复时间
    private String  device_type_code;        //设备类型编码(终端)

	@Transient
	private String  org_name;       	//机构名称

	@Transient
	private String dept_name;		//组织名称

	public Long getDevice_feedback_id() {
		return device_feedback_id;
	}

	public void setDevice_feedback_id(Long device_feedback_id) {
		this.device_feedback_id = device_feedback_id;
	}

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public String getOrg_name() {
		return org_name;
	}

	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}

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

	public Integer getUser_type() {
		return user_type;
	}

	public void setUser_type(Integer user_type) {
		this.user_type = user_type;
	}

	public Date getSend_time() {
		return send_time;
	}

	public void setSend_time(Date send_time) {
		this.send_time = send_time;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public Integer getReply_status() {
		return reply_status;
	}

	public void setReply_status(Integer reply_status) {
		this.reply_status = reply_status;
	}

	public Date getReply_time() {
		return reply_time;
	}

	public void setReply_time(Date reply_time) {
		this.reply_time = reply_time;
	}

	public String getDevice_type_code() {
		return device_type_code;
	}

	public void setDevice_type_code(String device_type_code) {
		this.device_type_code = device_type_code;
	}

	public Long getDept_id() {
		return dept_id;
	}

	public void setDept_id(Long dept_id) {
		this.dept_id = dept_id;
	}

	public String getDept_name() {
		return dept_name;
	}

	public void setDept_name(String dept_name) {
		this.dept_name = dept_name;
	}
}
