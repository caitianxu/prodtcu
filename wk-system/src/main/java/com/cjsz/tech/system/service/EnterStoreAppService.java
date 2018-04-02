package com.cjsz.tech.system.service;

import com.cjsz.tech.system.beans.EnterStoreApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 进店  APP信息
 * Created by shiaihua on 16/7/1.
 */
@Service
public class EnterStoreAppService {

    @Autowired
    JdbcTemplate jdbcTemplate;


    public List<EnterStoreApp> findList() {


        return null;
    }

    public EnterStoreApp findByOrg(Long orgid) {
        String qsql = "select * from org_intostore_app where org_id="+orgid;
        List<EnterStoreApp> appList = jdbcTemplate.query(qsql, BeanPropertyRowMapper.newInstance(EnterStoreApp.class));
        if(appList!=null && appList.size()>0) {
            return appList.get(0);
        }
        return null;
    }

}
