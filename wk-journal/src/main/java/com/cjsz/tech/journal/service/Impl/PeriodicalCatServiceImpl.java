package com.cjsz.tech.journal.service.Impl;

import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.journal.beans.*;
import com.cjsz.tech.journal.domain.PeriodicalCat;
import com.cjsz.tech.journal.domain.PeriodicalCatOrgRel;
import com.cjsz.tech.journal.mapper.PeriodicalCatMapper;
import com.cjsz.tech.journal.mapper.PeriodicalCatOrgRelMapper;
import com.cjsz.tech.journal.service.PeriodicalCatService;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.Constants;
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
public class PeriodicalCatServiceImpl implements PeriodicalCatService {

    @Autowired
    PeriodicalCatMapper periodicalCatMapper;

    @Autowired
    PeriodicalCatOrgRelMapper periodicalCatOrgRelMapper;


    @Override
    public List<PeriodicalCatBean> getCatList(Long org_id) {
        return periodicalCatMapper.getCatsByOrgId(org_id);
    }

    @Override
    public List<PeriodicalCat> selectByCatName(String periodical_cat_name) {
        return periodicalCatMapper.selectByCatName(periodical_cat_name);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "期刊分类")
    @Transactional
    //保存分类
    public boolean saveCat(PeriodicalCatBean bean) {
        String cat_path;    //分类的层次路径
        if (bean.getPeriodical_cat_pid() != null && bean.getPeriodical_cat_pid() != 0) {
            PeriodicalCat p_cat = periodicalCatMapper.selectByPrimaryKey(bean.getPeriodical_cat_pid());
            if (null == p_cat) {
                return false;
            }
            cat_path = p_cat.getPeriodical_cat_path();
        } else {
            bean.setPeriodical_cat_pid(0L);
            cat_path = "0|";
        }
        //分类表数据添加
        //启用，不删除，机构数1
        PeriodicalCat periodicalCat = new PeriodicalCat(bean.getPeriodical_cat_pid(), bean.getPeriodical_cat_name(), null,
                Constants.ENABLE, 1, bean.getRemark(), Constants.NOT_DELETE, bean.getOrder_weight(), new Date(), new Date());
        periodicalCatMapper.insert(periodicalCat);
        Long cat_id = periodicalCat.getPeriodical_cat_id();
        //更新层次路径
        cat_path = cat_path + cat_id + "|";
        periodicalCatMapper.updateCatPathByCatId(cat_id, cat_path);
        //关系表数据添加
        //显示
        PeriodicalCatOrgRel rel = new PeriodicalCatOrgRel(1L, cat_id, bean.getOrder_weight(), 1, new Date(), new Date(), Constants.NOT_DELETE);
        periodicalCatOrgRelMapper.insert(rel);
        return true;
    }

    @Override
    public List<PeriodicalCat> selectOtherByCatName(String cat_name, Long cat_id) {
        return periodicalCatMapper.selectOtherByCatName(cat_name, cat_id);
    }

    @Override
    public PeriodicalCat selectByCatId(Long cat_id) {
        return periodicalCatMapper.selectByPrimaryKey(cat_id);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "期刊分类")
    @Transactional
    public boolean updateCat(PeriodicalCatBean bean, Long org_id) {
        PeriodicalCatOrgRel rel = periodicalCatOrgRelMapper.selectByPrimaryKey(bean.getRel_id());
        if (null == rel) {
            return false;
        }
        if (org_id.intValue() == 1) {
            PeriodicalCat cat = periodicalCatMapper.selectByPrimaryKey(bean.getPeriodical_cat_id());
            if (null == cat) {
                return false;
            }
            cat.setPeriodical_cat_name(bean.getPeriodical_cat_name());
            cat.setRemark(bean.getRemark());
            cat.setUpdate_time(new Date());
            //更新层次路径：分类层次修改，则更新层次路径
            if (!bean.getPeriodical_cat_pid().equals(cat.getPeriodical_cat_pid())) {
                String cat_path;    //分类的层次路径
                if (bean.getPeriodical_cat_pid() != null && bean.getPeriodical_cat_pid() != 0) {
                    PeriodicalCat p_cat = periodicalCatMapper.selectByPrimaryKey(bean.getPeriodical_cat_pid());
                    if (null == p_cat) {
                        return false;
                    }
                    cat_path = p_cat.getPeriodical_cat_path();
                    cat.setPeriodical_cat_pid(bean.getPeriodical_cat_pid());
                } else {
                    cat.setPeriodical_cat_pid(0L);
                    cat_path = "0|";
                }
                periodicalCatMapper.updateByPrimaryKey(cat);
                String old_full_path = cat.getPeriodical_cat_path();        //修改前层次路径
                String new_full_path = cat_path + cat.getPeriodical_cat_id() + "|";    //修改后层次路径
                //更新当前修改的分类以及下级所有分类的层次路径：将层次路径中包含old_full_path的，更新为new_full_path
                periodicalCatMapper.updateFullPath(old_full_path, new_full_path);
            } else {
                periodicalCatMapper.updateByPrimaryKey(cat);
            }
        }
        //只能更新关系表
        if (rel.getOrder_weight() == null || !rel.getOrder_weight().equals(bean.getOrder_weight())) {
            periodicalCatOrgRelMapper.updateOrderWeightById(bean.getOrder_weight(), bean.getRel_id());
        }
        return true;
    }

