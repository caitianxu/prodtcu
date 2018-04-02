package com.cjsz.tech.system.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.cjsz.tech.beans.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * 导航实体类
 * Created by Administrator on 2016/11/30 0030.
 */
@Entity
@Table(name = "app_nav")
public class AppNav extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nav_id;    //大屏导航ID

    private Long org_id;    //机构ID

    private Integer sys_type;   //导航分类(0:自定义;1:其他...)

    private String nav_name;    //导航名称

    private String nav_img; //导航图标

    private String nav_url; //链接地址

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;   //创建时间

    private Long order_weight;  //排序

    private Integer enabled;    //是否显示(1:显示;2:不显示)

    public Long getNav_id() {
        return nav_id;
    }

    public void setNav_id(Long nav_id) {
        this.nav_id = nav_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Integer getSys_type() {
        return sys_type;
    }

    public void setSys_type(Integer sys_type) {
        this.sys_type = sys_type;
    }

    public String getNav_name() {
        return nav_name;
    }

    public void setNav_name(String nav_name) {
        this.nav_name = nav_name;
    }

    public String getNav_img() {
        return nav_img;
    }

    public void setNav_img(String nav_img) {
        this.nav_img = nav_img;
    }

    public String getNav_url() {
        return nav_url;
    }

    public void setNav_url(String nav_url) {
        this.nav_url = nav_url;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
}
