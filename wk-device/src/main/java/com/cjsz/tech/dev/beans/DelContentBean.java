package com.cjsz.tech.dev.beans;

import java.io.Serializable;

/**
 * Created by Li Yi on 2016/12/23.
 */
public class DelContentBean implements Serializable {

    private Long[] content_ids;

    public Long[] getContent_ids() {
        return content_ids;
    }

    public void setContent_ids(Long[] content_ids) {
        this.content_ids = content_ids;
    }
}
