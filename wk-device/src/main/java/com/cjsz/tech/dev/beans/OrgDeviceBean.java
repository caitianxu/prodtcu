package com.cjsz.tech.dev.beans;

import java.util.List;

import com.cjsz.tech.dev.domain.Device;

/**
 * Created by Li Yi on 2016/12/21.
 */
public class OrgDeviceBean {

    private Long org_id;//机构ID

    private String org_name;//机构名称

    private Long version_id;//版本名称

    private List<Device> orgDevices;//机构包含的设备列表

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public Long getVersion_id() {
        return version_id;
    }

    public void setVersion_id(Long version_id) {
        this.version_id = version_id;
    }

    public List<Device> getOrgDevices() {
        return orgDevices;
    }

    public void setOrgDevices(List<Device> orgDevices) {
        this.orgDevices = orgDevices;
    }
}
