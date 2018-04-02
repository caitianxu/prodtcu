package com.cjsz.tech.journal.beans;

import com.alibaba.fastjson.annotation.JSONField;
import com.cjsz.tech.beans.PageConditionBean;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:Jason
 * Date:2016/12/6
 */
public class NewspaperBean extends PageConditionBean implements Serializable {
    private Long newspaper_id;
    @JSONField(format = "yyyy-MM-dd")
    private Date update_time; //最后修改日期
    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;//创建时间

    private Long order_weight;//排序
    private Long org_id;//机构ID
    private Long org_newspaper_rel_id; //机构-报纸 关系ID
    private Integer source_type;//来源类型(1:系统分配;2:自主创建)
    private Long click_num; //报纸点击量
    private Long newspaper_cat_id;
    private String paper_name;
    private String paper_img;
    private String paper_img_small;
    private String paper_url;
    private String remark;
    private Integer is_recommend;//推荐 1： 是， 2： 否

    public Long getNewspaper_id() {
        return newspaper_id;
    }

    public void setNewspaper_id(Long newspaper_id) {
        this.newspaper_id = newspaper_id;
    }

    public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
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

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getOrg_newspaper_rel_id() {
        return org_newspaper_rel_id;
    }

    public void setOrg_newspaper_rel_id(Long org_newspaper_rel_id) {
        this.org_newspaper_rel_id = org_newspaper_rel_id;
    }

    public Integer getSource_type() {
        return source_type;
    }

    public void setSource_type(Integer source_type) {
        this.source_type = source_type;
    }

    public Long getClick_num() {
        return click_num;
    }

    public void setClick_num(Long click_num) {
        this.click_num = click_num;
    }

    public Long getNewspaper_cat_id() {
        return newspaper_cat_id;
    }

    public void setNewspaper_cat_id(Long newspaper_cat_id) {
        this.newspaper_cat_id = newspaper_cat_id;
    }

    public String getPaper_name() {
        return paper_name;
    }

    public void setPaper_name(String paper_name) {
        this.paper_name = paper_name;
    }

    public String getPaper_img() {
        return paper_img;
    }

    public void setPaper_img(String paper_img) {
        this.paper_img = paper_img;
    }

    public String getPaper_img_small() {
        return paper_img_small;
    }

    public void setPaper_img_small(String paper_img_small) {
        this.paper_img_small = paper_img_small;
    }

    public String getPaper_url() {
        return paper_url;
    }

    public void setPaper_url(String paper_url) {
        this.paper_url = paper_url;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

	public Integer getIs_recommend() {
		return is_recommend;
	}

	public void setIs_recommend(Integer is_recommend) {
		this.is_recommend = is_recommend;
	}
}
