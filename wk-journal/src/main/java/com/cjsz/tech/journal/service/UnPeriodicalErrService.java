package com.cjsz.tech.journal.service;


import com.cjsz.tech.journal.domain.PeriodicalRepo;
import com.cjsz.tech.journal.domain.UnPeriodicalErr;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface UnPeriodicalErrService {



    /**
     * 创建任务
     * @param bean
     */
    public void insertUnPeriodicalErr(UnPeriodicalErr bean);

    /**
     * 删除任务
     * @param file_name
     */
    public void delUnPeriodicalErr(String file_name);

}
