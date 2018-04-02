package com.cjsz.tech.system.domain;


import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/20 0020.
 */
@Entity
@Table(name = "adv_org_rel")
public class AdvOrgRel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rel_id;
    private Long org_id;
    private Long adv_id;
    private Long order_weight;
    private Integer is_show;
    private Date update_time;
    private Date create_time;
    private Integer is_delete;

    public AdvOrgRel(){

    }

    public AdvOrgRel(Long org_id, Long adv_id, Long order_weight, Integer is_show,  Date create_time, Date update_time, Integer is_delete) {
        this.org_id = org_id;
        this.adv_id = adv_id;
        this.order_weight = order_weight;
        this.is_show = is_show;
        this.create_time = create_time;
        this.update_time = update_time;
        this.is_delete = is_delete;
    }
    public Long getRel_id() {
        return rel_id;
    }

    public void setRel_id(Long rel_id) {
        this.rel_id = rel_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getAdv_id() {
        return adv_id;
    }

    public void setAdv_id(Long adv_id) {
        this.adv_id = adv_id;
    }

    public Long getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(Long order_weight) {
        this.order_weight = order_weight;
    }

    public Integer getIs_show() {
        return is_show;
    }

    public void setIs_show(Integer is_show) {
        this.is_show = is_show;
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

    public Integer getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Integer is_delete) {
        this.is_delete = is_delete;
    }
}
