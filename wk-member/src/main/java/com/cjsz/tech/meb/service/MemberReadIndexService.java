package com.cjsz.tech.meb.service;


import com.cjsz.tech.beans.PageConditionBean;
import com.cjsz.tech.system.beans.SearchBean;
import org.springframework.data.domain.Sort;

import java.util.Map;

/**
 * Created by LuoLi on 2017/4/13 0013.
 */
public interface MemberReadIndexService {

    public void addReadIndex();

    public Object sitePageQuery(Sort sort, PageConditionBean bean, String type,Long org_id);

    public Map<String, Object> getMemberOrder(Long member_id);

    public Object pageQuery(Sort sort, SearchBean searchBean);
}
