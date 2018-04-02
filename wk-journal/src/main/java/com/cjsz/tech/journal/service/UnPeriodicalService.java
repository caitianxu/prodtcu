package com.cjsz.tech.journal.service;


import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.journal.domain.PeriodicalRepo;
import com.cjsz.tech.journal.domain.UnPeriodical;
import org.springframework.data.domain.Sort;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface UnPeriodicalService {

    /**
     * 获取列表
     * @param ben
     * @param sort
     * @return
     */
    public  Object getUnPeriodicalAll(PageConditionBean ben, Sort sort);

    /**
     * 创建任务
     * @param bean
     */
    public void insertUnPeriodical(UnPeriodical bean);

    /**
     * 修改任务
     * @param bean
     */
    public void saveUnPeriodical(UnPeriodical bean);

    /**
     * 根据文件名获取
     * @param file_name
     */
    public PeriodicalRepo getPeriodicalByFileName(String file_name);

    /**
     * 修改任务状态
     * @param title
     */
    public  void updateUnStatus(String title);

    /**
     * 获取同系列一本 有分类和简介的期刊
     * @param series_name
     * @return
     */
    public PeriodicalRepo getParentRepo(String series_name);

    /**
     * 查询有未完成的线程
     * @return
     */
    public UnPeriodical getUnPeriodicalTop();
}
