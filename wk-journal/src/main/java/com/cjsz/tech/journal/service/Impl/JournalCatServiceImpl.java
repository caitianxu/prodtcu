package com.cjsz.tech.journal.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.journal.beans.FindCatOrgBean;
import com.cjsz.tech.journal.beans.JournalCatBean;
import com.cjsz.tech.journal.beans.JournalCatOrgBean;
import com.cjsz.tech.journal.domain.JournalCat;
import com.cjsz.tech.journal.domain.JournalCatOrgRel;
import com.cjsz.tech.journal.mapper.JournalCatMapper;
import com.cjsz.tech.journal.mapper.JournalCatOrgRelMapper;
import com.cjsz.tech.journal.mapper.JournalMapper;
import com.cjsz.tech.journal.service.JournalCatService;
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
 * Created by Administrator on 2016/10/25.
 */
@Service
public class JournalCatServiceImpl implements JournalCatService {

    @Autowired
    JournalCatMapper journalCatMapper;

    @Autowired
    JournalCatOrgRelMapper journalCatOrgRelMapper;

    @Autowired
    JournalMapper journalMapper;


    @Override
    //获取组织的全部期刊分类
    public List<JournalCatBean> getCats(Long org_id) {
        if(org_id == 1){
            return journalCatMapper.getCats(org_id);
        }else{
            return journalCatMapper.getEnabledCats(org_id);
        }
    }

    @Override
    //期刊分类树(本机构)
    public List<JournalCatBean> getOrgCats(Long org_id) {
        return journalCatMapper.getOrgCats(org_id);
    }

    @Override
    //分类名称重复(分类名查找)
    public List<JournalCat> selectByCatName(Long root_org_id, String journal_cat_name) {
        return journalCatMapper.selectByCatName(root_org_id, journal_cat_name);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "期刊分类")
    @Transactional
    //保存分类
    public JournalCatBean saveCat(JournalCatBean bean, Long org_id) {
        String cat_path = "";	//分类的层次路径
        if(bean.getPid() != null && bean.getPid() != 0 ){
            JournalCat p_cat = journalCatMapper.selectByCatId(bean.getPid());
            cat_path = p_cat.getJournal_cat_path();
        }else{
            bean.setPid(0L);
            cat_path = "0|";
        }
        //分类表数据添加
        //启用，不删除，机构数1
        JournalCat journalCat = new JournalCat(org_id, bean.getPid(), bean.getJournal_cat_name(), 1, bean.getRemark(), 2, new Date(), new Date(), 1);
        journalCatMapper.insert(journalCat);
        //更新层次路径
        cat_path = cat_path + journalCat.getJournal_cat_id() + "|";
        journalCatMapper.updateCatPathByCatId(journalCat.getJournal_cat_id(), cat_path);
        //关系表数据添加
        //显示，自建
        JournalCatOrgRel rel = new JournalCatOrgRel(org_id, journalCat.getJournal_cat_id(), bean.getOrder_weight(), 1, journalCat.getJournal_cat_id(), 1, new Date(), new Date(), 2);
        journalCatOrgRelMapper.insert(rel);

        Long cat_id = bean.getPid();
        List<JournalCatOrgRel> relList = journalCatOrgRelMapper.selectListByCatId(cat_id);
        List<JournalCatOrgRel> journalCatOrgRels = new ArrayList<JournalCatOrgRel>();
        for(JournalCatOrgRel catOrgRel : relList){
            if(!catOrgRel.getOrg_id().equals(org_id)){
                JournalCatOrgRel journalCatOrgRel = new JournalCatOrgRel(catOrgRel.getOrg_id(), journalCat.getJournal_cat_id(), bean.getOrder_weight(), 1, journalCat.getJournal_cat_id(), 2, new Date(), new Date(), 2);
                journalCatOrgRels.add(journalCatOrgRel);
            }
        }
        if(journalCatOrgRels.size()>0){
            journalCatOrgRelMapper.insertList(journalCatOrgRels);
            journalCatMapper.updateOrgCount(journalCat.getJournal_cat_id(), journalCatOrgRels.size());
        }
        return journalCatOrgRelMapper.seletInfoByOrgIdAndCatId(org_id, journalCat.getJournal_cat_id());
    }

    @Override
    //分类名称重复(分类名查找不包括本身)
    public List<JournalCat> selectOtherByCatName(Long root_org_id, String journal_cat_name, Long journal_cat_id) {
        return journalCatMapper.selectOtherByCatName(root_org_id, journal_cat_name, journal_cat_id);
    }

