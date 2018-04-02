package com.cjsz.tech.book.provider;

import com.cjsz.tech.system.beans.SearchBean;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by LuoLi on 2017/4/28 0028.
 */
public class BookIndexProvider {

    public String pageQuery(Map<String, Object> map){
        SearchBean bean = (SearchBean)map.get("searchBean");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select i.*, b.book_name, o.org_name from book_index i left join book_repo b on i.book_id = b.book_id left join sys_organization o on i.org_id = o.org_id  where i.book_type = 2 ");
        if(bean.getId() != null && bean.getId() != 0){
            if("pro".equals(bean.getType())){
                String sql = " and i.org_id in (select org_id from sys_org_extend where project_code = '" + bean.getCode() + "') ";
                sqlBuffer.append(sql);
            }else{
                sqlBuffer.append(" and i.org_id = " + bean.getId() );
            }
        }
        if(StringUtils.isNotEmpty(bean.getCreate_time())){
            String sql = " and TO_DAYS(i.create_time) = TO_DAYS('" + bean.getCreate_time() + "') ";
            sqlBuffer.append(sql);
        }
        sqlBuffer.append(" union ");
        sqlBuffer.append("select i.*, b.book_name, o.org_name from book_index i left join cjzww_books b on i.book_id = b.book_id left join sys_organization o on i.org_id = o.org_id  where i.book_type = 1 ");
        if(bean.getId() != null && bean.getId() != 0){
            if("pro".equals(bean.getType())){
                String sql = " and i.org_id in (select org_id from sys_org_extend where project_code = '" + bean.getCode() + "') ";
                sqlBuffer.append(sql);
            }else{
                sqlBuffer.append(" and i.org_id = " + bean.getId() );
            }
        }
        if(StringUtils.isNotEmpty(bean.getCreate_time())){
            String sql = " and TO_DAYS(i.create_time) = TO_DAYS('" + bean.getCreate_time() + "') ";
            sqlBuffer.append(sql);
        }
        sqlBuffer.append(" order by id desc");
        return sqlBuffer.toString();
    }

}
