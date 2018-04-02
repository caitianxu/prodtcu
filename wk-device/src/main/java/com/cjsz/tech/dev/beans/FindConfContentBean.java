package com.cjsz.tech.dev.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by Li Yi on 2016/12/23.
 */
public class FindConfContentBean extends PageConditionBean {

    private Long conf_id;//配置ID

    public Long getConf_id() {
        return conf_id;
    }

    public void setConf_id(Long conf_id) {
        this.conf_id = conf_id;
    }
}
