package com.cjsz.tech.journal.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.journal.beans.FindCatOrgBean;
import com.cjsz.tech.journal.beans.NewspaperCatBean;
import com.cjsz.tech.journal.beans.NewspaperCatOrgBean;
import com.cjsz.tech.journal.domain.NewspaperCat;
import com.cjsz.tech.journal.domain.NewspaperCatOrgRel;
import com.cjsz.tech.journal.mapper.NewspaperCatMapper;
import com.cjsz.tech.journal.mapper.NewspaperMapper;
import com.cjsz.tech.journal.mapper.NewspaperCatOrgRelMapper;
import com.cjsz.tech.journal.service.NewspaperCatService;
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
 * Author:Jason
 * Date:2016/12/2
 */
@Service
public class NewspaperCatServiceImpl implements NewspaperCatService {
    @Autowired
    NewspaperCatMapper newspaperCatMapper;

    @Autowired
    NewspaperCatOrgRelMapper newspaperCatOrgRelMapper;

    @Autowired
    NewspaperMapper newspaperMapper;


    @Override
    //获取组织的全部报纸分类
    public List<NewspaperCatBean> getCats(Long org_id) {
        if(org_id == 1){
            return newspaperCatMapper.getCats(org_id);
        }else{
            return newspaperCatMapper.getEnabledCats(org_id);
        }
    }

    @Override
    //报纸分类树(本机构)
    public List<NewspaperCatBean> getOrgCats(Long org_id) {
        return newspaperCatMapper.getOrgCats(org_id);
    }

    @Override
    //分类名称重复(分类名查找)
    public List<NewspaperCat> selectByCatName(Long root_org_id, String newspaper_cat_name) {
        return newspaperCatMapper.selectByCatName(root_org_id, newspaper_cat_name);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "报纸分类")
    @Transactional
    //保存分类
    public NewspaperCatBean saveCat(NewspaperCatBean bean, Long org_id) {
        String cat_path = "";	//分类的层次路径
        if(bean.getPid() != null && bean.getPid() != 0 ){
            NewspaperCat p_cat = newspaperCatMapper.selectByCatId(bean.getPid());
            cat_path = p_cat.getNewspaper_cat_path();
        }else{
            bean.setPid(0L);
            cat_path = "0|";
        }
        //分类表数据添加
        //启用，不删除，机构数1
        NewspaperCat newspaperCat = new NewspaperCat(org_id, bean.getPid(), bean.getNewspaper_cat_name(), 1, bean.getRemark(), 2, new Date(), new Date(), 1);
        newspaperCatMapper.insert(newspaperCat);
        //更新层次路径
        cat_path = cat_path + newspaperCat.getNewspaper_cat_id() + "|";
        newspaperCatMapper.updateCatPathByCatId(newspaperCat.getNewspaper_cat_id(), cat_path);
        //关系表数据添加
        //显示，自建
        NewspaperCatOrgRel rel = new NewspaperCatOrgRel(org_id, newspaperCat.getNewspaper_cat_id(), bean.getOrder_weight(), 1, newspaperCat.getNewspaper_cat_id(), 1, new Date(), new Date(), 2);
        newspaperCatOrgRelMapper.insert(rel);

        Long cat_id = bean.getPid();
        List<NewspaperCatOrgRel> relList = newspaperCatOrgRelMapper.selectListByCatId(cat_id);
        List<NewspaperCatOrgRel> newspaperCatOrgRels = new ArrayList<NewspaperCatOrgRel>();
        for(NewspaperCatOrgRel catOrgRel : relList){
            if(!catOrgRel.getOrg_id().equals(org_id)){
                NewspaperCatOrgRel newspaperCatOrgRel = new NewspaperCatOrgRel(catOrgRel.getOrg_id(), newspaperCat.getNewspaper_cat_id(), bean.getOrder_weight(), 1, newspaperCat.getNewspaper_cat_id(), 2, new Date(), new Date(), 2);
                newspaperCatOrgRels.add(newspaperCatOrgRel);
            }
        }
        if(newspaperCatOrgRels.size()>0){
            newspaperCatOrgRelMapper.insertList(newspaperCatOrgRels);
            newspaperCatMapper.updateOrgCount(newspaperCat.getNewspaper_cat_id(), newspaperCatOrgRels.size());
        }
        return newspaperCatOrgRelMapper.seletInfoByOrgIdAndCatId(org_id, newspaperCat.getNewspaper_cat_id());
    }

