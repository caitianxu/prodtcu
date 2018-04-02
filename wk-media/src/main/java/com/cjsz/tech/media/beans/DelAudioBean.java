package com.cjsz.tech.media.beans;

import java.io.Serializable;

/**
 * Created by Li Yi on 2016/12/7.
 */
public class DelAudioBean implements Serializable {

    private Long[] audio_ids;

    private Long audio_cat_id;

    public Long getAudio_cat_id() {
        return audio_cat_id;
    }

    public void setAudio_cat_id(Long audio_cat_id) {
        this.audio_cat_id = audio_cat_id;
    }

    public Long[] getAudio_ids() {
        return audio_ids;
    }

    public void setAudio_ids(Long[] audio_ids) {
        this.audio_ids = audio_ids;
    }
}
