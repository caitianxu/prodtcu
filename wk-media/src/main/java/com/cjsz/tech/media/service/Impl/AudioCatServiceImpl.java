package com.cjsz.tech.media.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.media.beans.FindCatOrgBean;
import com.cjsz.tech.media.beans.AudioCatBean;
import com.cjsz.tech.media.beans.AudioCatOrgBean;
import com.cjsz.tech.media.domain.AudioCat;
import com.cjsz.tech.media.domain.AudioCatOrgRel;
import com.cjsz.tech.media.mapper.AudioCatMapper;
import com.cjsz.tech.media.mapper.AudioCatOrgRelMapper;
import com.cjsz.tech.media.mapper.AudioMapper;
import com.cjsz.tech.media.service.AudioCatService;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Li Yi on 2016/12/7.
 */
@Service
public class AudioCatServiceImpl implements AudioCatService {

    @Autowired
    AudioCatMapper audioCatMapper;

    @Autowired
    AudioMapper audioMapper;

    @Autowired
    AudioCatOrgRelMapper audioCatOrgRelMapper;

    @Override
    //获取组织的全部音频分类
    public List<AudioCatBean> getCats(Long org_id) {
        if(org_id == 1){
            return audioCatMapper.getCats(org_id);
        }else{
            return audioCatMapper.getEnabledCats(org_id);
        }
    }

    @Override
    //音频分类树(本机构)
    public List<AudioCatBean> getOrgCats(Long org_id) {
        return audioCatMapper.getOrgCats(org_id);
    }

    @Override
    //分类名称重复(分类名查找)
    public List<AudioCat> selectByCatName(Long root_org_id, String audio_cat_name) {
        return audioCatMapper.selectByCatName(root_org_id, audio_cat_name);
    }

