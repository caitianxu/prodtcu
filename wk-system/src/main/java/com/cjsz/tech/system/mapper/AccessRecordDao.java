package com.cjsz.tech.system.mapper;

import org.apache.ibatis.annotations.Select;
import com.cjsz.tech.system.beans.AccessRecord;
import com.cjsz.tech.system.utils.MyMapper;

import java.util.List;
import java.util.Map;


public interface AccessRecordDao  extends MyMapper<AccessRecord> {

	@Select("select a.*,b.org_name,c.bk_name from ( select org_id,biz_id,biz_act,count(*) as num from access_log where biz_type=20 group by biz_id,biz_act) a  left join org_item b on a.org_id=b.id left join bookitem_repo c on a.biz_id=c.bk_id order by a.num desc,a.biz_id desc")
    List<Map<String,Object>> queryWDBOOK();
    @Select("select a.*,b.org_name,c.bk_name from ( select org_id,biz_id,biz_act,count(*) as num from access_log where biz_type=20 group by biz_id,biz_act) a  left join org_item b on a.org_id=b.id left join bookitem_repo c on a.biz_id=c.bk_id where a.org_id=#{0} order by a.num desc,a.biz_id desc")
    List<Map<String,Object>> queryWDBOOKByOrg(Long orgid);


    @Select("select b.id,if(isnull(a.num),0,a.num) num,b.org_name  from ( select org_id ,count(*) as num from access_log \n" +
            "            where org_id>0 and dev_id in (select id from app_device where enabled = 1) group by org_id ) a  \n" +
            "            RIGHT JOIN org_item b on a.org_id=b.id \n" +
            "            where b.province = '湖北省' and (b.enabled is null or  b.enabled = 1)order by a.num desc LIMIT 0,12 ")
    List<Map<String,Object>> queryOrgRank();

    @Select("select count(*) as num from access_log where dev_id in (select id from app_device where enabled = 1)")
    Long getVisitCount();

    @Select("select count(*) num from app_device ad \n" +
            "LEFT JOIN org_item oi on (ad.org_id = oi.id) \n" +
            "where oi.province = '湖北省' and oi.enabled !=0 and ad.enabled = 1 ")
    Long getDevCount();

    @Select("select al.biz_id,br.bk_name,count(*) click_count from access_log al \n" +
            "            right JOIN bookitem_repo br on(al.biz_id = br.bk_id) \n" +
            "            where biz_type = 20 and biz_act = 2 and dev_id in (select id from app_device where enabled = 1) GROUP BY al.biz_id order by click_count desc  LIMIT 0,5")
    List<Map<String,Object>> queryBookRank();

    @Select("select oi.city name,count(*) value from app_device ad " +
            "LEFT JOIN org_item oi on (ad.org_id = oi.id) " +
            "where oi.province = '湖北省' and oi.enabled <>0 and ad.enabled = 1 GROUP BY oi.city")
    List<Map<String,Object>> queryGroupByPlace();

    @Select("select h1,count(*) count from (select \n" +
            "            case when hour >= 0 and hour < 3 then 1\n" +
            "            when hour >= 3 and hour < 6 then 2 \n" +
            "            when hour >= 6 and hour < 9 then 3\n" +
            "            when hour >= 9 and hour < 12 then 4\n" +
            "            when hour >= 12 and hour < 15 then 5\n" +
            "            when hour >= 15 and hour < 18 then 6\n" +
            "            when hour >= 18 and hour < 21 then 7\n" +
            "            when hour >= 21 and hour < 24 then 8\n" +
            "            end h1,t1.*\n" +
            "             from (select \n" +
            "            *, hour(access_time) as hour from access_log where dev_id in (select id from app_device where enabled = 1) ) t1)t2 group by h1")
    List<Map<String,Object>> queryGroupByHour();



}
