package com.cjsz.tech.api.beans;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 页面点击统计
 * Created by Administrator on 2017/2/4 0004.
 */
@Entity
@Table(name = "page_click_count")
public class PageClickCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long page_click_count_id;        //页面点击统计Id

    private Long org_id;     //机构Id

    private Long user_id;        //用户Id

    private Long device_id;      //设备Id

    private Integer device_type;        //设备类型(1:大屏;2:IOS;3:Android)

    private String click_content;      //点击内容

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;        //点击时间

    public Long getPage_click_count_id() {
        return page_click_count_id;
    }

    public void setPage_click_count_id(Long page_click_count_id) {
        this.page_click_count_id = page_click_count_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Long device_id) {
        this.device_id = device_id;
    }

    public Integer getDevice_type() {
        return device_type;
    }

    public void setDevice_type(Integer device_type) {
        this.device_type = device_type;
    }

    public String getClick_content() {
        return click_content;
    }

    public void setClick_content(String click_content) {
        this.click_content = click_content;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
