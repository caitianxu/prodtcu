package com.cjsz.tech.system.beans;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/8 0008.
 */
public class DelHelpBean implements Serializable {

    private Long[] ids;

    public Long[] getIds() {
        return ids;
    }

    public void setIds(Long[] ids) {
        this.ids = ids;
    }
}
