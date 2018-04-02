package com.cjsz.tech.book.service;


import com.cjsz.tech.book.domain.UnBookErr;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public interface UnBookErrService {

    /**
     * 创建任务
     * @param bean
     */
    public void insertUnBookErr(UnBookErr bean);


    /**
     * 删除错误
     * @param file_name
     * @param task_id
     */
    public void delUnBookErr(String file_name, Long task_id);

}