    @Override
    public List<String> getFullPathsByCatIds(String cat_ids) {
        return periodicalCatMapper.getFullPathsByCatIds(cat_ids);
    }

    @Override
    public List<Long> getCatIdsByFullPath(String full_path) {
        return periodicalCatMapper.getCatIdsByFullPath(full_path);
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "期刊分类")
    public void deleteByCatIds(String catIds) {
        //删除期刊分类机构关系
        periodicalCatOrgRelMapper.deleteRelsByCatIds(catIds);
        //删除期刊分类
        periodicalCatMapper.deleteJournalCatsByIds(catIds);
    }

    //更新分类显示状态
    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "期刊分类")
    public boolean updateShow(PeriodicalCatBean bean) {
        //当前分类显示，则上级全部显示；当前取消显示，下级取消显示
        //is_show:是否显示(1:显示,2:不显示)
        String cat_path = bean.getPeriodical_cat_path();
        if (StringUtils.isEmpty(cat_path) || null == bean.getOrg_id() || null == bean.getIs_show()) {
            return false;
        }
        if (bean.getIs_show() == 1) {
            //自己和上级全部显示
            String cat_ids = StringUtils.join(cat_path.split("\\|"), ",");
            periodicalCatOrgRelMapper.updateShowByCatIds(bean.getOrg_id(), cat_ids);
        } else if (bean.getIs_show() == 2) {
            //自己和下级全部不显示
            periodicalCatOrgRelMapper.updateShowByCatPath(bean.getOrg_id(), cat_path);
        }
        return true;
    }

    //启用，停用
    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "期刊分类")
    public boolean updateEnabled(PeriodicalCatBean bean) {
        //当前分类启用，则上级全部启用；当前取消启用，下级取消启用，取消显示(一般机构不具备该功能)
        //是否启用(1:启用,2:不启用)
        String cat_ids;
        String cat_path = bean.getPeriodical_cat_path();
        if (StringUtils.isEmpty(cat_path) || null == bean.getOrg_id() || null == bean.getEnabled()) {
            return false;
        }
        if (bean.getEnabled() == 1) {
            //自己和上级全部启用
            cat_ids = StringUtils.join(cat_path.split("\\|"), ",");
            periodicalCatMapper.updateEnabledByCatIds(cat_ids);
        } else {
            //自己和下级全部不启用
            periodicalCatMapper.updateEnabledByCatPath(cat_path);
            //自己和下级全部不显示
            periodicalCatOrgRelMapper.updateAllShowByCatPath(cat_path,bean.getOrg_id());
        }
        return true;
    }

    @Override
    //将期刊分类转化为树形结构
    public List<PeriodicalCatBean> getCatTree(List<PeriodicalCatBean> cats1) {
        List<PeriodicalCatBean> rstList = new ArrayList<PeriodicalCatBean>();
        while (cats1.size() > 0) {
            PeriodicalCatBean curMenu = cats1.get(0);
            if (!rstList.contains(curMenu)) {
                rstList.add(curMenu);
            }
            cats1.remove(0);
            List<PeriodicalCatBean> children = this.getChildren(cats1, curMenu.getPeriodical_cat_id());
            curMenu.setChildren(children);
        }
        return rstList;
    }

    private List<PeriodicalCatBean> getChildren(List<PeriodicalCatBean> allList, Long pid) {
        List<PeriodicalCatBean> children = new ArrayList<PeriodicalCatBean>();
        List<PeriodicalCatBean> copyList = new ArrayList<PeriodicalCatBean>();
        copyList.addAll(allList);
        for (PeriodicalCatBean curMenu : copyList) {
            if (curMenu.getPeriodical_cat_pid().equals(pid)) {
                curMenu.setChildren(this.getChildren(allList, curMenu.getPeriodical_cat_id()));
                children.add(curMenu);
                allList.remove(curMenu);
            }
        }
        return children;
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "期刊机构分类关系")
    public boolean addOrg(Long periodical_cat_id, Long org_id) {
        PeriodicalCat cat = periodicalCatMapper.selectByPrimaryKey(periodical_cat_id);
        if (null == cat) {
            return false;
        }
        String cat_path = cat.getPeriodical_cat_path();
        if (StringUtils.isEmpty(cat_path)) {
            return false;
        }
        String[] cat_id_arr = cat_path.split("\\|");
        List<PeriodicalCatOrgRel> rel_list = new ArrayList<>();
        List<Long> catids = new ArrayList<>();
        List<Long> rel_ids = new ArrayList<>();
        for (String catId : cat_id_arr) {
            if (catId.equals("0")) {
                continue;
            }
            Long cat_id = Long.valueOf(catId);
            PeriodicalCatOrgRel rel = periodicalCatOrgRelMapper.selectByOrgIdAndCatId(org_id, cat_id);
            if (rel == null) {
                rel = new PeriodicalCatOrgRel(org_id, cat_id, System.currentTimeMillis(), 1, new Date(), new Date(), 2);
                rel_list.add(rel);
                catids.add(cat_id);
            } else {
                if (rel.getIs_delete() == Constants.IS_DELETE) {
                    rel_ids.add(rel.getRel_id());
                    catids.add(cat_id);
                }
            }
        }
        if (rel_list.size() > 0) {
            periodicalCatOrgRelMapper.insertList(rel_list);
        }
        if (catids.size() > 0) {
            String cat_ids = StringUtils.join(catids, ",");
            periodicalCatMapper.updateOrgCount(cat_ids, 1);
        }
        if (rel_ids.size() > 0) {
            String relIds = StringUtils.join(rel_ids, ",");
            periodicalCatOrgRelMapper.updateIsDelete(relIds, 2);
        }
        return true;
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "期刊机构分类关系")
    public boolean removeOrg(Long periodical_cat_id, Long org_id) {
        PeriodicalCat cat = periodicalCatMapper.selectByPrimaryKey(periodical_cat_id);
        if (null == cat) {
            return false;
        }
        String cat_path = cat.getPeriodical_cat_path();
        if (StringUtils.isEmpty(cat_path)) {
            return false;
        }
        List<Long> catids = new ArrayList<>();
        List<Long> rel_ids = new ArrayList<>();
        List<Long> cat_ids = periodicalCatMapper.getCatIdsByFullPath(cat_path);
        for (Long cat_id : cat_ids) {
            PeriodicalCatOrgRel rel = periodicalCatOrgRelMapper.selectByOrgIdAndCatId(org_id, cat_id);
            if (rel != null && rel.getIs_delete() == 2) {
                rel_ids.add(rel.getRel_id());
                catids.add(cat_id);
            }
        }
        if (catids.size() > 0) {
            String ids = StringUtils.join(catids, ",");
            periodicalCatMapper.updateOrgCount(ids, -1);
        }
        if (rel_ids.size() > 0) {
            String relIds = StringUtils.join(rel_ids, ",");
            periodicalCatOrgRelMapper.updateIsDelete(relIds, 1);
        }
        return true;
    }

    @Override
    public Object getOrgsPageQuery(Sort sort, FindNewCatOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<PeriodicalCatOrgBean> list = periodicalCatMapper.getOrgQuery(bean);
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public List<PeriodicalCat> getOffLineNumList(Long orgid, String oldtimestamp,Object...otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        return periodicalCatMapper.getOffLineNumList(orgid, oldtimestamp, num, size);
    }

    @Override
    public Integer hasOffLine(Long orgid, String timev,Object...otherparam) {
        Integer checknum = periodicalCatMapper.checkOffLineNum(orgid,timev);
        if(checknum==null ) {
            checknum =0;
        }
        return checknum;
    }


    @Override
    public List<PeriodicalCatBean> catList(Long org_id) {
        return periodicalCatMapper.getCatTree(org_id);
    }

    @Override
    public List<PeriodicalCat> selectSiteCatsByOrgId(Long org_id) {
        return periodicalCatMapper.selectSiteCatsByOrgId(org_id);
    }

    @Override
    public List<PeriodicalCatBean> getAllCats(Long org_id) {
        return periodicalCatMapper.getAllCats(org_id);
    }

    @Override
    public void orderByOrg(Long periodical_cat_id, Long order_weight,Long org_id) {
        periodicalCatOrgRelMapper.orderByOrg(periodical_cat_id,order_weight, org_id);
    }
}
