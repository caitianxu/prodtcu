package com.cjsz.tech.cms.beans;

import java.io.Serializable;

/**
 * 删除活动
 * Created by Administrator on 2016/11/21 0021.
 */
public class DelActivityBean implements Serializable {

    private Long[]  activity_ids;    //活动Ids

    public Long[] getActivity_ids() {
        return activity_ids;
    }

    public void setActivity_ids(Long[] activity_ids) {
        this.activity_ids = activity_ids;
    }
}
