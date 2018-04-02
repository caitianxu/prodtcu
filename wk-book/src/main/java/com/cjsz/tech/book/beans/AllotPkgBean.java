package com.cjsz.tech.book.beans;

import com.cjsz.tech.system.domain.Organization;

import java.util.List;

/**
 * 数据包分配给机构
 * Created by Administrator on 2016/12/19 0019.
 */
public class AllotPkgBean {

    private Long pkg_id;        //数据包ID

    private List<Organization> orgs;     //分配的机构

    public Long getPkg_id() {
        return pkg_id;
    }

    public void setPkg_id(Long pkg_id) {
        this.pkg_id = pkg_id;
    }

    public List<Organization> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<Organization> orgs) {
        this.orgs = orgs;
    }
}
