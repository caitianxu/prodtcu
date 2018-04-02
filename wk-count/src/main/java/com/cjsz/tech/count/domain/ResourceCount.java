package com.cjsz.tech.count.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 资源访问统计
 *
 * Created by Li Yi on 2016/12/20.
 */
@Entity
@Table(name = "resource_count")
public class ResourceCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long res_count_id;//资源统计ID

    private String source;  //来源 (大屏 ， app等)

    private Long org_id;	//机构id
    
    private Integer device_type; //设备类型（1：大屏， 2：其他）

    private Long device_id; //设备id

    private Integer resource_type;//资源类型（1:图书，2：新闻， 3：视频， 4：期刊，5：报纸， 6：外链）

    private Long resource_id;//资源id
    
    private Integer user_type; //用户类型（1：系统用户，2：会员）
    
    private Long user_id; //用户id
    
    private Integer operation_type; //操作类型（1:点击，2:收藏，3:分享，4：评论）
    
    private Long count;// 数量

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;//创建时间

	public Long getRes_count_id() {
		return res_count_id;
	}

	public void setRes_count_id(Long res_count_id) {
		this.res_count_id = res_count_id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public Integer getDevice_type() {
		return device_type;
	}

	public void setDevice_type(Integer device_type) {
		this.device_type = device_type;
	}

	public Long getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Long device_id) {
		this.device_id = device_id;
	}

	public Integer getResource_type() {
		return resource_type;
	}

	public void setResource_type(Integer resource_type) {
		this.resource_type = resource_type;
	}

	public Long getResource_id() {
		return resource_id;
	}

	public void setResource_id(Long resource_id) {
		this.resource_id = resource_id;
	}

	public Integer getUser_type() {
		return user_type;
	}

	public void setUser_type(Integer user_type) {
		this.user_type = user_type;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Integer getOperation_type() {
		return operation_type;
	}

	public void setOperation_type(Integer operation_type) {
		this.operation_type = operation_type;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
}
