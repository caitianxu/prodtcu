package com.cjsz.tech.media.beans;

import java.io.Serializable;

/**
 * Created by Li Yi on 2016/12/6.
 */
public class DelVideoBean implements Serializable {

    private Long[] video_ids;

    public Long[] getVideo_ids() {
        return video_ids;
    }

    public void setVideo_ids(Long[] video_ids) {
        this.video_ids = video_ids;
    }
}
