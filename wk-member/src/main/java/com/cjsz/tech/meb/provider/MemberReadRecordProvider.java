package com.cjsz.tech.meb.provider;

import com.cjsz.tech.system.beans.SearchBean;

import java.util.Map;

/**
 * Created by LuoLi on 2017/4/28 0028.
 */
public class MemberReadRecordProvider {



    public String getList(Map<String, Object> param) {
        SearchBean bean = (SearchBean) param.get("bean");
        Long org_id = (Long)param.get("org_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT  mrr.book_id,mrr.book_type,mrr.total_time,mrr.begin_time,mrr.end_time,mrr.device_type_code, m.nick_name,b.book_name,mrr.total_chapter FROM  member_read_record mrr " +
                "LEFT JOIN tb_member m ON m.member_id = mrr.member_id " +
                "LEFT JOIN book_repo b ON mrr.book_id = b.book_id " +
                "LEFT JOIN sys_org_extend soe on m.org_id = soe.org_id  where mrr.book_type = 2 ");
        if (bean.getCreate_time() != null){
            sqlBuffer.append(" and TO_DAYS(mrr.end_time) = TO_DAYS('"+bean.getCreate_time()+"')");
        }
        if (org_id ==1){
            if (bean.getType()!=null) {
                if (bean.getType().equals("pro")) {
                    sqlBuffer.append(" and soe.project_code =" + bean.getCode());
                }
                if (bean.getType().equals("org")) {
                    sqlBuffer.append(" and m.org_id = " + bean.getId());
                }
            }
        }else{
            sqlBuffer.append(" and m.org_id ="+org_id);
        }
        sqlBuffer.append(" union ");
        sqlBuffer.append("SELECT  mrr.book_id,mrr.book_type,mrr.total_time,mrr.begin_time,mrr.end_time,mrr.device_type_code, m.nick_name,b.book_name,mrr.total_chapter" +
                " FROM  member_read_record mrr LEFT JOIN tb_member m ON m.member_id = mrr.member_id " +
                "LEFT JOIN cjzww_books b ON mrr.book_id = b.book_id " +
                "LEFT JOIN sys_org_extend soe on m.org_id = soe.org_id  where mrr.book_type = 1 ");
        if (bean.getCreate_time() != null){
            sqlBuffer.append(" and TO_DAYS(mrr.end_time) = TO_DAYS('"+bean.getCreate_time()+"')");
        }
        if (org_id ==1){
            if (bean.getType()!=null) {
                if (bean.getType().equals("pro")) {
                    sqlBuffer.append(" and soe.project_code =" + bean.getCode());
                }
                if (bean.getType().equals("org")) {
                    sqlBuffer.append(" and m.org_id = " + bean.getId());
                }
            }
        }else{
            sqlBuffer.append(" and m.org_id ="+org_id);
        }
        return sqlBuffer.toString();
    }

    public String clickList(Map<String, Object> param) {
        SearchBean bean = (SearchBean) param.get("bean");
        Long org_id = (Long) param.get("org_id");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select bx.*,b.book_name from book_index bx LEFT JOIN book_repo b on bx.book_id=b.book_id " +
                         "left join sys_org_extend soe on bx.org_id = soe.org_id where 1=1");
        if (bean.getCreate_time() != null){
            sqlBuffer.append(" and TO_DAYS(bx.create_time) = TO_DAYS('"+bean.getCreate_time()+"')");
        }
        if (org_id ==1){
            if (bean.getType().equals("pro")){
                sqlBuffer.append(" and soe.project_code ="+bean.getCode());
            }
            if (bean.getType().equals("org")){
                sqlBuffer.append(" and bx.org_id = "+bean.getId());
            }
        }else{
            sqlBuffer.append(" and bx.org_id ="+org_id);
        }
        return sqlBuffer.toString();
    }
}
