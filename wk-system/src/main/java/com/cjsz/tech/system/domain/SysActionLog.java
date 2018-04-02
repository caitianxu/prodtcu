package com.cjsz.tech.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.cjsz.tech.beans.BaseEntity;
/**   
 * @Title: SysActionLog
 * @Description: 
 * @author Bruce
 * @date 2016-11-09 13:26:26
 * @version V1.0   
 *
 */
@Entity
@Table(name = "sys_action_log", schema = "")
@SuppressWarnings("serial")
public class SysActionLog extends BaseEntity  implements java.io.Serializable {
	/**action_log_id*/
	private Long action_log_id;
	/**增加 删除 更新 查询*/
	private String action_type;
	/**日志内容*/
	private Object action_log_content;
	/**action_log_module_name*/
	private String action_log_module_name;
	/**操作人*/
	private String action_user_name;
	/**操作时间*/
	private Date action_time;
	/**机构ID*/
	private Long org_id;
	/**操作IP*/
	private String action_ip;
	
	/**
	 *方法: 取得java.lang.Long
	 *@return: java.lang.Long  action_log_id
	 */
	@Column(name ="ACTION_LOG_ID",nullable=false,precision=19,scale=0)
	public Long getAction_log_id(){
		return this.action_log_id;
	}

	/**
	 *方法: 设置java.lang.Long
	 *@param: java.lang.Long  action_log_id
	 */
	public void setAction_log_id(Long action_log_id){
		this.action_log_id = action_log_id;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  增加 删除 更新 查询
	 */
	@Column(name ="ACTION_TYPE",nullable=true,length=20)
	public String getAction_type(){
		return this.action_type;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  增加 删除 更新 查询
	 */
	public void setAction_type(String action_type){
		this.action_type = action_type;
	}
	/**
	 *方法: 取得java.lang.Object
	 *@return: java.lang.Object  日志内容
	 */
	@Column(name ="ACTION_LOG_CONTENT",nullable=true,length=65535)
	public Object getAction_log_content(){
		return this.action_log_content;
	}

	/**
	 *方法: 设置java.lang.Object
	 *@param: java.lang.Object  日志内容
	 */
	public void setAction_log_content(Object action_log_content){
		this.action_log_content = action_log_content;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  action_log_module_name
	 */
	@Column(name ="ACTION_LOG_MODULE_NAME",nullable=true,length=50)
	public String getAction_log_module_name(){
		return this.action_log_module_name;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  action_log_module_name
	 */
	public void setAction_log_module_name(String action_log_module_name){
		this.action_log_module_name = action_log_module_name;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  操作人
	 */
	@Column(name ="ACTION_USER_NAME",nullable=true,length=50)
	public String getAction_user_name(){
		return this.action_user_name;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  操作人
	 */
	public void setAction_user_name(String action_user_name){
		this.action_user_name = action_user_name;
	}
	/**
	 *方法: 取得java.util.Date
	 *@return: java.util.Date  操作时间
	 */
	@Column(name ="ACTION_TIME",nullable=true)
	public Date getAction_time(){
		return this.action_time;
	}

	/**
	 *方法: 设置java.util.Date
	 *@param: java.util.Date  操作时间
	 */
	public void setAction_time(Date action_time){
		this.action_time = action_time;
	}

	/**
	 *方法: 取得java.lang.Long
	 *@param: java.lang.Long  机构ID
	 */
	@Column(name ="ORG_ID",nullable=true)
	public Long getOrg_id() {
		return org_id;
	}

	/**
	 *方法: 设置java.lang.Long
	 *@param: java.lang.Long  机构ID
	 */
	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  操作IP
	 */
	@Column(name ="ACTION_IP",nullable=true)
	public String getAction_ip() {
		return action_ip;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  操作IP
	 */
	public void setAction_ip(String action_ip) {
		this.action_ip = action_ip;
	}
}
