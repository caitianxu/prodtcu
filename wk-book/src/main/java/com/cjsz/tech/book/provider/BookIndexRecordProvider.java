package com.cjsz.tech.book.provider;

import com.cjsz.tech.system.beans.SearchBean;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by LuoLi on 2017/4/28 0028.
 */
public class BookIndexRecordProvider {

    public String pageQuery(Map<String, Object> map){
        SearchBean bean = (SearchBean)map.get("searchBean");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select r.*, m.nick_name, b.book_name, o.org_name from book_index_record r left join tb_member m on r.member_id = m.member_id " +
                " left join book_repo b on r.book_id = b.book_id left join sys_organization o on r.org_id = o.org_id where r.book_type = 2 ");
        if(bean.getId() != null && bean.getId() != 0){
            if("pro".equals(bean.getType())){
                String sql = " and r.org_id in (select org_id from sys_org_extend where project_code = '" + bean.getCode() + "') ";
                sqlBuffer.append(sql);
            }else{
                sqlBuffer.append(" and r.org_id = " + bean.getId() );
            }
        }
        if(StringUtils.isNotEmpty(bean.getCreate_time())){
            String sql = " and TO_DAYS(r.create_time) = TO_DAYS('" + bean.getCreate_time() + "') ";
            sqlBuffer.append(sql);
        }
        sqlBuffer.append(" union ");
        sqlBuffer.append("select r.*, m.nick_name, b.book_name, o.org_name from book_index_record r left join tb_member m on r.member_id = m.member_id " +
                " left join cjzww_books b on r.book_id = b.book_id left join sys_organization o on r.org_id = o.org_id where r.book_type = 1 ");
        if(bean.getId() != null && bean.getId() != 0){
            if("pro".equals(bean.getType())){
                String sql = " and r.org_id in (select org_id from sys_org_extend where project_code = '" + bean.getCode() + "') ";
                sqlBuffer.append(sql);
            }else{
                sqlBuffer.append(" and r.org_id = " + bean.getId() );
            }
        }
        if(StringUtils.isNotEmpty(bean.getCreate_time())){
            String sql = " and TO_DAYS(r.create_time) = TO_DAYS('" + bean.getCreate_time() + "') ";
            sqlBuffer.append(sql);
        }
        sqlBuffer.append(" order by record_id desc");
        return sqlBuffer.toString();
    }



}
