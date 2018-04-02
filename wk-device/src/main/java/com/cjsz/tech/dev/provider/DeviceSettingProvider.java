package com.cjsz.tech.dev.provider;

import com.cjsz.tech.dev.beans.DevicePageBean;
import com.github.pagehelper.StringUtil;

import java.util.Map;

/**
 * Created by Li Yi on 2016/12/22.
 */
public class DeviceSettingProvider {

    public String pageQuery(Map<String, Object> param) {
        String searchText = (String)param.get("searchText");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from device_setting where 1=1");
        if (StringUtil.isNotEmpty(searchText)) {
            sqlBuffer.append(" and conf_name like '%" + searchText + "%'");
        }
        sqlBuffer.append(" group by conf_id ");
        return sqlBuffer.toString();
    }

    public String getDeviceList(Map<String,Object> parpm){
        DevicePageBean bean = (DevicePageBean)parpm.get("bean");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT r.conf_id,d.*,s.org_name,CONCAT(d.province, city, area)address  FROM " +
                " device d LEFT JOIN  sys_organization s ON d.org_id = s.org_id " +
                " LEFT JOIN (SELECT * FROM device_conf_rel where conf_id = "+bean.getConf_id()+") r  ON d.device_id = r.device_id where 1=1 ");
        if (!bean.getSearchText().isEmpty()){
            String sql = "and ( device_code like '%"+bean.getSearchText()+"%' or org_name like '%"+bean.getSearchText()+"%') ";
            sqlBuffer.append(sql);
        }
        return sqlBuffer.toString();
    }
}
