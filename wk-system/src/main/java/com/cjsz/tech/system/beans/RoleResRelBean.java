package com.cjsz.tech.system.beans;

import com.alibaba.fastjson.annotation.JSONField;
import com.cjsz.tech.beans.PageConditionBean;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

/**
 * 权限资源实体类--辅助返回结果结构
 * Created by Administrator on 2016/11/12 0012.
 */
public class RoleResRelBean  extends PageConditionBean {

    private Long role_res_rel_id;//唯一编码

    private Long role_id; //角色编码

    private Long res_id;//资源编码

    private Long pid;//父编码

    private String res_name;//资源名称

    private String full_path;//资源路径

    private Integer data_type_id;//数据权限

    private Integer res_type;//资源类型

    private Integer enabled;//

    private Long order_weight;

    private Integer path_length;//full_path的|数量

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;//创建时间
    
    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;//创建时间

    private List<RoleResRelBean> children;//权限资源树子节点

    public Long getRole_res_rel_id() {
        return role_res_rel_id;
    }

    public void setRole_res_rel_id(Long role_res_rel_id) {
        this.role_res_rel_id = role_res_rel_id;
    }

    public Long getRole_id() {
        return role_id;
    }

    public void setRole_id(Long role_id) {
        this.role_id = role_id;
    }

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

    public String getRes_name() {
        return res_name;
    }

    public void setRes_name(String res_name) {
        this.res_name = res_name;
    }

    public String getFull_path() {
        return full_path;
    }

    public void setFull_path(String full_path) {
        this.full_path = full_path;
    }

    public Integer getData_type_id() {
        return data_type_id;
    }

    public void setData_type_id(Integer data_type_id) {
        this.data_type_id = data_type_id;
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

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public List<RoleResRelBean> getChildren() {
        return children;
    }

    public void setChildren(List<RoleResRelBean> children) {
        this.children = children;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Integer getPath_length() {
        return path_length;
    }

    public void setPath_length(Integer path_length) {
        this.path_length = path_length;
    }
}
