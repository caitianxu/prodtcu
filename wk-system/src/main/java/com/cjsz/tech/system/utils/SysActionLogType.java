package com.cjsz.tech.system.utils;

/**
 * Created by Bruce on 2016/11/11.
 */
public enum SysActionLogType {

    ADD("增加"), DELETE("删除"),UPDATE("修改"), SELECT("查询"), DEPLOY("发布"), BACKUP("备份");

    private String action_type;

    private SysActionLogType(String action_type) {
        this.action_type = action_type;
    }
    public String getAction_type() {
        return action_type;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type;
    }
}
