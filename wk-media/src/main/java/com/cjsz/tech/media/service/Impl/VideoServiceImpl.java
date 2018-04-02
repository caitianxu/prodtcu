package com.cjsz.tech.media.service.Impl;

import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.media.beans.FindVideoBean;
import com.cjsz.tech.media.domain.Video;
import com.cjsz.tech.media.mapper.VideoCatMapper;
import com.cjsz.tech.media.mapper.VideoMapper;
import com.cjsz.tech.media.service.VideoService;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Li Yi on 2016/12/5.
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private VideoCatMapper videoCatMapper;

    @Override
    public Object pageQuery(Sort sort, FindVideoBean bean) {
        List<String> catPaths = videoCatMapper.getFullPathsByCatIds(bean.getVideo_cat_id().toString());
        String catPath = "";
        if (catPaths.size() > 0) {
            catPath = catPaths.get(0);
        }
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        List<Video> result = videoMapper.pageQuery(bean.getOrg_id(), catPath, bean.getSearchText());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    public Object pageSiteQuery(Sort sort, FindVideoBean bean) {
        List<String> catPaths = videoCatMapper.getFullPathsByCatIds(bean.getVideo_cat_id().toString());
        String catPath = "";
        if (catPaths.size() > 0) {
            catPath = catPaths.get(0);
        }
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        List<Video> result = videoMapper.pageSiteQuery(bean.getOrg_id(), catPath, bean.getSearchText());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "视频")
    @Transactional
    public Video saveVideo(Video video) {
        video.setCreate_time(new Date());
        video.setUpdate_time(new Date());
        videoMapper.insert(video);
        return video;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "视频")
    @Transactional
    public Video updateVideo(Video video) {
        Video org_video = videoMapper.selectById(video.getVideo_id());
        BeanUtils.copyProperties(video, org_video);
        video.setUpdate_time(new Date());
        videoMapper.updateByPrimaryKey(video);
        return video;
    }

    @Override
    public Video selectById(Long video_id) {
        return videoMapper.selectById(video_id);
    }

    @Override
    public List<Video> selectByOrgId(Long org_id) {
        return videoMapper.pageQuery(org_id, null, null);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "视频")
    @Transactional
    public void deleteByCatIds(String cat_ids) {
        videoMapper.deleteByCatIds(cat_ids);
    }

    @Override
    public List<Video> selectByCatIds(String cat_ids) {
        return videoMapper.selectByCatIds(cat_ids);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "视频")
    @Transactional
    public void deleteByIds(String video_ids) {
        videoMapper.deleteByVideoIds(video_ids);
    }

    @Override
    public Integer getCountByOrgId(Long org_id) {
        return videoMapper.getCountByOrgId(org_id);
    }

    @Override
    public List getOffLineNumList(Long org_id, String oldTimeStamp, Object... otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        return videoMapper.getOffLineNumList(org_id, oldTimeStamp, num, size);
    }

    @Override
    public Integer hasOffLine(Long org_id, String oldTimeStamp, Object... otherparam) {
        Integer checknum = videoMapper.checkOffLineNum(org_id, oldTimeStamp);
        if (checknum == null) {
            checknum = 0;
        }
        return checknum;
    }

    @Override
    public Long getVideoCatId(Long video_id) {
        return videoMapper.getVideoCatId(video_id);
    }
}
