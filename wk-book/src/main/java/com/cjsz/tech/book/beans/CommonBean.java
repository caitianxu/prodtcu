package com.cjsz.tech.book.beans;

import java.util.List;

/**
 * 一些图书公用的传递参数实体类
 * Created by Administrator on 2016/12/22 0022.
 */
public class CommonBean {

    private Boolean bool;

    private Integer is_pkg;     //是否数据包数据（1:是;2:否）

    private Long pkg_id;        //数据包Id

    private List<Long> ids;

    private Long device_id;     //设备Id

    public Boolean getBool() {
        return bool;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

    public Integer getIs_pkg() {
        return is_pkg;
    }

    public void setIs_pkg(Integer is_pkg) {
        this.is_pkg = is_pkg;
    }

    public Long getPkg_id() {
        return pkg_id;
    }

    public void setPkg_id(Long pkg_id) {
        this.pkg_id = pkg_id;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Long device_id) {
        this.device_id = device_id;
    }
}
