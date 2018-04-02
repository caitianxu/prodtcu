package com.cjsz.tech.journal.service;

import com.cjsz.tech.journal.beans.FindJournalBean;
import com.cjsz.tech.journal.domain.Journal;
import com.cjsz.tech.system.service.IOfflineService;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/11/22 0022.
 */
public interface JournalService extends IOfflineService {

    /**
     * 分页查询本机构的期刊
     *
     * @param sort
     * @param bean
     * @return
     */
    public Object pageQuery(Sort sort, FindJournalBean bean);

    /**
     * 分页查询本机构的期刊(前台)
     *
     * @param sort
     * @param bean
     * @return
     */
    public Object sitePageQuery(Sort sort, FindJournalBean bean);

    /**
     * 添加期刊
     *
     * @param journal
     */
    public Journal saveJournal(Journal journal, Long user_id, Long org_id);

    /**
     * 修改期刊
     *
     * @param journal
     */
    public Journal updateJournal(Journal journal, Long user_id);

    /**
     * 通过期刊ID查找期刊
     *
     * @param journal_id
     * @return
     */
    public Journal selectById(Long journal_id);

    /**
     * 通过组织ID查找期刊
     *
     * @param org_id
     * @return
     */
    public List<Journal> selectByOrgId(Long org_id);

    /**
     * 通过期刊Ids删除期刊
     * @param cat_ids
     */
    public void deleteByCatIds(String cat_ids);

    /**
     * 通过期刊Ids查找期刊
     * @param cat_ids
     * @return
     */
    public List<Journal> selectByCatIds(String cat_ids);

    /**
     * 通过期刊Ids删除期刊
     * @param journal_ids_str
     */
    public void deleteByJournalIds(String journal_ids_str);

    /**
     * 期刊详情 发布、取消发布，推荐，头条
     * @param journal
     * @param user_id
     * @return
     */
    public Object updateStatus(Journal journal, Long user_id);

    /**
     * 获取有限数量的热门期刊
     * @param count
     * @return
     */
    public List<Map<String,Object>> selectRecommendListByCount(Long org_id, Integer count);

    /**
     * 查询杂志数量
     * @param org_id
     * @return
     */
    Integer getCountByOrgId(Long org_id);

    /**
     * 根据期刊ID查询期刊分类ID
     * @param journal_id
     * @return
     */
    Long getCatId(Long journal_id);
}
