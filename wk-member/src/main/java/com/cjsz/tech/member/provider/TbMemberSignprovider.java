package com.cjsz.tech.member.provider;

import com.cjsz.tech.member.beans.MemberSignListBean;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by Administrator on 2017/9/12 0012.
 */
public class TbMemberSignprovider {

    public String getList(Map<String,Object> param){
        MemberSignListBean bean = (MemberSignListBean)param.get("bean");
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from (select ms.*,m.nick_name,m.account from tb_member_sign ms left join tb_member m on ms.member_id = m.member_id where 1=1");
        if (StringUtils.isNotEmpty(bean.getSearchText())){
            sqlBuffer.append(" and ( m.nick_name like '%"+bean.getSearchText()+"%' or " +
                             " m.account like '%"+bean.getSearchText()+"%' or " +
                             " m.phone like '%"+bean.getSearchText()+"%' ) ");
        }
        if (bean.getDate_time() != null){
            sqlBuffer.append(" and sign_time like '"+bean.getDate_time()+"%' ");
        }
        sqlBuffer.append(" order by ms.create_time desc )aaa group by member_id order by id desc ");
        return sqlBuffer.toString();
    }
}
