package com.cjsz.tech.media.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.media.beans.FindCatOrgBean;
import com.cjsz.tech.media.beans.VideoCatBean;
import com.cjsz.tech.media.beans.VideoCatOrgBean;
import com.cjsz.tech.media.domain.VideoCat;
import com.cjsz.tech.media.domain.VideoCatOrgRel;
import com.cjsz.tech.media.mapper.VideoCatMapper;
import com.cjsz.tech.media.mapper.VideoCatOrgRelMapper;
import com.cjsz.tech.media.mapper.VideoMapper;
import com.cjsz.tech.media.service.VideoCatService;
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
 * Created by Li Yi on 2016/12/6.
 */
@Service
public class VideoCatServiceImpl implements VideoCatService {

    @Autowired
    VideoCatMapper videoCatMapper;

    @Autowired
    VideoMapper videoMapper;

    @Autowired
    VideoCatOrgRelMapper videoCatOrgRelMapper;


    @Override
    //获取组织的全部视频分类
    public List<VideoCatBean> getCats(Long org_id) {
        if(org_id == 1){
            return videoCatMapper.getCats(org_id);
        }else{
            return videoCatMapper.getEnabledCats(org_id);
        }
    }

    @Override
    //视频分类树(本机构)
    public List<VideoCatBean> getOrgCats(Long org_id) {
        return videoCatMapper.getOrgCats(org_id);
    }

    @Override
    //分类名称重复(分类名查找)
    public List<VideoCat> selectByCatName(Long root_org_id, String video_cat_name) {
        return videoCatMapper.selectByCatName(root_org_id, video_cat_name);
    }

