package com.cjsz.tech.journal.service;

import com.cjsz.tech.journal.beans.FindCatOrgBean;
import com.cjsz.tech.journal.beans.FindNewCatOrgBean;
import com.cjsz.tech.journal.beans.JournalCatBean;
import com.cjsz.tech.journal.beans.PeriodicalCatBean;
import com.cjsz.tech.journal.domain.PeriodicalCat;
import com.cjsz.tech.system.service.IOfflineService;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface PeriodicalCatService extends IOfflineService{

    List<PeriodicalCatBean> getCatList(Long org_id);

    List<PeriodicalCat> selectByCatName(String periodical_cat_name);

    boolean saveCat(PeriodicalCatBean bean);

    List<PeriodicalCat> selectOtherByCatName(String cat_name, Long cat_id);

    PeriodicalCat selectByCatId(Long cat_id);

    boolean updateCat(PeriodicalCatBean bean, Long org_id);

    List<String> getFullPathsByCatIds(String cat_ids);

    List<Long> getCatIdsByFullPath(String full_path);

    void deleteByCatIds(String catIds);

    boolean updateShow(PeriodicalCatBean bean);

    boolean updateEnabled(PeriodicalCatBean bean);

    List<PeriodicalCatBean> getCatTree(List<PeriodicalCatBean> journalCats);

    boolean addOrg(Long periodical_cat_id, Long org_id);

    boolean removeOrg(Long periodical_cat_id, Long org_id);

    Object getOrgsPageQuery(Sort sort, FindNewCatOrgBean bean);

    List<PeriodicalCatBean> catList(Long org_id);

    List<PeriodicalCat> selectSiteCatsByOrgId(Long org_id);

    List<PeriodicalCatBean> getAllCats(Long org_id);

    //修改机构期刊分类排序
    void orderByOrg(Long periodical_cat_id, Long order_weight,Long org_id);
}