    @Override
    //根据Id查找分类
    public JournalCat selectByCatId(Long journal_cat_id) {
        return journalCatMapper.selectByCatId(journal_cat_id);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "期刊分类")
    @Transactional
    //修改分类
    public JournalCatBean updateCat(JournalCatBean bean, Long org_id) {
        JournalCatOrgRel rel = journalCatOrgRelMapper.selectById(bean.getRel_id());
        //是否自建(1:自建;2:分配)
        if(bean.getSource_type() == 1){
            JournalCat cat = journalCatMapper.selectByCatId(bean.getJournal_cat_id());
            cat.setJournal_cat_name(bean.getJournal_cat_name());
            cat.setRemark(bean.getRemark());
            cat.setUpdate_time(new Date());
            //更新层次路径：分类层次修改，则更新层次路径
            if(!bean.getPid().equals(cat.getPid())){
                String cat_path = "";	//分类的层次路径
                if(bean.getPid() != null && bean.getPid() != 0 ){
                    JournalCat p_cat = journalCatMapper.selectOrgCatByCatId(org_id, bean.getPid());
                    cat_path = p_cat.getJournal_cat_path();
                    cat.setPid(bean.getPid());
                }else{
                    cat.setPid(0L);
                    cat_path = "0|";
                }
                journalCatMapper.updateByPrimaryKey(cat);
                String old_full_path = bean.getJournal_cat_path();		//修改前层次路径
                String new_full_path = cat_path + bean.getJournal_cat_id() + "|";	//修改后层次路径
                //更新当前修改的分类以及下级所有分类的层次路径：将层次路径中包含old_full_path的，更新为new_full_path
                journalCatMapper.updateFullPath(old_full_path, new_full_path, org_id);
            }else{
                journalCatMapper.updateByPrimaryKey(cat);
            }
        }
        //只能更新关系表
        if(rel.getOrder_weight() == null || !rel.getOrder_weight().equals(bean.getOrder_weight())){
            journalCatOrgRelMapper.updateOrderById(bean.getOrder_weight(), bean.getRel_id());
        }
        return journalCatOrgRelMapper.seletInfoByOrgIdAndCatId(org_id, bean.getJournal_cat_id());
    }

