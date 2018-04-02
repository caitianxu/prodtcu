package com.cjsz.tech.book.service.Impl;


import com.cjsz.tech.book.domain.UnBookErr;
import com.cjsz.tech.book.mapper.UnBookErrMapper;
import com.cjsz.tech.book.service.UnBookErrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
@Service
public class UnBookErrServiceImpl implements UnBookErrService {

    @Autowired
    private UnBookErrMapper unBookErrMapper;

    @Override
    public void insertUnBookErr(UnBookErr bean) {
        //先删除 再新增
        delUnBookErr(bean.getFile_name(), bean.getTask_id());

        bean.setCreate_time(new Date());
        unBookErrMapper.insert(bean);
    }

    @Override
    public void delUnBookErr(String file_name, Long task_id){
        unBookErrMapper.deleteByBookUrl(file_name, task_id);
    }
}
