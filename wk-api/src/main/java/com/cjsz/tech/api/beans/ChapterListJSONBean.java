package com.cjsz.tech.api.beans;

import java.util.Date;

/**
 * Created by Administrator on 2017/10/19 0019.
 */
public class ChapterListJSONBean {

    private String ch_id;//章节id
    private String ch_name;
    private String ch_vip;
    private Integer ch_sale;
    private Integer ch_index;
    private Integer is_buyed;
    private String ch_update;

    public String getCh_id() {
        return ch_id;
    }

    public void setCh_id(String ch_id) {
        this.ch_id = ch_id;
    }

    public String getCh_name() {
        return ch_name;
    }

    public void setCh_name(String ch_name) {
        this.ch_name = ch_name;
    }

    public String getCh_vip() {
        return ch_vip;
    }

    public void setCh_vip(String ch_vip) {
        this.ch_vip = ch_vip;
    }

    public Integer getCh_sale() {
        return ch_sale;
    }

    public void setCh_sale(Integer ch_sale) {
        this.ch_sale = ch_sale;
    }

    public Integer getCh_index() {
        return ch_index;
    }

    public void setCh_index(Integer ch_index) {
        this.ch_index = ch_index;
    }

    public Integer getIs_buyed() {
        return is_buyed;
    }

    public void setIs_buyed(Integer is_buyed) {
        this.is_buyed = is_buyed;
    }

    public String getCh_update() {
        return ch_update;
    }

    public void setCh_update(String ch_update) {
        this.ch_update = ch_update;
    }
}
