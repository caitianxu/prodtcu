package com.cjsz.tech.dev.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * 配置内容
 * Created by Administrator on 2016/10/25.
 */
@Entity
@Table(name = "conf_content")
public class ConfContent implements Serializable {
	
	@Id
    private Long conf_content_id;	//配置内容id

    private Long conf_id;			//配置id

    private String content_name;	//内容名称

    private String content_icon;	// 图标

    private String content_type;	//类型1：自定义（填链接地址） 2：系统默认（不填链接地址）

    private String content_url;		//链接地址

    private Long order_weight;	//排序

	@JSONField(format = "yyyy-MM-dd")
	private Date create_time;       //创建时间

	@JSONField(format = "yyyy-MM-dd")
	private Date update_time;     //最近更新时间
    
	public Long getConf_content_id() {
		return conf_content_id;
	}

	public void setConf_content_id(Long conf_content_id) {
		this.conf_content_id = conf_content_id;
	}

	public Long getConf_id() {
		return conf_id;
	}

	public void setConf_id(Long conf_id) {
		this.conf_id = conf_id;
	}

	public String getContent_name() {
		return content_name;
	}

	public void setContent_name(String content_name) {
		this.content_name = content_name;
	}

	public String getContent_icon() {
		return content_icon;
	}

	public void setContent_icon(String content_icon) {
		this.content_icon = content_icon;
	}

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	public String getContent_url() {
		return content_url;
	}

	public void setContent_url(String content_url) {
		this.content_url = content_url;
	}

	public Long getOrder_weight() {
		return order_weight;
	}

	public void setOrder_weight(Long order_weight) {
		this.order_weight = order_weight;
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
}
