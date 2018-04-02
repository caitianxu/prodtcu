package com.cjsz.tech.dev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.cjsz.tech.dev.domain.DeviceOfflineSyn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

/**
 * 同步信息记录服务
 * Created by shiaihua on 16/12/21.
 */
@Service
public class DeviceOfflineSynService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 查找指定的同步记录
     * @param orgid
     * @param devid
     * @param timestamp 时间戳
     * @return
     */
    public DeviceOfflineSyn findSynRecord(Long orgid,Long devid,Integer modetype,Long timestamp) {
        if(orgid==null || devid==null || timestamp==null || orgid<=0 || devid<=0 || timestamp<-1) {
            return null;
        }
        String qsql = "select * from device_offline_syn where org_id="+orgid+" and devid="+devid+" and mod_type="+modetype+" and time_stamp="+timestamp;
        List<DeviceOfflineSyn> resultList = jdbcTemplate.query(qsql, BeanPropertyRowMapper.newInstance(DeviceOfflineSyn.class));
        if(resultList!=null && resultList.size()>0) {
            return resultList.get(0);
        }
        return null;
    }

    /**
     * 查找最近的同步记录
     * @param orgid
     * @param devid
     * @param modtype 时间戳
     * @return
     */
    public DeviceOfflineSyn findLastestSynRecord(Long orgid,Long devid,Integer modtype) {
        if(orgid==null || devid==null || modtype==null || orgid<=0 || devid<=0 || modtype<-1) {
            return null;
        }
        String qsql = "select * from device_offline_syn where org_id="+orgid+" and devid="+devid+" and mod_type="+modtype;
        List<DeviceOfflineSyn> resultList = jdbcTemplate.query(qsql, BeanPropertyRowMapper.newInstance(DeviceOfflineSyn.class));
        if(resultList!=null && resultList.size()>0) {
            return resultList.get(0);
        }
        return null;
    }


    /**
     * 保存同步记录
     * @param synrecord
     * @return
     */
    public Long saveSynRecord(final DeviceOfflineSyn synrecord) {
        if(synrecord==null) {
            return null;
        }
        final String insertSql = "insert into device_offline_syn(org_id,mod_type,device_id,device_code,time_stamp,syn_status,time_begin) values(?,?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                if(synrecord.getTime_begin()==null) {
                    synrecord.setTime_begin(new Date());
                }
                ps.setLong(1, synrecord.getOrg_id());
                ps.setInt(2, synrecord.getMod_type());
                ps.setLong(3, synrecord.getDevice_id());
                ps.setString(4, synrecord.getDevice_code());
                ps.setLong(5, synrecord.getTime_stamp());
                ps.setInt(6, synrecord.getSyn_status());
                ps.setTimestamp(7, new java.sql.Timestamp(synrecord.getTime_begin().getTime()));
                return ps;
            }
        }, keyHolder);
        Long generatedId = keyHolder.getKey().longValue();
        return generatedId;
    }



}
