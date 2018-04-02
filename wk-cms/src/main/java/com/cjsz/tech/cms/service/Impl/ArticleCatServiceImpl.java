package com.cjsz.tech.cms.service.Impl;

import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.cms.beans.ArticleCatBean;
import com.cjsz.tech.cms.beans.ArticleCatOrgBean;
import com.cjsz.tech.cms.beans.FindCatOrgBean;
import com.cjsz.tech.cms.domain.ArticleCat;
import com.cjsz.tech.cms.domain.ArticleCatOrgRel;
import com.cjsz.tech.cms.mapper.ArticleCatMapper;
import com.cjsz.tech.cms.mapper.ArticleCatOrgRelMapper;
import com.cjsz.tech.cms.mapper.ArticleMapper;
import com.cjsz.tech.cms.service.ArticleCatService;
import com.cjsz.tech.core.page.ConditionOrderUtil;
import com.cjsz.tech.core.page.PageList;
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
public class ArticleCatServiceImpl implements ArticleCatService {

    @Autowired
    ArticleCatMapper articleCatMapper;

    @Autowired
    ArticleCatOrgRelMapper articleCatOrgRelMapper;

    @Autowired
    ArticleMapper articleMapper;


    @Override
    //获取组织的全部资讯分类
    public List<ArticleCatBean> getCats(Long org_id) {
//        if(org_id == 1){
//            return articleCatMapper.getCats(org_id);
//        }else{
//            return articleCatMapper.getEnabledCats(org_id);
//        }
        return articleCatMapper.getEnabledCats(org_id);
    }

    @Override
    //资讯分类树(本机构)
    public List<ArticleCatBean> getOrgCats(Long org_id) {
//        return articleCatMapper.getOrgCats(org_id);
        return articleCatMapper.getEnabledCats(org_id);
    }

