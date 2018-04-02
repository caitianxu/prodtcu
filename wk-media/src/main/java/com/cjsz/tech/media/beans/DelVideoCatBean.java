package com.cjsz.tech.media.beans;

import java.io.Serializable;

/**
 * Created by Li Yi on 2016/12/6.
 */
public class DelVideoCatBean implements Serializable {

    private Long[] video_cat_ids;//视频分类Id数组

    private String mark;//删除信息，用于二次确认

    public Long[] getVideo_cat_ids() {
        return video_cat_ids;
    }

    public void setVideo_cat_ids(Long[] video_cat_ids) {
        this.video_cat_ids = video_cat_ids;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
