package com.cjsz.tech.media.beans;

import java.io.Serializable;

/**
 * Created by Li Yi on 2016/12/7.
 */
public class DelAudioCatBean implements Serializable {

    private Long[] audio_cat_ids;//音频分类Id数组

    private String mark;//删除信息，用于二次确认

    public Long[] getAudio_cat_ids() {
        return audio_cat_ids;
    }

    public void setAudio_cat_ids(Long[] audio_cat_ids) {
        this.audio_cat_ids = audio_cat_ids;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
