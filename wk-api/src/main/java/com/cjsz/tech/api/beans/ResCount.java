package com.cjsz.tech.api.beans;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 资源点击统计
 * Created by Administrator on 2017/2/4 0004.
 */
@Entity
@Table(name = "res_count")
public class ResCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long res_count_id;      //同步统计Id

    private Long org_id;     //机构Id

    private Long user_id;        //用户Id

    private Long device_id;      //设备Id

    private Integer device_type;        //设备类型(1:大屏;2:IOS;3:Android)

    private Integer res_type;       //资源类型(1:图书;2:报纸;3:期刊;4:新闻;5:视频;6:音频;7:外链;8:广告)

    private String res_content;        //资源内容

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;        //访问时间

    public Long getRes_count_id() {
        return res_count_id;
    }

    public void setRes_count_id(Long res_count_id) {
        this.res_count_id = res_count_id;
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

    public Integer getRes_type() {
        return res_type;
    }

    public void setRes_type(Integer res_type) {
        this.res_type = res_type;
    }

    public String getRes_content() {
        return res_content;
    }

    public void setRes_content(String res_content) {
        this.res_content = res_content;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