    @Override
    //分类名称重复(分类名查找)
    public List<ArticleCat> selectByCatName(Long root_org_id, String article_cat_name) {
        return articleCatMapper.selectByCatName(root_org_id, article_cat_name);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资讯分类")
    @Transactional
    //保存分类
    public ArticleCatBean saveCat(ArticleCatBean bean, Long org_id) {
        String cat_path = "";	//分类的层次路径
        if(bean.getPid() != null && bean.getPid() != 0 ){
            ArticleCat p_cat = articleCatMapper.selectByCatId(bean.getPid());
            cat_path = p_cat.getArticle_cat_path();
        }else{
            bean.setPid(0L);
            cat_path = "0|";
        }
        //分类表数据添加
        //启用，不删除，机构数1
        ArticleCat articleCat = new ArticleCat(org_id, bean.getPid(), bean.getArticle_cat_name(), 1, bean.getRemark(), 2, new Date(), new Date(), 1);
        articleCatMapper.insert(articleCat);
        //更新层次路径
        cat_path = cat_path + articleCat.getArticle_cat_id() + "|";
        articleCatMapper.updateCatPathByCatId(articleCat.getArticle_cat_id(), cat_path);
        //关系表数据添加
        //显示，自建
        ArticleCatOrgRel rel = new ArticleCatOrgRel(org_id, articleCat.getArticle_cat_id(), bean.getOrder_weight(), 1, articleCat.getArticle_cat_id(), 1, new Date(), new Date(), 2);
        articleCatOrgRelMapper.insert(rel);

        Long cat_id = bean.getPid();
        List<ArticleCatOrgRel> relList = articleCatOrgRelMapper.selectListByCatId(cat_id);
        List<ArticleCatOrgRel> articleCatOrgRels = new ArrayList<ArticleCatOrgRel>();
        for(ArticleCatOrgRel catOrgRel : relList){
            if(!catOrgRel.getOrg_id().equals(org_id)){
                ArticleCatOrgRel articleCatOrgRel = new ArticleCatOrgRel(catOrgRel.getOrg_id(), articleCat.getArticle_cat_id(), bean.getOrder_weight(), 1, articleCat.getArticle_cat_id(), 2, new Date(), new Date(), 2);
                articleCatOrgRels.add(articleCatOrgRel);
            }
        }
        if(articleCatOrgRels.size()>0){
            articleCatOrgRelMapper.insertList(articleCatOrgRels);
            articleCatMapper.updateOrgCount(articleCat.getArticle_cat_id(), articleCatOrgRels.size());
        }
        return articleCatOrgRelMapper.seletInfoByOrgIdAndCatId(org_id, articleCat.getArticle_cat_id());
    }

    @Override
    //分类名称重复(分类名查找不包括本身)
    public List<ArticleCat> selectOtherByCatName(Long root_org_id, String article_cat_name, Long article_cat_id) {
        return articleCatMapper.selectOtherByCatName(root_org_id, article_cat_name, article_cat_id);
    }

    @Override
    //根据Id查找分类
    public ArticleCat selectByCatId(Long article_cat_id) {
        return articleCatMapper.selectByCatId(article_cat_id);
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资讯分类")
    @Transactional
    //修改分类
    public ArticleCatBean updateCat(ArticleCatBean bean, Long org_id) {
        ArticleCatOrgRel rel = articleCatOrgRelMapper.selectById(bean.getRel_id());
        //是否自建(1:自建;2:分配)
        if(bean.getSource_type() == 1){
            ArticleCat cat = articleCatMapper.selectByCatId(bean.getArticle_cat_id());
            cat.setArticle_cat_name(bean.getArticle_cat_name());
            cat.setRemark(bean.getRemark());
            cat.setUpdate_time(new Date());
            //更新层次路径：分类层次修改，则更新层次路径
            if(!bean.getPid().equals(cat.getPid())){
                String cat_path = "";	//分类的层次路径
                if(bean.getPid() != null && bean.getPid() != 0 ){
                    ArticleCat p_cat = articleCatMapper.selectOrgCatByCatId(org_id, bean.getPid());
                    cat_path = p_cat.getArticle_cat_path();
                    cat.setPid(bean.getPid());
                }else{
                    cat.setPid(0L);
                    cat_path = "0|";
                }
                articleCatMapper.updateByPrimaryKey(cat);
                String old_full_path = bean.getArticle_cat_path();		//修改前层次路径
                String new_full_path = cat_path + bean.getArticle_cat_id() + "|";	//修改后层次路径
                //更新当前修改的分类以及下级所有分类的层次路径：将层次路径中包含old_full_path的，更新为new_full_path
                articleCatMapper.updateFullPath(old_full_path, new_full_path, org_id);
            }else{
                articleCatMapper.updateByPrimaryKey(cat);
            }
        }
        //只能更新关系表
        if(rel.getOrder_weight() == null || !rel.getOrder_weight().equals(bean.getOrder_weight())){
            articleCatOrgRelMapper.updateOrderById(bean.getOrder_weight(), bean.getRel_id());
        }
        return articleCatOrgRelMapper.seletInfoByOrgIdAndCatId(org_id, bean.getArticle_cat_id());
    }

    @Override
    @Transactional
    //启用状态
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资讯分类")
    public void updateEnabled(ArticleCatBean bean) {
        //当前分类启用，则上级全部启用；当前取消启用，下级取消启用，取消显示(一般机构不具备该功能)
        //是否启用(1:启用,2:不启用)
        String cat_ids = "";

        if(bean.getEnabled() == 1){
            //自己和上级全部启用
            String cat_path = bean.getArticle_cat_path();
            cat_ids = StringUtils.join(cat_path.split("\\|"), ",");
            articleCatMapper.updateEnabledByCatIds(bean.getOrg_id(), cat_ids);
        }else{
            //自己和下级全部不启用
            articleCatMapper.updateEnabledByCatPath(bean.getOrg_id(), bean.getArticle_cat_path());
            //自己和下级全部不显示
            articleCatMapper.updateShowByCatPath(bean.getOrg_id(), bean.getArticle_cat_path());
        }
    }

    @Override
    @Transactional
    //显示状态
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资讯分类")
    public void updateShow(ArticleCatBean bean) {
        //当前分类显示，则上级全部显示；当前取消显示，下级取消显示
        //is_show:是否显示(1:显示,2:不显示)
        String cat_ids = "";
        if(bean.getIs_show() == 1){
            //自己和上级全部显示
            String cat_path = bean.getArticle_cat_path();
            cat_ids = StringUtils.join(cat_path.split("\\|"), ",");
            articleCatMapper.updateShowByCatIds(bean.getOrg_id(), cat_ids);
        }else if(bean.getIs_show() == 2){
            //自己和下级全部不显示
            articleCatMapper.updateShowByCatPath(bean.getOrg_id(), bean.getArticle_cat_path());
        }
    }

    @Override
    //查询机构的资讯分类
    public List<Long> selectOrgCatIds(Long org_id) {
        return articleCatMapper.selectOrgCatIds(org_id);
    }

    @Override
    //通过cat_ids找到full_paths
    public List<String> getFullPathsByCatIds(String cat_ids){
        return articleCatMapper.getFullPathsByCatIds(cat_ids);
    }

    @Override
    public List<Long> getCatIdsByFullPath(String full_path) {
        return articleCatMapper.getCatIdsByFullPath(full_path);
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.DELETE, action_log_module_name = "资讯分类")
    public void deleteAllByCatIds(String cat_ids) {
        //删除资讯
        articleMapper.deleteByCatIds(cat_ids);
        //删除资讯分类机构关系
        articleCatOrgRelMapper.deleteRelsByIds(cat_ids);
        //删除资讯分类
        articleCatMapper.deleteArticleCatsByIds(cat_ids);
    }

    @Override
    //将资讯分类转化为树形结构
    public List<ArticleCatBean> selectTree(List<ArticleCatBean> cats1) {
        List<ArticleCatBean> rstList  = new ArrayList<ArticleCatBean>();
        while(cats1.size()>0){
            ArticleCatBean curMenu = cats1.get(0);
            if(!rstList.contains(curMenu)) {
                rstList.add(curMenu);
            }
            cats1.remove(0);
            List<ArticleCatBean> children = this.getChildren(cats1, curMenu.getArticle_cat_id());
            curMenu.setChildren(children);
        }
        return rstList;
    }

    private List<ArticleCatBean> getChildren(List<ArticleCatBean> allList, Long pid){
        List<ArticleCatBean> children = new ArrayList<ArticleCatBean>();
        List<ArticleCatBean> copyList = new ArrayList<ArticleCatBean>();
        copyList.addAll(allList);
        for(ArticleCatBean curMenu :copyList){
            if(curMenu.getPid().equals(pid)){
                curMenu.setChildren(this.getChildren(allList, curMenu.getArticle_cat_id()));
                children.add(curMenu);
                allList.remove(curMenu);
            }
        }
        return children;
    }

    @Override
    public List<ArticleCat> selectSiteCatsByOrgId(Long org_id) {
        return articleCatMapper.selectSiteCatsByOrgId(org_id);
    }

    @Override
    public List<ArticleCatOrgBean> getAddOrgs() {
        return articleCatMapper.getAddOrgs();
    }

    @Override
    public Object getAddOrgsPageQuery(Sort sort, PageConditionBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order == null){
            PageHelper.orderBy(order);
        }
        List<ArticleCatOrgBean> list = articleCatMapper.getAddOrgs();
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public List<ArticleCatOrgBean> getRemoveOrgs(Long article_cat_id) {
        return articleCatMapper.getRemoveOrgs(article_cat_id);
    }

    @Override
    public Object getRemoveOrgsPageQuery(Sort sort, FindCatOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order == null){
            PageHelper.orderBy(order);
        }
        List<ArticleCatOrgBean> list = articleCatMapper.getRemoveOrgs(bean.getArticle_cat_id());
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    @Transactional
    @SysActionLogAnnotation(action_type = SysActionLogType.ADD, action_log_module_name = "资讯机构分类关系")
    public void addOrg(Long video_cat_id, Long org_id) {
        ArticleCat articleCat = articleCatMapper.selectByCatId(video_cat_id);
        String cat_path = articleCat.getArticle_cat_path();

        String[] cat_id_arr = cat_path.split("\\|");

        for(int i = 1; i<cat_id_arr.length; i++){
            ArticleCatOrgRel rel = articleCatOrgRelMapper.selectByOrgIdAndCatId(org_id, Long.valueOf(cat_id_arr[i]));
            if(rel == null){
                rel = new ArticleCatOrgRel(org_id, Long.valueOf(cat_id_arr[i]), System.currentTimeMillis(), 1, Long.valueOf(cat_id_arr[i]), 2, new Date(), new Date(), 2);
                articleCatOrgRelMapper.insert(rel);
                articleCatMapper.updateOrgCount(Long.valueOf(cat_id_arr[i]), 1);
            }else{
                if(rel.getIs_delete() == 1){
                    articleCatOrgRelMapper.updateIsDelete(rel.getRel_id(), 2);
                    articleCatMapper.updateOrgCount(Long.valueOf(cat_id_arr[i]), 1);
                }
            }
        }
    }

    @Override
    @SysActionLogAnnotation(action_type = SysActionLogType.UPDATE, action_log_module_name = "资讯机构分类关系")
    public void removeOrg(Long video_cat_id, Long org_id) {
        ArticleCat articleCat = articleCatMapper.selectByCatId(video_cat_id);
        List<Long> video_cat_ids = articleCatMapper.getCatIdsByFullPath(articleCat.getArticle_cat_path());
        for(Long cat_id : video_cat_ids){
            ArticleCatOrgRel rel = articleCatOrgRelMapper.selectByOrgIdAndCatId(org_id, cat_id);
            if(rel != null && rel.getIs_delete() == 2){
                articleCatOrgRelMapper.updateIsDelete(rel.getRel_id(), 1);
                articleCatMapper.updateOrgCount(cat_id, -1);
            }
        }
    }

    @Override
    public List<ArticleCatBean> getOwnerCats(Long org_id, String video_cat_path) {
        return articleCatMapper.getOwnerCats(org_id, video_cat_path);
    }

    @Override
    public Object getOrgsPageQuery(Sort sort, FindCatOrgBean bean) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        String order = ConditionOrderUtil.prepareOrder(sort);
        if(order != null){
            PageHelper.orderBy(order);
        }
        List<ArticleCatOrgBean> list = articleCatMapper.getOrgQuery(bean);
        PageList pageList = new PageList(list, null);
        return pageList;
    }

    @Override
    public List<ArticleCatBean> getOffLineNumList(Long orgid, String oldtimestamp,Object...otherparam) {
        Integer num = 0;
        Integer size = 1000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        return articleCatMapper.getOffLineNumList(orgid, oldtimestamp, num, size);
    }

    @Override
    public Integer hasOffLine(Long orgid, String timev,Object...otherparam) {
        Integer checknum = articleCatMapper.checkOffLineNum(orgid,timev);
        if(checknum==null ) {
            checknum =0;
        }
        return checknum;
    }

    @Override
    public ArticleCat selectSourceByCatId(Long cat_id,Long org_id){
        return articleCatMapper.selectSourceById(cat_id,org_id);
    }

    @Override
    public List<ArticleCatBean> getAllCats(Long org_id) {
        return articleCatMapper.getAllCats(org_id);
    }

    @Override
    public void orderByOrg(Long article_cat_id, Long order_weight, Long org_id) {
        articleCatOrgRelMapper.orderByOrg(article_cat_id,order_weight,org_id);
    }
}
