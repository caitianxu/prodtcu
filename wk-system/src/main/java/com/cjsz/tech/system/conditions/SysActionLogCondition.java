package com.cjsz.tech.system.conditions;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by Bruce on 2016/11/9.
 */
public class SysActionLogCondition extends PageConditionBean{
    private Long sys_log_id;
    /**异常编码*/
    private String action_type;
    /**异常原因*/
    private String action_log_content;

    private String action_user_name;

    private String action_log_module_name;

    private String begin_time;

    private String end_time;

    private Integer is_delete = 0;

    private Long org_id;

    private String action_ip;

    public Long getSys_log_id() {
        return sys_log_id;
    }

    public void setSys_log_id(Long sys_log_id) {
        this.sys_log_id = sys_log_id;
    }

    public String getAction_type() {
        return action_type;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type;
    }

    public String getAction_log_content() {
        return action_log_content;
    }

    public void setAction_log_content(String action_log_content) {
        this.action_log_content = action_log_content;
    }

    public String getAction_user_name() {
        return action_user_name;
    }

    public void setAction_user_name(String action_user_name) {
        this.action_user_name = action_user_name;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getAction_log_module_name() {
        return action_log_module_name;
    }

    public void setAction_log_module_name(String action_log_module_name) {
        this.action_log_module_name = action_log_module_name;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getAction_ip() {
        return action_ip;
    }

    public void setAction_ip(String action_ip) {
        this.action_ip = action_ip;
    }
}
