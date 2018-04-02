package com.cjsz.tech.media.beans;

/**
 * Created by LuoLi on 2017/4/24 0024.
 */
public class UpdateCatOrgBean {

    private Long video_cat_id;//分类Id

    private Long audio_cat_id;//分类Id

    private Long org_id;//机构Id

    private Long[] org_ids;

    public Long getVideo_cat_id() {
        return video_cat_id;
    }

    public void setVideo_cat_id(Long video_cat_id) {
        this.video_cat_id = video_cat_id;
    }

    public Long getAudio_cat_id() {
        return audio_cat_id;
    }

    public void setAudio_cat_id(Long audio_cat_id) {
        this.audio_cat_id = audio_cat_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long[] getOrg_ids() {
        return org_ids;
    }

    public void setOrg_ids(Long[] org_ids) {
        this.org_ids = org_ids;
    }
}
