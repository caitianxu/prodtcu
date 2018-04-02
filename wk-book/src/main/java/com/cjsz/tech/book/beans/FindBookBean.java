package com.cjsz.tech.book.beans;

import com.cjsz.tech.beans.PageConditionBean;


/**
 *  查找图书分类——条件
 * Created by Administrator on 2016/10/25.
 */
public class FindBookBean extends PageConditionBean {

	private Long org_id;//机构ID

	private Long book_cat_id;//分类ID

	private Long pkg_id;//数据包ID

	private Long device_id;//设备ID

	private Integer is_hot;//是否热门(1:是;2:否)

	private Integer is_recommend;//是否推荐(1:是;2:否)

	private Integer offline_status;//0:不离线;1:发送离线

	private Integer enabled;//是否启用(1:是;2:否)

	private Integer status;//0:未离线;1:离线中;2:已离线;3:取消离线

	private String tag_name;//标签名

	private String order_type;//排序规则

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public Long getBook_cat_id() {
		return book_cat_id;
	}

	public void setBook_cat_id(Long book_cat_id) {
		this.book_cat_id = book_cat_id;
	}

	public Long getPkg_id() {
		return pkg_id;
	}

	public void setPkg_id(Long pkg_id) {
		this.pkg_id = pkg_id;
	}

	public Long getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Long device_id) {
		this.device_id = device_id;
	}

	public Integer getIs_hot() {
		return is_hot;
	}

	public void setIs_hot(Integer is_hot) {
		this.is_hot = is_hot;
	}

	public Integer getIs_recommend() {
		return is_recommend;
	}

	public void setIs_recommend(Integer is_recommend) {
		this.is_recommend = is_recommend;
	}

	public Integer getOffline_status() {
		return offline_status;
	}

	public void setOffline_status(Integer offline_status) {
		this.offline_status = offline_status;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getTag_name() {
		return tag_name;
	}

	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
	}

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}
}
