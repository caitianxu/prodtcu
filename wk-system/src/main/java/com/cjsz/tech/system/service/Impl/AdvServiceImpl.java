package com.cjsz.tech.system.service.Impl;

import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.beans.AdvOrgListBean;
import com.cjsz.tech.system.conditions.AdvCondition;
import com.cjsz.tech.system.domain.Adv;
import com.cjsz.tech.system.domain.AdvOrgRel;
import com.cjsz.tech.system.domain.Organization;
import com.cjsz.tech.system.mapper.AdvMapper;
import com.cjsz.tech.system.mapper.AdvOrgRelMapper;
import com.cjsz.tech.system.mapper.OrgExtendMapper;
import com.cjsz.tech.system.mapper.OrganizationMapper;
import com.cjsz.tech.system.service.AdvService;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/22 0022.
 */
@Service
public class AdvServiceImpl implements AdvService {

    @Autowired
    private AdvMapper advMapper;
    @Autowired
    private OrgExtendMapper orgExtendMapper;
    @Autowired
    private AdvOrgRelMapper advOrgRelMapper;
    @Autowired
    private OrganizationMapper organizationMapper;

    @Override
    //分页列表
    public Object pageQuery(Sort sort,  AdvCondition advCondition) {
        PageHelper.startPage(advCondition.getPageNum(), advCondition.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<Adv> advs = advMapper.getAdvList( advCondition.getSearchText(), advCondition.getAdv_cat_id(),advCondition.getOrg_id());
        for(Adv adv:advs){
            Integer org_count = advOrgRelMapper.selectOrgCount(adv.getAdv_id());
            adv.setOrg_count(org_count);
        }
        PageList pageList = new PageList(advs, null);
        return pageList;
    }

    @Override
    //广告分类Ids查找广告
    public List<Adv> selectAdvByCatIds(String catIdsStr) {
        return advMapper.selectAdvByCatIds(catIdsStr);
    }

    @Override
    //添加广告
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "广告")
    public void saveAdv(Adv adv) {
        advMapper.insert(adv);
        //非长江创建广告创建分配关系
        if (adv.getOrg_id() != 1) {
            AdvOrgRel rel = new AdvOrgRel();
            rel.setOrg_id(adv.getOrg_id());
            rel.setAdv_id(adv.getAdv_id());
            rel.setUpdate_time(new Date());
            rel.setCreate_time(new Date());
            rel.setOrder_weight(System.currentTimeMillis());
            rel.setIs_show(1);
            rel.setIs_delete(2);
            advOrgRelMapper.insert(rel);
        }
    }

    @Override
    //修改广告
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "广告")
    public void updateAdv(Adv adv) {
        advMapper.updateByPrimaryKey(adv);
        //广告停用则关掉所有显示开关
        if (adv.getEnabled() ==2){
            advMapper.updateShow(adv.getAdv_id());
        }
    }

    @Override
    //根据广告Id删除广告
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "广告")
    public void deleteByIds(String advIdsStr) {
        advMapper.deleteByIds(advIdsStr);
    }

    @Override
    //根据广告分类Id删除广告
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "广告")
    public void deleteAdvByCats(String catIdsStr){
        advMapper.deleteAdvByCats(catIdsStr);
    }

    @Override
    //广告Ids查找广告
    public List<Adv> selectAdvByIds(String advIdsStr) {
        return advMapper.selectAdvByIds(advIdsStr);
    }

    @Override
    //广告Id查找广告
    public Adv selectAdvById(Long adv_id) {
        return advMapper.selectAdvById(adv_id);
    }

    @Override
    //获取分类下有限数量的广告
    public List<Adv> selectAdvsByCatIdAndCount(Long adv_cat_id, Integer count) {
        return advMapper.selectAdvsByCatIdAndCount(adv_cat_id, count);
    }

    @Override
    public Object getOrgList(AdvOrgListBean bean,Sort sort) {
        PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<AdvOrgListBean> result = advMapper.getOrgList(bean);
        PageList pageList = new PageList(result,null);
        return pageList;
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD,action_log_module_name = "广告分配关系")
    public void addOrg(Long adv_id, Long org_id) {
        AdvOrgRel rel = advOrgRelMapper.selectByOrgIdAndAdvId(org_id,adv_id);
        if (rel == null){
            rel = new AdvOrgRel(org_id,adv_id,System.currentTimeMillis(),1,new Date(),new Date(),2);
            advOrgRelMapper.insert(rel);
        }else {
            if (rel.getIs_delete() == 1){
                advOrgRelMapper.updateIsDelete(rel.getRel_id(),2);
            }
        }

    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "广告分配关系")
    public void removeOrg(Long adv_id, Long org_id) {
        AdvOrgRel rel = advOrgRelMapper.selectByOrgIdAndAdvId(org_id,adv_id);
        if ((rel != null && rel.getIs_delete() == 2)){
            advOrgRelMapper.updateIsDelete(rel.getRel_id(),1);
        }
    }

    @Override
    public void updateShow(Adv bean,Long org_id) {
       advOrgRelMapper.updateIsShow(bean.getAdv_id(),org_id,bean.getIs_show());
    }

    @Override
    public void saveRelByAllOrg(Adv adv) {
        List<Organization> orgList = organizationMapper.selectAllOrg();
        List<AdvOrgRel> list = new ArrayList<>();
        for (Organization org:orgList){
            AdvOrgRel rel = new AdvOrgRel();
            rel.setAdv_id(adv.getAdv_id());
            rel.setCreate_time(new Date());
            rel.setIs_delete(2);
            rel.setIs_show(1);
            rel.setOrder_weight(System.currentTimeMillis());
            rel.setOrg_id(org.getOrg_id());
            rel.setUpdate_time(new Date());
            list.add(rel);
        }
        advOrgRelMapper.insertList(list);
    }

    @Override
    public void updateOrder(Long adv_id, Long order_weight, Long org_id) {
        advOrgRelMapper.updateOrder(adv_id,order_weight,org_id);
    }

    @Override
    public List<Adv> selectAdvsByOrgIdAndCatIdNum(Long adv_cat_id, Long org_id) {
        return advMapper.selectAdvsByOrgIdAndCatIdNum(adv_cat_id,org_id);
    }

    @Override
    public Integer hasOffLine(Long orgid,String time,Object...otherparam) {
        Integer checknum = advMapper.checkOffLineNum(orgid,time);
        if(checknum==null ) {
            checknum =0;
        }
        return checknum;
    }

    @Override
    public List<Adv> getOffLineNumList(Long orgid,String timev,Object...otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }

        List<Adv> result = advMapper.getOffLineNumList(orgid,timev, num, size);
        for (Adv adv:result){
            if (adv.getIs_show() == 2){
                adv.setEnabled(2);
            }else if (adv.getEnabled() == 1 && adv.getIs_show() == 1){
                adv.setEnabled(1);
            }
        }
        return result;
    }

}
