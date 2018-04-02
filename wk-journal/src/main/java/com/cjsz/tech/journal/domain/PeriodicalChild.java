package com.cjsz.tech.journal.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 期刊详情
 * Created by Administrator on 2016/12/5 0005.
 */
@Entity
@Table(name = "periodical_child")
public class PeriodicalChild {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long child_id;    //转换的图片ID

    private Long periodical_id;    //所属期刊

    private Integer image_page;     //当前页数

    private String image_url;    //图片路径

    private String image_small_url;   //小图路径

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;    //创建时间

    public Integer getImage_page() {
        return image_page;
    }

    public void setImage_page(Integer image_page) {
        this.image_page = image_page;
    }

    public Long getChild_id() {
        return child_id;
    }

    public void setChild_id(Long child_id) {
        this.child_id = child_id;
    }

    public Long getPeriodical_id() {
        return periodical_id;
    }

    public void setPeriodical_id(Long periodical_id) {
        this.periodical_id = periodical_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_small_url() {
        return image_small_url;
    }

    public void setImage_small_url(String image_small_url) {
        this.image_small_url = image_small_url;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
