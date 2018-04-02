package com.cjsz.tech.journal.service;

import com.cjsz.tech.beans.FileForm;
import com.cjsz.tech.journal.beans.FindPeriodicalBean;
import com.cjsz.tech.journal.domain.PeriodicalChild;
import com.cjsz.tech.journal.domain.PeriodicalRepo;
import com.cjsz.tech.journal.domain.UnPeriodical;
import com.cjsz.tech.system.service.IOfflineService;
import com.cjsz.tech.utils.JsonResult;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.List;


/**
 * Created by Administrator on 2016/11/22 0022.
 */
public interface PeriodicalRepoService extends IOfflineService {

    void uploadPeriodicals(List<FileForm> fileForms);

    PeriodicalRepo findByFileName(String perioical_name);

    boolean saveRepo(FileForm fileForm);

    boolean saveRepoNew(FileForm fileForm, UnPeriodical unper);

    boolean saveRepoToImg(FileForm fileForm, UnPeriodical unper) throws IOException;

    List<PeriodicalRepo> selectByCatIds(String catIds);

    PeriodicalRepo selectById(Long journal_id);

    void updateRepo(PeriodicalRepo repo);

    JsonResult updateStatus(PeriodicalRepo repo);

    //期刊分页列表
    Object pageQuery(FindPeriodicalBean bean);

    //获取阅读期刊图片（一页）
    List<PeriodicalChild> getImg(Long periodical_id);

    //获取相同分类期刊
    List<PeriodicalRepo> findSameCatperiodicals(Long periodical_id,Long org_id,Long periodical_cat_id,Integer limit);

    //获取期刊的分类ID
    Long getCatId(Long periodical_id);

    //获取期刊列表（同系列显示最新一本）
    Object webGetList(FindPeriodicalBean bean,Sort sort);

    //获取期刊列表（同系列显示最新一本）
    Object getList(FindPeriodicalBean bean,Sort sort);


    //获取相同系列期刊
    List<PeriodicalRepo> getSuggestList(Long periodical_id,Long org_id);

    //获取相同系列期刊(后台)
    List<PeriodicalRepo> getSameList(String series_name,Long org_id);

    //分页查询本机构期刊列表（前台）
    Object stiePageQuery(Sort sort , FindPeriodicalBean bean);

    PeriodicalRepo savePeriodicalRepo(FileForm fileForm);

    PeriodicalRepo savePeriodicalRepoNew(FileForm fileForm, UnPeriodical unper);

    Integer getCountByOrgId(Long org_id);

    void delectPeriodicalChild(Long periodical_id);

    void updateRepoStatusAndPages(int Status, int num, Long periodical_id);

    void insertList(List<PeriodicalChild> list);

    void setPeriodicalAllToCat(Long periodical_cat_id, String serises_name);

    void setPeriodicalAllToRemark(String periodical_remark, String serises_name);

    List<PeriodicalRepo>  getSameBySNameAll(String serises_name);

    //修改期刊系列发布状态
    void updateEnabled(Integer enabled,String series_name);
}
