package com.cjsz.tech.media.beans;

import com.cjsz.tech.beans.PageConditionBean;

/**
 * Created by LuoLi on 2017/4/25 0025.
 */
public class FindCatOrgBean extends PageConditionBean {

    private Long video_cat_id;

    private Long audio_cat_id;

    private Long project_id;

    private String project_code;

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

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
    }
}
