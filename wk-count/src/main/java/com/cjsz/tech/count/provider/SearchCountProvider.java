package com.cjsz.tech.count.provider;

import com.github.pagehelper.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by Li Yi on 2016/12/22.
 */
public class SearchCountProvider {

    public String searchCountList(Map<String, Object> param) {
        Long org_id = (Long) param.get("org_id");
        String searchText = (String) param.get("searchText");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from search_count where 1=1 ");
        if (org_id != null && org_id != 1){
            sqlBuffer.append(" and org_id ="+org_id);
        }
        if (StringUtils.isNotEmpty(searchText)){
            sqlBuffer.append(" and name like concat('%','"+searchText+"','%') or org_name like concat('%','"+searchText+"','%')");
        }
        return sqlBuffer.toString();
    }
}
