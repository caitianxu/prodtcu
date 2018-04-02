package com.cjsz.tech.media.service;

import com.cjsz.tech.media.domain.AudioCatOrgRel;

/**
 * Created by LuoLi on 2017/4/21 0021.
 */
public interface AudioCatOrgRelService {

    AudioCatOrgRel selectCatOrgRelByOrgIdAndCatId(Long org_id, Long audio_cat_id);

    void saveCatOrgRel(Long org_id, Long audio_cat_id);

    void updateCatOrgRel(AudioCatOrgRel rel);
}