    @Override
    @Transactional
    //保存分类
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "视频分类")
    public VideoCatBean saveCat(VideoCatBean bean, Long org_id) {
        String cat_path = "";	//分类的层次路径
        if(bean.getPid() != null && bean.getPid() != 0 ){
            VideoCat p_cat = videoCatMapper.selectByCatId(bean.getPid());
            cat_path = p_cat.getVideo_cat_path();
        }else{
            bean.setPid(0L);
            cat_path = "0|";
        }
        //分类表数据添加
        //启用，不删除，机构数1
        VideoCat videoCat = new VideoCat(org_id, bean.getPid(), bean.getVideo_cat_name(), 1, bean.getRemark(), 2, new Date(), new Date(), 1);
        videoCatMapper.insert(videoCat);
        //更新层次路径
        cat_path = cat_path + videoCat.getVideo_cat_id() + "|";
        videoCatMapper.updateCatPathByCatId(videoCat.getVideo_cat_id(), cat_path);
        //关系表数据添加
        //显示，自建
        VideoCatOrgRel rel = new VideoCatOrgRel(org_id, videoCat.getVideo_cat_id(), bean.getOrder_weight(), 1, videoCat.getVideo_cat_id(), 1, new Date(), new Date(), 2);
        videoCatOrgRelMapper.insert(rel);

        Long cat_id = bean.getPid();
        List<VideoCatOrgRel> relList = videoCatOrgRelMapper.selectListByCatId(cat_id);
        List<VideoCatOrgRel> videoCatOrgRels = new ArrayList<VideoCatOrgRel>();
        for(VideoCatOrgRel catOrgRel : relList){
            if(!catOrgRel.getOrg_id().equals(org_id)){
                VideoCatOrgRel videoCatOrgRel = new VideoCatOrgRel(catOrgRel.getOrg_id(), videoCat.getVideo_cat_id(), bean.getOrder_weight(), 1, videoCat.getVideo_cat_id(), 2, new Date(), new Date(), 2);
                videoCatOrgRels.add(videoCatOrgRel);
            }
        }
        if(videoCatOrgRels.size()>0){
            videoCatOrgRelMapper.insertList(videoCatOrgRels);
            videoCatMapper.updateOrgCount(videoCat.getVideo_cat_id(), videoCatOrgRels.size());
        }
        return videoCatOrgRelMapper.seletInfoByOrgIdAndCatId(org_id, videoCat.getVideo_cat_id());
    }

    @Override
    //分类名称重复(分类名查找不包括本身)
    public List<VideoCat> selectOtherByCatName(Long root_org_id, String video_cat_name, Long video_cat_id) {
        return videoCatMapper.selectOtherByCatName(root_org_id, video_cat_name, video_cat_id);
    }

    @Override
    //根据Id查找分类
    public VideoCat selectByCatId(Long video_cat_id) {
        return videoCatMapper.selectByCatId(video_cat_id);
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "视频分类")
    public VideoCatBean updateCat(VideoCatBean bean, Long org_id) {
        VideoCatOrgRel rel = videoCatOrgRelMapper.selectById(bean.getRel_id());
        //是否自建(1:自建;2:分配)
        if(bean.getSource_type() == 1){
            VideoCat cat = videoCatMapper.selectByCatId(bean.getVideo_cat_id());
            cat.setVideo_cat_name(bean.getVideo_cat_name());
            cat.setRemark(bean.getRemark());
            cat.setUpdate_time(new Date());
            //更新层次路径：分类层次修改，则更新层次路径
            if(!bean.getPid().equals(cat.getPid())){
                String cat_path = "";	//分类的层次路径
                if(bean.getPid() != null && bean.getPid() != 0 ){
                    VideoCat p_cat = videoCatMapper.selectOrgCatByCatId(org_id, bean.getPid());
                    cat_path = p_cat.getVideo_cat_path();
                    cat.setPid(bean.getPid());
                }else{
                    cat.setPid(0L);
                    cat_path = "0|";
                }
                videoCatMapper.updateByPrimaryKey(cat);
                String old_full_path = bean.getVideo_cat_path();        //修改前层次路径
                String new_full_path = cat_path + bean.getVideo_cat_id() + "|";	//修改后层次路径
                //更新当前修改的分类以及下级所有分类的层次路径：将层次路径中包含old_full_path的，更新为new_full_path
                videoCatMapper.updateFullPath(old_full_path, new_full_path, org_id);
            }else{
                videoCatMapper.updateByPrimaryKey(cat);
            }
        }
        //只能更新关系表
        if(rel.getOrder_weight() == null || !rel.getOrder_weight().equals(bean.getOrder_weight())){
            videoCatOrgRelMapper.updateOrderById(bean.getOrder_weight(), bean.getRel_id());
        }
        return videoCatOrgRelMapper.seletInfoByOrgIdAndCatId(org_id, bean.getVideo_cat_id());
    }

    @Override
    @Transactional
    //启用状态
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "视频分类")
    public void updateEnabled(VideoCatBean bean) {
        //当前分类启用，则上级全部启用；当前取消启用，下级取消启用，取消显示(一般机构不具备该功能)
        //是否启用(1:启用,2:不启用)
        String cat_ids = "";

        if(bean.getEnabled() == 1){
            //自己和上级全部启用
            String cat_path = bean.getVideo_cat_path();
            cat_ids = StringUtils.join(cat_path.split("\\|"), ",");
            videoCatMapper.updateEnabledByCatIds(bean.getOrg_id(), cat_ids);
        }else{
            //自己和下级全部不启用
            videoCatMapper.updateEnabledByCatPath(bean.getOrg_id(), bean.getVideo_cat_path());
            //自己和下级全部不显示
            videoCatMapper.updateShowByCatPath(bean.getOrg_id(), bean.getVideo_cat_path());
        }
    }

    @Override
    @Transactional
    //显示状态
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "视频分类")
    public void updateShow(VideoCatBean bean) {
        //当前分类显示，则上级全部显示；当前取消显示，下级取消显示
        //is_show:是否显示(1:显示,2:不显示)
        String cat_ids = "";
        if(bean.getIs_show() == 1){
            //自己和上级全部显示
            String cat_path = bean.getVideo_cat_path();
            cat_ids = StringUtils.join(cat_path.split("\\|"), ",");
            videoCatMapper.updateShowByCatIds(bean.getOrg_id(), cat_ids);
        }else if(bean.getIs_show() == 2){
            //自己和下级全部不显示
            videoCatMapper.updateShowByCatPath(bean.getOrg_id(), bean.getVideo_cat_path());
        }
    }

    @Override
    //查询机构的视频分类
    public List<Long> selectOrgCatIds(Long org_id) {
        return videoCatMapper.selectOrgCatIds(org_id);
    }

    @Override
    //通过cat_ids找到full_paths
    public List<String> getFullPathsByCatIds(String cat_ids) {
        return videoCatMapper.getFullPathsByCatIds(cat_ids);
    }

    @Override
    public List<Long> getCatIdsByFullPath(String full_path) {
        return videoCatMapper.getCatIdsByFullPath(full_path);
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "视频分类")
    public void deleteAllByCatIds(String cat_ids) {
        //删除视频
        videoMapper.deleteByCatIds(cat_ids);
        //删除视频分类机构关系
        videoCatOrgRelMapper.deleteRelsByIds(cat_ids);
        //删除视频分类
        videoCatMapper.deleteVideoCatsByIds(cat_ids);
    }

    @Override
    public List<VideoCatBean> selectTree(List<VideoCatBean> cats) {
        List<VideoCatBean> rstList  = new ArrayList<VideoCatBean>();
        while(cats.size()>0){
            VideoCatBean curMenu = cats.get(0);
            if(!rstList.contains(curMenu)) {
                rstList.add(curMenu);
            }
            cats.remove(0);
            List<VideoCatBean> children = this.getChildren(cats, curMenu.getVideo_cat_id());
            curMenu.setChildren(children);
        }
        return rstList;
    }
    private List<VideoCatBean> getChildren(List<VideoCatBean> allList, Long pid){
        List<VideoCatBean> children = new ArrayList<VideoCatBean>();
        List<VideoCatBean> copyList = new ArrayList<VideoCatBean>();
        copyList.addAll(allList);
        for(VideoCatBean curMenu :copyList){
            if(curMenu.getPid().equals(pid)){
                curMenu.setChildren(this.getChildren(allList, curMenu.getVideo_cat_id()));
                children.add(curMenu);
                allList.remove(curMenu);
            }
        }
        return children;
    }

    @Override
    public List<VideoCat> selectSiteCatsByOrgId(Long org_id) {
        return videoCatMapper.selectSiteCatsByOrgId(org_id);
    }

    @Override
    public List<VideoCatOrgBean> getAddOrgs() {
        return videoCatMapper.getAddOrgs();
    }

    @Override
    public Object getAddOrgsPageQuery(Sort sort, PageConditionBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order == null){
            PageHelper.orderBy(order);
        }
        List<VideoCatOrgBean> list = videoCatMapper.getAddOrgs();
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public List<VideoCatOrgBean> getRemoveOrgs(Long video_cat_id) {
        return videoCatMapper.getRemoveOrgs(video_cat_id);
    }

    @Override
    public Object getRemoveOrgsPageQuery(Sort sort, FindCatOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order == null){
            PageHelper.orderBy(order);
        }
        List<VideoCatOrgBean> list = videoCatMapper.getRemoveOrgs(bean.getVideo_cat_id());
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "视频机构分类关系")
    public void addOrg(Long video_cat_id, Long org_id) {
        VideoCat videoCat = videoCatMapper.selectByCatId(video_cat_id);
        String cat_path = videoCat.getVideo_cat_path();

        String[] cat_id_arr = cat_path.split("\\|");

        for(int i = 1; i<cat_id_arr.length; i++){
            VideoCatOrgRel rel = videoCatOrgRelMapper.selectByOrgIdAndCatId(org_id, Long.valueOf(cat_id_arr[i]));
            if(rel == null){
                rel = new VideoCatOrgRel(org_id, Long.valueOf(cat_id_arr[i]), System.currentTimeMillis(), 1, Long.valueOf(cat_id_arr[i]), 2, new Date(), new Date(), 2);
                videoCatOrgRelMapper.insert(rel);
                videoCatMapper.updateOrgCount(Long.valueOf(cat_id_arr[i]), 1);
            }else{
                if(rel.getIs_delete() == 1){
                    videoCatOrgRelMapper.updateIsDelete(rel.getRel_id(), 2);
                    videoCatMapper.updateOrgCount(Long.valueOf(cat_id_arr[i]), 1);
                }
            }
        }
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "视频机构分类关系")
    public void removeOrg(Long video_cat_id, Long org_id) {
        VideoCat videoCat = videoCatMapper.selectByCatId(video_cat_id);
        List<Long> video_cat_ids = videoCatMapper.getCatIdsByFullPath(videoCat.getVideo_cat_path());
        for(Long cat_id : video_cat_ids){
            VideoCatOrgRel rel = videoCatOrgRelMapper.selectByOrgIdAndCatId(org_id, cat_id);
            if(rel != null && rel.getIs_delete() == 2){
                videoCatOrgRelMapper.updateIsDelete(rel.getRel_id(), 1);
                videoCatMapper.updateOrgCount(cat_id, -1);
            }
        }
    }

    @Override
    public List<VideoCatBean> getOwnerCats(Long org_id, String video_cat_path) {
        return videoCatMapper.getOwnerCats(org_id, video_cat_path);
    }

    @Override
    public Object getOrgsPageQuery(Sort sort, FindCatOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<VideoCatOrgBean> list = videoCatMapper.getOrgQuery(bean);
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
        return videoCatMapper.getOffLineNumList(orgid, oldtimestamp, num, size);
    }

    @Override
    public Integer hasOffLine(Long orgid, String timev, Object... otherparam) {
        Integer checknum = videoCatMapper.checkOffLineNum(orgid,timev);
        if(checknum==null ) {
            checknum =0;
        }
        return checknum;
    }

    @Override
    public Long getCatId(Long video_cat_id,Long org_id){
        return videoCatMapper.getCatId(video_cat_id,org_id);
    }
}
