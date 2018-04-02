package com.cjsz.tech.system.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.cjsz.tech.beans.BaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 资源实体类
 * Created by Administrator on 2016/11/8 0008.
 */
@Entity
@Table(name = "sys_resource")
public class SysResource extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long res_id;//资源编码

    private Long pid;//父编码

    private Long org_id;//组织编码

    private String full_path;//完整层次路径

    private Long order_weight;//排序

    private String res_name;//资源名称

    private String res_url;//资源URL

    private String res_key;//资源标识

    private String res_icon;//图标

    private Integer res_type;//资源类型(1: 菜单 2: 按钮)

    private Integer enabled;//是否启用(1: 启用 2: 停用)

    private Integer is_forbid;//是否禁止(1: 禁止 2: 不禁止)

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;//创建时间

    private Long source_id; //资源来源Id
    
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date update_time;//更新时间

    @Transient
    private List<SysResource> children; //资源子节点，方便做上下级关系模式

    public Long getRes_id() {
        return res_id;
    }

    public void setRes_id(Long res_id) {
        this.res_id = res_id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getFull_path() {
        return full_path;
    }

    public void setFull_path(String full_path) {
        this.full_path = full_path;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public String getRes_name() {
        return res_name;
    }

    public void setRes_name(String res_name) {
        this.res_name = res_name;
    }

    public String getRes_url() {
        return res_url;
    }

    public void setRes_url(String res_url) {
        this.res_url = res_url;
    }

    public String getRes_key() {
        return res_key;
    }

    public void setRes_key(String res_key) {
        this.res_key = res_key;
    }

    public String getRes_icon() {
        return res_icon;
    }

    public void setRes_icon(String res_icon) {
        this.res_icon = res_icon;
    }

    public Integer getRes_type() {
        return res_type;
    }

    public void setRes_type(Integer res_type) {
        this.res_type = res_type;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getIs_forbid() {
        return is_forbid;
    }

    public void setIs_forbid(Integer is_forbid) {
        this.is_forbid = is_forbid;
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

	public List<SysResource> getChildren() {
        return children;
    }

    public void setChildren(List<SysResource> children) {
        this.children = children;
    }

    public Long getSource_id() {
        return source_id;
    }

    public void setSource_id(Long source_id) {
        this.source_id = source_id;
    }
}
