package com.cjsz.tech.system.service;

import com.cjsz.tech.system.beans.EnterStoreInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 进店 店面信息（含待统计KPI信息)
 * Created by shiaihua on 16/7/1.
 */
@Service
public class EnterStoreShopService {


    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<EnterStoreInfo> findByOrg(Long orgid) {


        return null;
    }

    public EnterStoreInfo findByOrgAndShop(Long orgid,String shopid) {
        String qsql = "select * from org_intostore_shop where org_id="+orgid+" and shop_id='"+shopid+"'";
        List<EnterStoreInfo> storeList = jdbcTemplate.query(qsql, BeanPropertyRowMapper.newInstance(EnterStoreInfo.class));
        if(storeList!=null && storeList.size()>0) {
            return storeList.get(0);
        }
        return null;
    }


}
