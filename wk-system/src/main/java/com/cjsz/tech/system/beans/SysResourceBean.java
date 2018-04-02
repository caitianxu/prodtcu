package com.cjsz.tech.system.beans;

import com.cjsz.tech.beans.BaseEntity;

/**
 * Created by Administrator on 2016/11/8 0008.
 */
public class SysResourceBean extends BaseEntity {

    private Long res_id;

    private Long pid;

    private Long order_weight;

    private String res_name;

    private String res_url;

    private String res_icon;

    private String res_type;

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

    public String getRes_icon() {
        return res_icon;
    }

    public void setRes_icon(String res_icon) {
        this.res_icon = res_icon;
    }

    public String getRes_type() {
        return res_type;
    }

    public void setRes_type(String res_type) {
        this.res_type = res_type;
    }
}
