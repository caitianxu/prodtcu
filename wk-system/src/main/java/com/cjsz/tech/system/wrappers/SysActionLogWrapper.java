package com.cjsz.tech.system.wrappers;

import com.cjsz.tech.core.page.BaseWrapper;
import com.cjsz.tech.system.domain.SysActionLog;
import com.cjsz.tech.utils.DateUtils;

import java.util.*;

/**
 * Created by Bruce on 2016/11/10.
 */
public class SysActionLogWrapper implements BaseWrapper<Map, SysActionLog> {

    @Override
    public List<Map> toDTOList(Collection<SysActionLog> objectList) {
        if (objectList == null) {
            return null;
        }
        List<Map> results = new ArrayList<Map>();
        for (SysActionLog each : objectList) {
            results.add(toDTO(each));
        }
        return results;
    }

    @Override
    public Map toDTO(SysActionLog o) {
        if (o == null) {
            return null;
        }
        Map<String, Object> r = new HashMap<String, Object>();
        r.put("action_log_id", o.getAction_log_id());
        r.put("action_type", o.getAction_type());
        r.put("action_log_content", o.getAction_log_content());
        r.put("action_log_module_name", o.getAction_log_module_name());
        r.put("action_user_name", o.getAction_user_name());
        r.put("action_ip", o.getAction_ip());
        Date action_time = o.getAction_time();
        if(action_time != null) {
            r.put("action_time", DateUtils.getDateTime(action_time));
        }
        return r;
    }
}
