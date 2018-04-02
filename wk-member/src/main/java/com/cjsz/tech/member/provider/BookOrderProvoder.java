package com.cjsz.tech.member.provider;

import com.cjsz.tech.member.beans.OrderListBean;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/12 0012.
 */
public class BookOrderProvoder {

    public String getList(Map<String,Object> param){
        OrderListBean bean = (OrderListBean)param.get("bean");
        String searchText = bean.getSearchText();
        Date start_time = bean.getStart_time();
        Date end_time = bean.getEnd_time();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(" select bo.*,b.book_name,m.nick_name from book_order bo left join book_repo b on b.book_id = bo.book_id " +
                         " left join tb_member m on bo.member_id = m.member_id where 1=1  ");
        if (StringUtils.isNotEmpty(searchText)){
            sqlBuffer.append(" and (m.nick_name like '%"+searchText+"%' or " +
                             " b.book_name like '%"+searchText+"%'  )");
        }
        if (start_time != null){
            sqlBuffer.append(" and bo.create_time >= "+start_time);
        }
        if (end_time != null){
            sqlBuffer.append(" and bo.create_time <= "+end_time);
        }
        sqlBuffer.append("group by bo.id order by bo.create_time desc");
        return sqlBuffer.toString();
    }
}
