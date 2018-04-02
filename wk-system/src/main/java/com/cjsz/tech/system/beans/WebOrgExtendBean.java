package com.cjsz.tech.system.beans;

import com.alibaba.fastjson.annotation.JSONField;
import com.cjsz.tech.system.domain.WebConfig;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/8/1 0001.
 */
public class WebOrgExtendBean implements Serializable{
    private Long pro_org_extend_id;//项目机构拓展Id

    private String pro_code;//项目code

    private String pro_name;//项目名称

    private Long org_id;//机构Id

    private String org_name;//机构名称

    private String org_logo;//机构logo

    private String org_logo_small;//机构logo压缩

    private String org_weixin;//机构logo

    private String org_weixin_small;//机构logo压缩

    private String server_name;//域名

    private Integer enabled;

    @JSONField(format = "yyyy-MM-dd")
    private Date create_time;//创建时间

    @JSONField(format = "yyyy-MM-dd")
    private Date update_time;//修改时间

    private Integer is_registe;

    private List<WebConfig> webConfigList;


    public Integer getIs_registe() {
        return is_registe;
    }

    public void setIs_registe(Integer is_registe) {
        this.is_registe = is_registe;
    }

    public Long getPro_org_extend_id() {
        return pro_org_extend_id;
    }

    public void setPro_org_extend_id(Long pro_org_extend_id) {
        this.pro_org_extend_id = pro_org_extend_id;
    }

    public String getPro_code() {
        return pro_code;
    }

    public void setPro_code(String pro_code) {
        this.pro_code = pro_code;
    }

    public String getPro_name() {
        return pro_name;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

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

    public String getOrg_logo() {
        return org_logo;
    }

    public void setOrg_logo(String org_logo) {
        this.org_logo = org_logo;
    }

    public String getOrg_logo_small() {
        return org_logo_small;
    }

    public void setOrg_logo_small(String org_logo_small) {
        this.org_logo_small = org_logo_small;
    }

    public String getOrg_weixin() {
        return org_weixin;
    }

    public void setOrg_weixin(String org_weixin) {
        this.org_weixin = org_weixin;
    }

    public String getOrg_weixin_small() {
        return org_weixin_small;
    }

    public void setOrg_weixin_small(String org_weixin_small) {
        this.org_weixin_small = org_weixin_small;
    }

    public String getServer_name() {
        return server_name;
    }

    public void setServer_name(String server_name) {
        this.server_name = server_name;
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

    public List<WebConfig> getWebConfigList() {
        return webConfigList;
    }

    public void setWebConfigList(List<WebConfig> webConfigList) {
        this.webConfigList = webConfigList;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
}
