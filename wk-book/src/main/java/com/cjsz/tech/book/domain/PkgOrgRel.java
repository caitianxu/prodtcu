package com.cjsz.tech.book.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

/**
 * 数据包组织关系
 * Created by Administrator on 2016/12/21 0021.
 */
@Entity
@Table(name = "pkg_org_rel")
public class PkgOrgRel {

    private Long org_id;    //机构ID

    private Long pkg_id;    //数据包ID

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;   //创建时间

    public PkgOrgRel(){}

    public PkgOrgRel(Long org_id, Long pkg_id, Date create_time){
        this.org_id = org_id;
        this.pkg_id = pkg_id;
        this.create_time = create_time;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getPkg_id() {
        return pkg_id;
    }

    public void setPkg_id(Long pkg_id) {
        this.pkg_id = pkg_id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