    @Override
    @Transactional
    //保存分类
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "音频分类")
    public AudioCatBean saveCat(AudioCatBean bean, Long org_id) {
        String cat_path = "";	//分类的层次路径
        if(bean.getPid() != null && bean.getPid() != 0 ){
            AudioCat p_cat = audioCatMapper.selectByCatId(bean.getPid());
            cat_path = p_cat.getAudio_cat_path();
        }else{
            bean.setPid(0L);
            cat_path = "0|";
        }
        //分类表数据添加
        //启用，不删除，机构数1
        AudioCat audioCat = new AudioCat(org_id, bean.getPid(), bean.getAudio_cat_name(), 1, bean.getRemark(), 2, new Date(), new Date(), 1);
        audioCatMapper.insert(audioCat);
        //更新层次路径
        cat_path = cat_path + audioCat.getAudio_cat_id() + "|";
        audioCatMapper.updateCatPathByCatId(audioCat.getAudio_cat_id(), cat_path);
        //关系表数据添加
        //显示，自建
        AudioCatOrgRel rel = new AudioCatOrgRel(org_id, audioCat.getAudio_cat_id(), bean.getOrder_weight(), 1, audioCat.getAudio_cat_id(), 1, new Date(), new Date(), 2);
        audioCatOrgRelMapper.insert(rel);

        Long cat_id = bean.getPid();
        List<AudioCatOrgRel> relList = audioCatOrgRelMapper.selectListByCatId(cat_id);
        List<AudioCatOrgRel> AudioCatOrgRels = new ArrayList<AudioCatOrgRel>();
        for(AudioCatOrgRel catOrgRel : relList){
            if(!catOrgRel.getOrg_id().equals(org_id)){
                AudioCatOrgRel AudioCatOrgRel = new AudioCatOrgRel(catOrgRel.getOrg_id(), audioCat.getAudio_cat_id(), bean.getOrder_weight(), 1, audioCat.getAudio_cat_id(), 2, new Date(), new Date(), 2);
                AudioCatOrgRels.add(AudioCatOrgRel);
            }
        }
        if(AudioCatOrgRels.size()>0){
            audioCatOrgRelMapper.insertList(AudioCatOrgRels);
            audioCatMapper.updateOrgCount(audioCat.getAudio_cat_id(), AudioCatOrgRels.size());
        }
        return audioCatOrgRelMapper.seletInfoByOrgIdAndCatId(org_id, audioCat.getAudio_cat_id());
    }

    @Override
    //分类名称重复(分类名查找不包括本身)
    public List<AudioCat> selectOtherByCatName(Long root_org_id, String audio_cat_name, Long audio_cat_id) {
        return audioCatMapper.selectOtherByCatName(root_org_id, audio_cat_name, audio_cat_id);
    }

    @Override
    //根据Id查找分类
    public AudioCat selectByCatId(Long audio_cat_id) {
        return audioCatMapper.selectByCatId(audio_cat_id);
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "音频分类")
    public AudioCatBean updateCat(AudioCatBean bean, Long org_id) {
        AudioCatOrgRel rel = audioCatOrgRelMapper.selectById(bean.getRel_id());
        //是否自建(1:自建;2:分配)
        if(bean.getSource_type() == 1){
            AudioCat cat = audioCatMapper.selectByCatId(bean.getAudio_cat_id());
            cat.setAudio_cat_name(bean.getAudio_cat_name());
            cat.setRemark(bean.getRemark());
            cat.setUpdate_time(new Date());
            //更新层次路径：分类层次修改，则更新层次路径
            if(!bean.getPid().equals(cat.getPid())){
                String cat_path = "";	//分类的层次路径
                if(bean.getPid() != null && bean.getPid() != 0 ){
                    AudioCat p_cat = audioCatMapper.selectOrgCatByCatId(org_id, bean.getPid());
                    cat_path = p_cat.getAudio_cat_path();
                    cat.setPid(bean.getPid());
                }else{
                    cat.setPid(0L);
                    cat_path = "0|";
                }
                audioCatMapper.updateByPrimaryKey(cat);
                String old_full_path = bean.getAudio_cat_path();        //修改前层次路径
                String new_full_path = cat_path + bean.getAudio_cat_id() + "|";	//修改后层次路径
                //更新当前修改的分类以及下级所有分类的层次路径：将层次路径中包含old_full_path的，更新为new_full_path
                audioCatMapper.updateFullPath(old_full_path, new_full_path, org_id);
            }else{
                audioCatMapper.updateByPrimaryKey(cat);
            }
        }
        //只能更新关系表
        if(rel.getOrder_weight() == null || !rel.getOrder_weight().equals(bean.getOrder_weight())){
            audioCatOrgRelMapper.updateOrderById(bean.getOrder_weight(), bean.getRel_id());
        }
        return audioCatOrgRelMapper.seletInfoByOrgIdAndCatId(org_id, bean.getAudio_cat_id());
    }

    @Override
    @Transactional
    //启用状态
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "音频分类")
    public void updateEnabled(AudioCatBean bean) {
        //当前分类启用，则上级全部启用；当前取消启用，下级取消启用，取消显示(一般机构不具备该功能)
        //是否启用(1:启用,2:不启用)
        String cat_ids = "";

        if(bean.getEnabled() == 1){
            //自己和上级全部启用
            String cat_path = bean.getAudio_cat_path();
            cat_ids = StringUtils.join(cat_path.split("\\|"), ",");
            audioCatMapper.updateEnabledByCatIds(bean.getOrg_id(), cat_ids);
        }else{
            //自己和下级全部不启用
            audioCatMapper.updateEnabledByCatPath(bean.getOrg_id(), bean.getAudio_cat_path());
            //自己和下级全部不显示
            audioCatMapper.updateShowByCatPath(bean.getOrg_id(), bean.getAudio_cat_path());
        }
    }

    @Override
    @Transactional
    //显示状态
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "音频分类")
    public void updateShow(AudioCatBean bean) {
        //当前分类显示，则上级全部显示；当前取消显示，下级取消显示
        //is_show:是否显示(1:显示,2:不显示)
        String cat_ids = "";
        if(bean.getIs_show() == 1){
            //自己和上级全部显示
            String cat_path = bean.getAudio_cat_path();
            cat_ids = StringUtils.join(cat_path.split("\\|"), ",");
            audioCatMapper.updateShowByCatIds(bean.getOrg_id(), cat_ids);
        }else if(bean.getIs_show() == 2){
            //自己和下级全部不显示
            audioCatMapper.updateShowByCatPath(bean.getOrg_id(), bean.getAudio_cat_path());
        }
    }

    @Override
    //查询机构的音频分类
    public List<Long> selectOrgCatIds(Long org_id) {
        return audioCatMapper.selectOrgCatIds(org_id);
    }

    @Override
    //通过cat_ids找到full_paths
    public List<String> getFullPathsByCatIds(String cat_ids) {
        return audioCatMapper.getFullPathsByCatIds(cat_ids);
    }

    @Override
    public List<Long> getCatIdsByFullPath(String full_path) {
        return audioCatMapper.getCatIdsByFullPath(full_path);
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "音频分类")
    public void deleteAllByCatIds(String cat_ids) {
        //删除音频
        audioMapper.deleteByCatIds(cat_ids);
        //删除音频分类机构关系
        audioCatOrgRelMapper.deleteRelsByIds(cat_ids);
        //删除音频分类
        audioCatMapper.deleteAudioCatsByIds(cat_ids);
    }

    @Override
    public List<AudioCatBean> selectTree(List<AudioCatBean> cats) {
        List<AudioCatBean> rstList  = new ArrayList<AudioCatBean>();
        while(cats.size()>0){
            AudioCatBean curMenu = cats.get(0);
            if(!rstList.contains(curMenu)) {
                rstList.add(curMenu);
            }
            cats.remove(0);
            List<AudioCatBean> children = this.getChildren(cats, curMenu.getAudio_cat_id());
            curMenu.setChildren(children);
        }
        return rstList;
    }
    private List<AudioCatBean> getChildren(List<AudioCatBean> allList, Long pid){
        List<AudioCatBean> children = new ArrayList<AudioCatBean>();
        List<AudioCatBean> copyList = new ArrayList<AudioCatBean>();
        copyList.addAll(allList);
        for(AudioCatBean curMenu :copyList){
            if(curMenu.getPid().equals(pid)){
                curMenu.setChildren(this.getChildren(allList, curMenu.getAudio_cat_id()));
                children.add(curMenu);
                allList.remove(curMenu);
            }
        }
        return children;
    }

    @Override
    public List<AudioCat> selectSiteCatsByOrgId(Long org_id) {
        return audioCatMapper.selectSiteCatsByOrgId(org_id);
    }

    @Override
    public List<AudioCatOrgBean> getAddOrgs() {
        return audioCatMapper.getAddOrgs();
    }

    @Override
    public Object getAddOrgsPageQuery(Sort sort, PageConditionBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order == null){
            PageHelper.orderBy(order);
        }
        List<AudioCatOrgBean> list = audioCatMapper.getAddOrgs();
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public List<AudioCatOrgBean> getRemoveOrgs(Long audio_cat_id) {
        return audioCatMapper.getRemoveOrgs(audio_cat_id);
    }

    @Override
    public Object getRemoveOrgsPageQuery(Sort sort, FindCatOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order == null){
            PageHelper.orderBy(order);
        }
        List<AudioCatOrgBean> list = audioCatMapper.getRemoveOrgs(bean.getAudio_cat_id());
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "音频机构分类关系")
    public void addOrg(Long audio_cat_id, Long org_id) {
        AudioCat audioCat = audioCatMapper.selectByCatId(audio_cat_id);
        String cat_path = audioCat.getAudio_cat_path();

        String[] cat_id_arr = cat_path.split("\\|");

        for(int i = 1; i<cat_id_arr.length; i++){
            AudioCatOrgRel rel = audioCatOrgRelMapper.selectByOrgIdAndCatId(org_id, Long.valueOf(cat_id_arr[i]));
            if(rel == null){
                rel = new AudioCatOrgRel(org_id, Long.valueOf(cat_id_arr[i]), System.currentTimeMillis(), 1, Long.valueOf(cat_id_arr[i]), 2, new Date(), new Date(), 2);
                audioCatOrgRelMapper.insert(rel);
                audioCatMapper.updateOrgCount(Long.valueOf(cat_id_arr[i]), 1);
            }else{
                if(rel.getIs_delete() == 1){
                    audioCatOrgRelMapper.updateIsDelete(rel.getRel_id(), 2);
                    audioCatMapper.updateOrgCount(Long.valueOf(cat_id_arr[i]), 1);
                }
            }
        }
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "音频机构分类关系")
    public void removeOrg(Long audio_cat_id, Long org_id) {
        AudioCat audioCat = audioCatMapper.selectByCatId(audio_cat_id);
        List<Long> audio_cat_ids = audioCatMapper.getCatIdsByFullPath(audioCat.getAudio_cat_path());
        for(Long cat_id : audio_cat_ids){
            AudioCatOrgRel rel = audioCatOrgRelMapper.selectByOrgIdAndCatId(org_id, cat_id);
            if(rel != null && rel.getIs_delete() == 2){
                audioCatOrgRelMapper.updateIsDelete(rel.getRel_id(), 1);
                audioCatMapper.updateOrgCount(cat_id, -1);
            }
        }
    }

    @Override
    public List<AudioCatBean> getOwnerCats(Long org_id, String audio_cat_path) {
        return audioCatMapper.getOwnerCats(org_id, audio_cat_path);
    }

    @Override
    public Object getOrgsPageQuery(Sort sort, FindCatOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<AudioCatOrgBean> list = audioCatMapper.getOrgQuery(bean);
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public List getOffLineNumList(Long orgid, String oldtimestamp, Object... otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        return audioCatMapper.getOffLineNumList(orgid, oldtimestamp, num, size);
    }

    @Override
    public Integer hasOffLine(Long orgid, String timev, Object... otherparam) {
        Integer checknum = audioCatMapper.checkOffLineNum(orgid,timev);
        if(checknum==null ) {
            checknum =0;
        }
        return checknum;
    }

    @Override
    public Long getAudioCatId(Long audio_cat_id,Long org_id){
        return audioCatMapper.getAudioCatId(audio_cat_id,org_id);
    }
}
