package com.cjsz.tech.media.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * 查找视频——条件
 * Created by Li Yi on 2016/12/5.
 */
public class FindVideoBean extends PageConditionBean {

    private Long org_id;//机构ID

    private Long video_cat_id;//分类ID

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getVideo_cat_id() {
        return video_cat_id;
    }

    public void setVideo_cat_id(Long video_cat_id) {
        this.video_cat_id = video_cat_id;
    }
}
