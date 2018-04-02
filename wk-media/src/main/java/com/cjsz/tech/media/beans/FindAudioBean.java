package com.cjsz.tech.media.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by Li Yi on 2016/12/7.
 */
public class FindAudioBean extends PageConditionBean {

    private Long org_id;//机构ID

    private Long audio_cat_id;//分类ID

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getAudio_cat_id() {
        return audio_cat_id;
    }

    public void setAudio_cat_id(Long audio_cat_id) {
        this.audio_cat_id = audio_cat_id;
    }
}
