package com.cjsz.tech.book.service.Impl;

import com.cjsz.tech.book.beans.BookOffLineBean;
import com.cjsz.tech.book.service.BookOffLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 书的离线清单--按组织，按设备以及时间戳获取
 * Created by luoli on 2016/12/26 0026.
 */
@Service
public class BookOffLineServiceImpl implements BookOffLineService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List getOffLineNumList(Long orgid, String time, Object... otherparam) {
        Long devid = (Long)otherparam[0];
        Integer num = 0;
        Integer size = 30000;
        if (null != otherparam && otherparam.length > 2) {
            num = (Integer) otherparam[1];
            size = (Integer) otherparam[2];
        }
        StringBuffer sb = new StringBuffer();
        sb.append(" select br.book_id,br.book_name,br.book_author,br.book_cover,br.book_cover_small,br.book_isbn,br.book_url,br.file_name," +
                "   br.book_publisher,br.book_remark,br.publish_time,br.book_status,br.price,br.end_time,br.create_time,br.update_time,br.parse_status " +
                " , group_concat(bt.tag_name) as tag_names, bor.pkg_id, bor.create_time as book_create_time, bor.order_weight " +
                " , bor.is_hot, bor.is_recommend, bor.book_cat_id, bor.enabled, "+devid+" as device_id " +
                " , bdr.`status` as offline_status, bor.org_id, bor.update_time as book_update_time, br.update_time as repo_update_time, bdr.update_time as sendoff_update_time " +
                " from book_org_rel bor " +
                "  left join book_repo br on br.book_id = bor.book_id " +
                "  left join book_tag_rel btr on btr.book_id = br.book_id " +
                "  left join book_tag bt on btr.tag_id = bt.tag_id " +
                "  left join book_device_rel bdr on bor.book_id = bdr.book_id " +
                " where bor.org_id = " + orgid +
                " and bdr.org_id =  " + orgid +
                "    and bdr.device_id =  " + devid +
                " and br.book_id is not null " +
                " and (bor.update_time is not null " +
                "  and bor.update_time >  '" + time +
                "'  or br.update_time is not null " +
                "  and br.update_time >  '" + time +
                "'  or bdr.update_time is not null " +
                "  and bdr.update_time >  '"+time+"'   ) " +
                "group by bor.book_id, bor.book_cat_id " +
                "limit "+num+", "+size+" ");
        List<BookOffLineBean> books = jdbcTemplate.query(sb.toString(), BeanPropertyRowMapper.newInstance(BookOffLineBean.class));
        return books;
    }

    @Override
    public Integer hasOffLine(Long orgid, String oldtimestamp, Object... otherparam) {
        Long devid = (Long)otherparam[0];
        StringBuffer sb = new StringBuffer();
        sb.append(" select count(*) from ( ");
        sb.append(" select br.*, bor.pkg_id,bor.create_time as book_create_time,bor.order_weight,bor.is_hot,bor.is_recommend,bor.book_cat_id," +
                "bor.enabled,"+devid+" as device_id,bdr.`status` as offline_status, bor.org_id,bor.update_time as book_update_time,br.update_time as repo_update_time," +
                "bdr.update_time as sendoff_update_time from book_org_rel bor  ");
        sb.append(" left join book_repo br  on br.book_id = bor.book_id  ");
        sb.append(" left join ( select * from  book_device_rel where org_id="+orgid+" and device_id= "+devid+" ) bdr on bor.book_id = bdr.book_id  ");
        sb.append(" where bor.org_id = "+orgid+" GROUP BY bor.book_id,bor.book_cat_id");
        sb.append(" ) a1 where a1.book_id is not null and ( " +
                "( a1.book_update_time is not null and a1.book_update_time > '" + oldtimestamp + "') or " +
                "(a1.repo_update_time is not null and  a1.repo_update_time > '" + oldtimestamp + "') or " +
                "(a1.sendoff_update_time is not null and a1.sendoff_update_time > '" + oldtimestamp + "')  )  ");

        Integer checknum= jdbcTemplate.queryForObject(sb.toString(),Integer.class);
        if(checknum==null ) {
            checknum =0;
        }
        return checknum;
    }
}
