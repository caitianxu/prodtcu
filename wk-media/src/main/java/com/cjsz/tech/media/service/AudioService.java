package com.cjsz.tech.media.service;

import com.cjsz.tech.media.beans.FindAudioBean;
import com.cjsz.tech.media.domain.Audio;
import com.cjsz.tech.system.service.IOfflineService;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Li Yi on 2016/12/7.
 */
public interface AudioService extends IOfflineService {

    /**
     * 分页查询本机构的音频
     *
     * @param sort
     * @param bean
     * @return
     */
    Object pageQuery(Sort sort, FindAudioBean bean);

    /**
     * 添加音频
     *
     * @param audio
     */
    Audio saveAudio(Audio audio);

    /**
     * 修改音频
     *
     * @param audio
     */
    Audio updateAudio(Audio audio);

    /**
     * 通过音频ID查找音频
     *
     * @param audio_id
     * @return
     */
    Audio selectById(Long audio_id);

    /**
     * 通过组织ID查找音频
     *
     * @param org_id
     * @return
     */
    List<Audio> selectByOrgId(Long org_id);

    /**
     * 通过分类IDs删除
     *
     * @param cat_ids
     */
    void deleteByCatIds(String cat_ids);

    /**
     * 通过分类IDs查找音频
     *
     * @param cat_ids
     * @return
     */
    List<Audio> selectByCatIds(String cat_ids);

    /**
     * 通过IDs删除音频
     *
     * @param audio_ids
     */
    void deleteByIds(String audio_ids);

    /**
     * 查询音频数量
     * @param org_id
     * @return
     */
    Integer getCountByOrgId(Long org_id);

    /**
     * 根据音频ID查询音频分类ID
     * @param audio_id
     * @return
     */
    Long getCatId(Long audio_id);

}
