package com.cjsz.tech.system.domain;

import com.cjsz.tech.beans.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**   
 * @Title: SysLog
 * @Description: 异常日志查询日志
 * @author Bruce
 * @date 2016-11-09 13:26:25
 * @version V1.0   
 *
 */
@Entity
@Table(name = "sys_log", schema = "")
@SuppressWarnings("serial")
public class SysLog extends BaseEntity implements java.io.Serializable {
	/**sys_log_id*/
	private Long sys_log_id;
	/**异常编码*/
	private Long sys_log_code;
	/**异常原因*/
	private String sys_log_content;
	/**操作时间*/
	private Date create_time;
	/**机构ID*/
	private Long org_id;
	
	/**
	 *方法: 取得java.lang.Long
	 *@return: java.lang.Long  sys_log_id
	 */
	@Column(name ="SYS_LOG_ID",nullable=false,precision=19,scale=0)
	public Long getSys_log_id(){
		return this.sys_log_id;
	}

	/**
	 *方法: 设置java.lang.Long
	 *@param: java.lang.Long  sys_log_id
	 */
	public void setSys_log_id(Long sys_log_id){
		this.sys_log_id = sys_log_id;
	}
	/**
	 *方法: 取得java.lang.Long
	 *@return: java.lang.Long  异常编码
	 */
	@Column(name ="SYS_LOG_CODE",nullable=true,precision=10,scale=0)
	public Long getSys_log_code(){
		return this.sys_log_code;
	}

	/**
	 *方法: 设置java.lang.Long
	 *@param: java.lang.Long  异常编码
	 */
	public void setSys_log_code(Long sys_log_code){
		this.sys_log_code = sys_log_code;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  异常原因
	 */
	@Column(name ="SYS_LOG_CONTENT",nullable=true,length=65535)
	public String getSys_log_content(){
		return this.sys_log_content;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  异常原因
	 */
	public void setSys_log_content(String sys_log_content){
		this.sys_log_content = sys_log_content;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  操作时间
	 */
	@Column(name ="CREATE_TIME",nullable=true)
	public Date getCreate_time(){
		return this.create_time;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  操作时间
	 */
	public void setCreate_time(Date create_time){
		this.create_time = create_time;
	}

	public Long getOrg_id() {
		return org_id;
	}

	@Column(name ="ORG_ID",nullable=true)
	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}
}
