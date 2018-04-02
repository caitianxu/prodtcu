package com.cjsz.tech.dev.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Li Yi on 2016/12/23.
 */
public class DelConfigBean implements Serializable {

    private List<Long> confs;

    public List<Long> getConfs() {
        return confs;
    }

    public void setConfs(List<Long> confs) {
        this.confs = confs;
    }
}
