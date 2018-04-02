package com.cjsz.tech.dev.beans;

import com.alibaba.fastjson.annotation.JSONField;
import com.cjsz.tech.dev.domain.ConfContent;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * Created by Li Yi on 2016/12/22.
 */
public class FindDeviceSettingBean {

    private Long conf_id;//设备配置id

    private String conf_name;//配置名称

    private int content_num;//导航数量

    private int device_num;//使用本配置的设备数量

    private String logo_url;   //LOGO地址

    private String home_url;	//首页背景图地址

    private String list_url;   //列表背景图地址

    private String detail_url;	//详情背景图地址

    private String screensaver_url;	//屏保地址

    private Integer screensaver_time; //屏保时间

    @Transient
    private List<ConfContent> tabList;

    @Transient
    private List<SimpleDeviceBean> device_ids;

    private Integer is_default;//是否默认 1:是 ， 2：否

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date create_time;//创建时间

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date update_time;//更新时间

    public Long getConf_id() {
        return conf_id;
    }

    public void setConf_id(Long conf_id) {
        this.conf_id = conf_id;
    }

    public String getConf_name() {
        return conf_name;
    }

    public void setConf_name(String conf_name) {
        this.conf_name = conf_name;
    }

    public int getContent_num() {
        return content_num;
    }

    public void setContent_num(int content_num) {
        this.content_num = content_num;
    }

    public int getDevice_num() {
        return device_num;
    }

    public void setDevice_num(int device_num) {
        this.device_num = device_num;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getHome_url() {
        return home_url;
    }

    public void setHome_url(String home_url) {
        this.home_url = home_url;
    }

    public String getList_url() {
        return list_url;
    }

    public void setList_url(String list_url) {
        this.list_url = list_url;
    }

    public String getDetail_url() {
        return detail_url;
    }

    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }

    public Integer getIs_default() {
        return is_default;
    }

    public void setIs_default(Integer is_default) {
        this.is_default = is_default;
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

    public List<ConfContent> getTabList() {
        return tabList;
    }

    public void setTabList(List<ConfContent> tabList) {
        this.tabList = tabList;
    }

    public List<SimpleDeviceBean> getDevice_ids() {
        return device_ids;
    }

    public void setDevice_ids(List<SimpleDeviceBean> device_ids) {

        this.device_ids = device_ids;
    }

    public String getScreensaver_url() {
        return screensaver_url;
    }

    public void setScreensaver_url(String screensaver_url) {
        this.screensaver_url = screensaver_url;
    }

    public Integer getScreensaver_time() {
        return screensaver_time;
    }

    public void setScreensaver_time(Integer screensaver_time) {
        this.screensaver_time = screensaver_time;
    }
}