    @Override
    @Transactional
    //启用状态
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "期刊分类")
    public void updateEnabled(JournalCatBean bean) {
        //当前分类启用，则上级全部启用；当前取消启用，下级取消启用，取消显示(一般机构不具备该功能)
        //是否启用(1:启用,2:不启用)
        String cat_ids = "";

        if(bean.getEnabled() == 1){
            //自己和上级全部启用
            String cat_path = bean.getJournal_cat_path();
            cat_ids = StringUtils.join(cat_path.split("\\|"), ",");
            journalCatMapper.updateEnabledByCatIds(bean.getOrg_id(), cat_ids);
        }else{
            //自己和下级全部不启用
            journalCatMapper.updateEnabledByCatPath(bean.getOrg_id(), bean.getJournal_cat_path());
            //自己和下级全部不显示
            journalCatMapper.updateShowByCatPath(bean.getOrg_id(), bean.getJournal_cat_path());
        }
    }

    @Override
    @Transactional
    //显示状态
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "期刊分类")
    public void updateShow(JournalCatBean bean) {
        //当前分类显示，则上级全部显示；当前取消显示，下级取消显示
        //is_show:是否显示(1:显示,2:不显示)
        String cat_ids = "";
        if(bean.getIs_show() == 1){
            //自己和上级全部显示
            String cat_path = bean.getJournal_cat_path();
            cat_ids = StringUtils.join(cat_path.split("\\|"), ",");
            journalCatMapper.updateShowByCatIds(bean.getOrg_id(), cat_ids);
        }else if(bean.getIs_show() == 2){
            //自己和下级全部不显示
            journalCatMapper.updateShowByCatPath(bean.getOrg_id(), bean.getJournal_cat_path());
        }
    }

    @Override
    //查询机构的期刊分类
    public List<Long> selectOrgCatIds(Long org_id) {
        return journalCatMapper.selectOrgCatIds(org_id);
    }

    @Override
    //通过cat_ids找到full_paths
    public List<String> getFullPathsByCatIds(String cat_ids){
        return journalCatMapper.getFullPathsByCatIds(cat_ids);
    }

    @Override
    public List<Long> getCatIdsByFullPath(String full_path) {
        return journalCatMapper.getCatIdsByFullPath(full_path);
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "期刊分类")
    public void deleteAllByCatIds(String cat_ids) {
        //删除期刊
        journalMapper.deleteByCatIds(cat_ids);
        //删除期刊分类机构关系
        journalCatOrgRelMapper.deleteRelsByIds(cat_ids);
        //删除期刊分类
        journalCatMapper.deleteJournalCatsByIds(cat_ids);
    }

    @Override
    //将期刊分类转化为树形结构
    public List<JournalCatBean> selectTree(List<JournalCatBean> cats1) {
        List<JournalCatBean> rstList  = new ArrayList<JournalCatBean>();
        while(cats1.size()>0){
            JournalCatBean curMenu = cats1.get(0);
            if(!rstList.contains(curMenu)) {
                rstList.add(curMenu);
            }
            cats1.remove(0);
            List<JournalCatBean> children = this.getChildren(cats1, curMenu.getJournal_cat_id());
            curMenu.setChildren(children);
        }
        return rstList;
    }

    private List<JournalCatBean> getChildren(List<JournalCatBean> allList, Long pid){
        List<JournalCatBean> children = new ArrayList<JournalCatBean>();
        List<JournalCatBean> copyList = new ArrayList<JournalCatBean>();
        copyList.addAll(allList);
        for(JournalCatBean curMenu :copyList){
            if(curMenu.getPid().equals(pid)){
                curMenu.setChildren(this.getChildren(allList, curMenu.getJournal_cat_id()));
                children.add(curMenu);
                allList.remove(curMenu);
            }
        }
        return children;
    }

    @Override
    public List<JournalCat> selectSiteCatsByOrgId(Long org_id) {
        return journalCatMapper.selectSiteCatsByOrgId(org_id);
    }

    @Override
    public List<JournalCatOrgBean> getAddOrgs() {
        return journalCatMapper.getAddOrgs();
    }

    @Override
    public Object getAddOrgsPageQuery(Sort sort, PageConditionBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order == null){
            PageHelper.orderBy(order);
        }
        List<JournalCatOrgBean> list = journalCatMapper.getAddOrgs();
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public List<JournalCatOrgBean> getRemoveOrgs(Long journal_cat_id) {
        return journalCatMapper.getRemoveOrgs(journal_cat_id);
    }

    @Override
    public Object getRemoveOrgsPageQuery(Sort sort, FindCatOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order == null){
            PageHelper.orderBy(order);
        }
        List<JournalCatOrgBean> list = journalCatMapper.getRemoveOrgs(bean.getJournal_cat_id());
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "期刊机构分类关系")
    public void addOrg(Long video_cat_id, Long org_id) {
        JournalCat journalCat = journalCatMapper.selectByCatId(video_cat_id);
        String cat_path = journalCat.getJournal_cat_path();

        String[] cat_id_arr = cat_path.split("\\|");

        for(int i = 1; i<cat_id_arr.length; i++){
            JournalCatOrgRel rel = journalCatOrgRelMapper.selectByOrgIdAndCatId(org_id, Long.valueOf(cat_id_arr[i]));
            if(rel == null){
                rel = new JournalCatOrgRel(org_id, Long.valueOf(cat_id_arr[i]), System.currentTimeMillis(), 1, Long.valueOf(cat_id_arr[i]), 2, new Date(), new Date(), 2);
                journalCatOrgRelMapper.insert(rel);
                journalCatMapper.updateOrgCount(Long.valueOf(cat_id_arr[i]), 1);
            }else{
                if(rel.getIs_delete() == 1){
                    journalCatOrgRelMapper.updateIsDelete(rel.getRel_id(), 2);
                    journalCatMapper.updateOrgCount(Long.valueOf(cat_id_arr[i]), 1);
                }
            }
        }
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "期刊机构分类关系")
    public void removeOrg(Long video_cat_id, Long org_id) {
        JournalCat journalCat = journalCatMapper.selectByCatId(video_cat_id);
        List<Long> video_cat_ids = journalCatMapper.getCatIdsByFullPath(journalCat.getJournal_cat_path());
        for(Long cat_id : video_cat_ids){
            JournalCatOrgRel rel = journalCatOrgRelMapper.selectByOrgIdAndCatId(org_id, cat_id);
            if(rel != null && rel.getIs_delete() == 2){
                journalCatOrgRelMapper.updateIsDelete(rel.getRel_id(), 1);
                journalCatMapper.updateOrgCount(cat_id, -1);
            }
        }
    }

    @Override
    public List<JournalCatBean> getOwnerCats(Long org_id, String video_cat_path) {
        return journalCatMapper.getOwnerCats(org_id, video_cat_path);
    }

    @Override
    public Object getOrgsPageQuery(Sort sort, FindCatOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<JournalCatOrgBean> list = journalCatMapper.getOrgQuery(bean);
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public List<JournalCatBean> getOffLineNumList(Long orgid, String oldtimestamp,Object...otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        return journalCatMapper.getOffLineNumList(orgid, oldtimestamp, num, size);
    }

    @Override
    public Integer hasOffLine(Long orgid, String timev,Object...otherparam) {
        Integer checknum = journalCatMapper.checkOffLineNum(orgid,timev);
        if(checknum==null ) {
            checknum =0;
        }
        return checknum;
    }

    @Override
    public Long getCatId(Long journal_cat_id,Long org_id){
        return journalCatMapper.getCatId(journal_cat_id,org_id);
    }

}