    @Override
    //分类名称重复(分类名查找不包括本身)
    public List<NewspaperCat> selectOtherByCatName(Long root_org_id, String newspaper_cat_name, Long newspaper_cat_id) {
        return newspaperCatMapper.selectOtherByCatName(root_org_id, newspaper_cat_name, newspaper_cat_id);
    }

    @Override
    //根据Id查找分类
    public NewspaperCat selectByCatId(Long newspaper_cat_id) {
        return newspaperCatMapper.selectByCatId(newspaper_cat_id);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "报纸分类")
    @Transactional
    //修改分类
    public NewspaperCatBean updateCat(NewspaperCatBean bean, Long org_id) {
        NewspaperCatOrgRel rel = newspaperCatOrgRelMapper.selectById(bean.getRel_id());
        //是否自建(1:自建;2:分配)
        if(bean.getSource_type() == 1){
            NewspaperCat cat = newspaperCatMapper.selectByCatId(bean.getNewspaper_cat_id());
            cat.setNewspaper_cat_name(bean.getNewspaper_cat_name());
            cat.setRemark(bean.getRemark());
            cat.setUpdate_time(new Date());
            //更新层次路径：分类层次修改，则更新层次路径
            if(!bean.getPid().equals(cat.getPid())){
                String cat_path = "";	//分类的层次路径
                if(bean.getPid() != null && bean.getPid() != 0 ){
                    NewspaperCat p_cat = newspaperCatMapper.selectOrgCatByCatId(org_id, bean.getPid());
                    cat_path = p_cat.getNewspaper_cat_path();
                    cat.setPid(bean.getPid());
                }else{
                    cat.setPid(0L);
                    cat_path = "0|";
                }
                newspaperCatMapper.updateByPrimaryKey(cat);
                String old_full_path = bean.getNewspaper_cat_path();		//修改前层次路径
                String new_full_path = cat_path + bean.getNewspaper_cat_id() + "|";	//修改后层次路径
                //更新当前修改的分类以及下级所有分类的层次路径：将层次路径中包含old_full_path的，更新为new_full_path
                newspaperCatMapper.updateFullPath(old_full_path, new_full_path, org_id);
            }else{
                newspaperCatMapper.updateByPrimaryKey(cat);
            }
        }
        //只能更新关系表
        if(rel.getOrder_weight() == null || !rel.getOrder_weight().equals(bean.getOrder_weight())){
            newspaperCatOrgRelMapper.updateOrderById(bean.getOrder_weight(), bean.getRel_id());
        }
        return newspaperCatOrgRelMapper.seletInfoByOrgIdAndCatId(org_id, bean.getNewspaper_cat_id());
    }

