package com.cjsz.tech.system.conditions;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by Bruce on 2016/11/9.
 */
public class SysLogCondition extends PageConditionBean{
    private Long sys_log_id;
    /**异常编码*/
    private Long sys_log_code;
    /**异常原因*/
    private String sys_log_content;

    private String begin_time;

    private String end_time;

    private Integer is_delete = 0;

    private Long org_id;

    public Long getSys_log_id() {
        return sys_log_id;
    }

    public void setSys_log_id(Long sys_log_id) {
        this.sys_log_id = sys_log_id;
    }

    public Long getSys_log_code() {
        return sys_log_code;
    }

    public void setSys_log_code(Long sys_log_code) {
        this.sys_log_code = sys_log_code;
    }

    public String getSys_log_content() {
        return sys_log_content;
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

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setSys_log_content(String sys_log_content) {
        this.sys_log_content = sys_log_content;
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
}
