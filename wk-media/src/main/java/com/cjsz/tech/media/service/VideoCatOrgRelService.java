package com.cjsz.tech.media.service;

import com.cjsz.tech.media.domain.VideoCatOrgRel;

/**
 * Created by LuoLi on 2017/4/21 0021.
 */
public interface VideoCatOrgRelService {

    VideoCatOrgRel selectCatOrgRelByOrgIdAndCatId(Long org_id, Long video_cat_id);

    void saveCatOrgRel(Long org_id, Long video_cat_id);

    void updateCatOrgRel(VideoCatOrgRel rel);
}
