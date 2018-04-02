package com.cjsz.tech.media.service.Impl;

import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.media.beans.FindAudioBean;
import com.cjsz.tech.media.domain.Audio;
import com.cjsz.tech.media.mapper.AudioCatMapper;
import com.cjsz.tech.media.mapper.AudioMapper;
import com.cjsz.tech.media.service.AudioService;
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
 * Created by Li Yi on 2016/12/7.
 */
@Service
public class AudioServiceImpl implements AudioService {

    @Autowired
    private AudioMapper audioMapper;

    @Autowired
    private AudioCatMapper audioCatMapper;

    @Override
    public Object pageQuery(Sort sort, FindAudioBean bean) {
        List<String> catPaths = audioCatMapper.getFullPathsByCatIds(bean.getAudio_cat_id().toString());
        String catPath = "";
        if(catPaths.size()>0){
            catPath = catPaths.get(0);
        }
        //分页的另外一种用法,紧随其后的第一个查询将使用分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if (order != null) {
            PageHelper.orderBy(order);
        }
        //组装分页列表对象,并讲列表对象转换为dto对象传输到前台
        List<Audio> result = audioMapper.pageQuery(bean.getOrg_id(), catPath, bean.getSearchText());
        PageList pageList = new PageList(result, null);
        return pageList;
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "音频")
    @Transactional
    public Audio saveAudio(Audio audio) {
        audio.setCreate_time(new Date());
        audio.setUpdate_time(new Date());
        audioMapper.insert(audio);
        return audio;
    }

    @Override
    @SysActionLogAnnotation(action_type =SysActionLogType.UPDATE, action_log_module_name = "音频")
    @Transactional
    public Audio updateAudio(Audio audio) {
        Audio org_audio = audioMapper.selectById(audio.getAudio_id());
        BeanUtils.copyProperties(audio, org_audio);
        audio.setUpdate_time(new Date());
        audioMapper.updateByPrimaryKey(audio);
        return audio;
    }

    @Override
    public Audio selectById(Long audio_id) {
        return audioMapper.selectById(audio_id);
    }

    @Override
    public List<Audio> selectByOrgId(Long org_id) {
        return audioMapper.pageQuery(org_id, null, null);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "音频")
    @Transactional
    public void deleteByCatIds(String cat_ids) {
        audioMapper.deleteByCatIds(cat_ids);
    }

    @Override
    public List<Audio> selectByCatIds(String cat_ids) {
        return audioMapper.selectByCatIds(cat_ids);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "音频")
    @Transactional
    public void deleteByIds(String audio_ids) {
        audioMapper.deleteByAudioIds(audio_ids);
    }

    @Override
    public Integer getCountByOrgId(Long org_id) {
        return audioMapper.getCountByOrgId(org_id);
    }


    @Override
    public Integer hasOffLine(Long org_id, String oldTimeStamp,Object...otherparam) {
        Integer checknum = audioMapper.checkOffLineNum(org_id, oldTimeStamp);
        if(checknum == null ) {
            checknum = 0;
        }
        return checknum;
    }

    @Override
    public List getOffLineNumList(Long org_id, String oldTimeStamp,Object...otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        return audioMapper.getOffLineNumList(org_id, oldTimeStamp, num, size);
    }

    @Override
    public Long getCatId(Long audio_id){
        return audioMapper.getCatId(audio_id);
    }
}
