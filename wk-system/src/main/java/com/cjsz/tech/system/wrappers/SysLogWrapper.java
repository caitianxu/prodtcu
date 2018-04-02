package com.cjsz.tech.system.wrappers;

import com.cjsz.tech.core.page.BaseWrapper;
import com.cjsz.tech.system.domain.SysActionLog;
import com.cjsz.tech.system.domain.SysLog;
import com.cjsz.tech.utils.DateUtils;

import java.util.*;

/**
 * Created by Bruce on 2016/11/10.
 */
public class SysLogWrapper implements BaseWrapper<Map, SysLog> {

    @Override
    public List<Map> toDTOList(Collection<SysLog> objectList) {
        if (objectList == null) {
            return null;
        }
        List<Map> results = new ArrayList<Map>();
        for (SysLog each : objectList) {
            results.add(toDTO(each));
        }
        return results;
    }

    @Override
    public Map toDTO(SysLog o) {
        if (o == null) {
            return null;
        }
        Map<String, Object> r = new HashMap<String, Object>();
        r.put("sys_log_id", o.getSys_log_id());
        r.put("sys_log_code", o.getSys_log_code());
        r.put("sys_log_content", o.getSys_log_content());
        Date create_time = o.getCreate_time();
        if(create_time != null) {
            r.put("create_time", DateUtils.getDateTime(create_time));
        }
        return r;
    }
}