    @Override
    @Transactional
    //启用状态
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "报纸分类")
    public void updateEnabled(NewspaperCatBean bean) {
        //当前分类启用，则上级全部启用；当前取消启用，下级取消启用，取消显示(一般机构不具备该功能)
        //是否启用(1:启用,2:不启用)
        String cat_ids = "";

        if(bean.getEnabled() == 1){
            //自己和上级全部启用
            String cat_path = bean.getNewspaper_cat_path();
            cat_ids = StringUtils.join(cat_path.split("\\|"), ",");
            newspaperCatMapper.updateEnabledByCatIds(bean.getOrg_id(), cat_ids);
        }else{
            //自己和下级全部不启用
            newspaperCatMapper.updateEnabledByCatPath(bean.getOrg_id(), bean.getNewspaper_cat_path());
            //自己和下级全部不显示
            newspaperCatMapper.updateShowByCatPath(bean.getOrg_id(), bean.getNewspaper_cat_path());
        }
    }

    @Override
    @Transactional
    //显示状态
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "报纸分类")
    public void updateShow(NewspaperCatBean bean) {
        //当前分类显示，则上级全部显示；当前取消显示，下级取消显示
        //is_show:是否显示(1:显示,2:不显示)
        String cat_ids = "";
        if(bean.getIs_show() == 1){
            //自己和上级全部显示
            String cat_path = bean.getNewspaper_cat_path();
            cat_ids = StringUtils.join(cat_path.split("\\|"), ",");
            newspaperCatMapper.updateShowByCatIds(bean.getOrg_id(), cat_ids);
        }else if(bean.getIs_show() == 2){
            //自己和下级全部不显示
            newspaperCatMapper.updateShowByCatPath(bean.getOrg_id(), bean.getNewspaper_cat_path());
        }
    }

    @Override
    //查询机构的报纸分类
    public List<Long> selectOrgCatIds(Long org_id) {
        return newspaperCatMapper.selectOrgCatIds(org_id);
    }

    @Override
    //通过cat_ids找到full_paths
    public List<String> getFullPathsByCatIds(String cat_ids){
        return newspaperCatMapper.getFullPathsByCatIds(cat_ids);
    }

    @Override
    public List<Long> getCatIdsByFullPath(String full_path) {
        return newspaperCatMapper.getCatIdsByFullPath(full_path);
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "报纸分类")
    public void deleteAllByCatIds(String cat_ids) {
        //删除报纸
        newspaperMapper.deleteByCatIds(cat_ids);
        //删除报纸分类机构关系
        newspaperCatOrgRelMapper.deleteRelsByIds(cat_ids);
        //删除报纸分类
        newspaperCatMapper.deleteNewspaperCatsByIds(cat_ids);
    }

    @Override
    //将报纸分类转化为树形结构
    public List<NewspaperCatBean> selectTree(List<NewspaperCatBean> cats1) {
        List<NewspaperCatBean> rstList  = new ArrayList<NewspaperCatBean>();
        while(cats1.size()>0){
            NewspaperCatBean curMenu = cats1.get(0);
            if(!rstList.contains(curMenu)) {
                rstList.add(curMenu);
            }
            cats1.remove(0);
            List<NewspaperCatBean> children = this.getChildren(cats1, curMenu.getNewspaper_cat_id());
            curMenu.setChildren(children);
        }
        return rstList;
    }

    private List<NewspaperCatBean> getChildren(List<NewspaperCatBean> allList, Long pid){
        List<NewspaperCatBean> children = new ArrayList<NewspaperCatBean>();
        List<NewspaperCatBean> copyList = new ArrayList<NewspaperCatBean>();
        copyList.addAll(allList);
        for(NewspaperCatBean curMenu :copyList){
            if(curMenu.getPid().equals(pid)){
                curMenu.setChildren(this.getChildren(allList, curMenu.getNewspaper_cat_id()));
                children.add(curMenu);
                allList.remove(curMenu);
            }
        }
        return children;
    }

    @Override
    public List<NewspaperCat> selectSiteCatsByOrgId(Long org_id) {
        return newspaperCatMapper.selectSiteCatsByOrgId(org_id);
    }

    @Override
    public List<NewspaperCatOrgBean> getAddOrgs() {
        return newspaperCatMapper.getAddOrgs();
    }

    @Override
    public Object getAddOrgsPageQuery(Sort sort, PageConditionBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order == null){
            PageHelper.orderBy(order);
        }
        List<NewspaperCatOrgBean> list = newspaperCatMapper.getAddOrgs();
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public List<NewspaperCatOrgBean> getRemoveOrgs(Long newspaper_cat_id) {
        return newspaperCatMapper.getRemoveOrgs(newspaper_cat_id);
    }

    @Override
    public Object getRemoveOrgsPageQuery(Sort sort, FindCatOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order == null){
            PageHelper.orderBy(order);
        }
        List<NewspaperCatOrgBean> list = newspaperCatMapper.getRemoveOrgs(bean.getNewspaper_cat_id());
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "报纸机构分类关系")
    public void addOrg(Long video_cat_id, Long org_id) {
        NewspaperCat newspaperCat = newspaperCatMapper.selectByCatId(video_cat_id);
        String cat_path = newspaperCat.getNewspaper_cat_path();

        String[] cat_id_arr = cat_path.split("\\|");

        for(int i = 1; i<cat_id_arr.length; i++){
            NewspaperCatOrgRel rel = newspaperCatOrgRelMapper.selectByOrgIdAndCatId(org_id, Long.valueOf(cat_id_arr[i]));
            if(rel == null){
                rel = new NewspaperCatOrgRel(org_id, Long.valueOf(cat_id_arr[i]), System.currentTimeMillis(), 1, Long.valueOf(cat_id_arr[i]), 2, new Date(), new Date(), 2);
                newspaperCatOrgRelMapper.insert(rel);
                newspaperCatMapper.updateOrgCount(Long.valueOf(cat_id_arr[i]), 1);
            }else{
                if(rel.getIs_delete() == 1){
                    newspaperCatOrgRelMapper.updateIsDelete(rel.getRel_id(), 2);
                    newspaperCatMapper.updateOrgCount(Long.valueOf(cat_id_arr[i]), 1);
                }
            }
        }
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "报纸机构分类关系")
    public void removeOrg(Long video_cat_id, Long org_id) {
        NewspaperCat newspaperCat = newspaperCatMapper.selectByCatId(video_cat_id);
        List<Long> video_cat_ids = newspaperCatMapper.getCatIdsByFullPath(newspaperCat.getNewspaper_cat_path());
        for(Long cat_id : video_cat_ids){
            NewspaperCatOrgRel rel = newspaperCatOrgRelMapper.selectByOrgIdAndCatId(org_id, cat_id);
            if(rel != null && rel.getIs_delete() == 2){
                newspaperCatOrgRelMapper.updateIsDelete(rel.getRel_id(), 1);
                newspaperCatMapper.updateOrgCount(cat_id, -1);
            }
        }
    }

    @Override
    public List<NewspaperCatBean> getOwnerCats(Long org_id, String video_cat_path) {
        return newspaperCatMapper.getOwnerCats(org_id, video_cat_path);
    }

    @Override
    public Object getOrgsPageQuery(Sort sort, FindCatOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<NewspaperCatOrgBean> list = newspaperCatMapper.getOrgQuery(bean);
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public List<NewspaperCatBean> getOffLineNumList(Long orgid, String oldtimestamp,Object...otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        return newspaperCatMapper.getOffLineNumList(orgid, oldtimestamp, num, size);
    }

    @Override
    public Integer hasOffLine(Long orgid, String timev,Object...otherparam) {
        Integer checknum = newspaperCatMapper.checkOffLineNum(orgid,timev);
        if(checknum==null ) {
            checknum =0;
        }
        return checknum;
    }

    @Override
    public Long getCatId(Long cat_id,Long org_id){
        return newspaperCatMapper.getCatId(cat_id,org_id);
    }

    @Override
    public List<NewspaperCatBean> getAllCats(Long org_id) {
        return newspaperCatMapper.selectAllCats(org_id);
    }

    @Override
    public void orderByOrg(Long newspaper_cat_id, Long order_weight, Long org_id) {
        newspaperCatOrgRelMapper.orderByOrg(newspaper_cat_id,order_weight,org_id);
    }
}
