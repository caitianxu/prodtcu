package com.cjsz.tech.journal.service.Impl;

import com.cjsz.tech.journal.domain.PeriodicalRepo;
import com.cjsz.tech.journal.domain.UnPeriodicalErr;
import com.cjsz.tech.journal.mapper.UnPeriodicalErrMapper;
import com.cjsz.tech.journal.service.UnPeriodicalErrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by Administrator on 2016/12/22 0022.
 */
@Service
public class UnPeriodicalErrServiceImpl implements UnPeriodicalErrService {

    @Autowired
    UnPeriodicalErrMapper unPeriodicalErrMapper;

    @Override
    public void insertUnPeriodicalErr(UnPeriodicalErr bean){
        unPeriodicalErrMapper.insert(bean);
    }
    @Override
    public void  delUnPeriodicalErr(String file_name){
        unPeriodicalErrMapper.deleteByFileName(file_name);
    }
}
