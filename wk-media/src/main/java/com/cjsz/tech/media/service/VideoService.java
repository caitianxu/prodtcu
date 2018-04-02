package com.cjsz.tech.media.service;

import com.cjsz.tech.media.beans.FindVideoBean;
import com.cjsz.tech.media.domain.Video;
import com.cjsz.tech.system.service.IOfflineService;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Li Yi on 2016/12/5.
 */
public interface VideoService extends IOfflineService {

    /**
     * 分页查询本机构的视频
     *
     * @param sort
     * @param bean
     * @return
     */
    Object pageQuery(Sort sort, FindVideoBean bean);

    /**
     * 分页查询本机构的视频__前端
     *
     * @param sort
     * @param bean
     * @return
     */
    Object pageSiteQuery(Sort sort, FindVideoBean bean);

    /**
     * 添加视频
     *
     * @param video
     */
    Video saveVideo(Video video);

    /**
     * 修改视频
     *
     * @param video
     */
    Video updateVideo(Video video);

    /**
     * 通过视频ID查找视频
     *
     * @param video_id
     * @return
     */
    Video selectById(Long video_id);

    /**
     * 通过组织ID查找视频
     *
     * @param org_id
     * @return
     */
    List<Video> selectByOrgId(Long org_id);

    /**
     * 通过分类IDs删除
     *
     * @param cat_ids
     */
    void deleteByCatIds(String cat_ids);

    /**
     * 通过分类IDs查找视频
     *
     * @param cat_ids
     * @return
     */
    List<Video> selectByCatIds(String cat_ids);

    /**
     * 通过IDs删除视频
     *
     * @param video_ids
     */
    void deleteByIds(String video_ids);

    /**
     * 查询视频的数量
     * @param org_id
     * @return
     */
    Integer getCountByOrgId(Long org_id);

    /**
     * 根据视频ID查找视频分类ID
     * @param video_id
     * @return
     */
    Long getVideoCatId(Long video_id);
}
