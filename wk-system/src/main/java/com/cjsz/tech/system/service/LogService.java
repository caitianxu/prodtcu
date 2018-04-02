package com.cjsz.tech.system.service;

import com.cjsz.tech.system.beans.AccessRecord;
import com.cjsz.tech.system.mapper.AccessRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class LogService {

	/**
	 * 业务类型
	 */
	
	public static int BIZ_LOGIN=1;//登陆
	public static int BIZ_BOOK =20;//图书
	public static int BIZ_NEWS = 30;//新闻
	public static int BIZ_VIDEO = 40;//视频
	public static int BIZ_AUDIO = 50;//音频
	public static int BIZ_LINK = 60;//外链
	public static int BIZ_PAPER = 70;//报纸
	public static int BIZ_UPDATE = 80;//升级

	public static int BIZ_SUGGEST = 90;//建议
	
	/**
	 * 动作类型
	 */
	public static int ACT_VIEW = 1;//查看
	public static int ACT_CLICK =2;//点击
	public static int ACT_SCAN = 3;//扫描
	public static int ACT_TOP = 4;//热榜
	
	public static int ACT_LISTCAT=9;
	public static int ACT_LISTITEM=10;
	
	@Autowired
	AccessRecordDao accessRecordDao;

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	
	/**
	 * 记载日志
	 * @param orgid		机构编码
	 * @param devid		设备编码
	 * @param uid		用户编码
	 * @param biztype	业务类型
	 * @param bizid		具体业务编码
	 * @param act		业务操作
	 * @param url		详细url
	 * @param ip		访问IP
	 */
	public void record(Long orgid,Long devid,Long uid,Integer biztype,Long bizid,Integer act,String url,String ip) {
		AccessRecord log = new AccessRecord();
		log.setOrg_id(orgid);
		log.setDev_id(devid);
		log.setUid(uid);
		log.setBiz_type(biztype);
		log.setBiz_id(bizid);
		log.setBiz_act(act);
		log.setAccess_time(new Date());
		log.setAccess_url(url);
		log.setAccess_ip(ip);
		accessRecordDao.insertUseGeneratedKeys(log);
	}

	public List<Map<String,Object>> queryWDBOOK() {
		return accessRecordDao.queryWDBOOK();
	}

	public List<Map<String,Object>> queryWDBOOKByOrg(Long orgid) {
		return accessRecordDao.queryWDBOOKByOrg(orgid);
	}


	/**
	 *
	 * @param orgid  按组织
	 * @param biztype 业务操作
	 * @param year  查询条件
	 * @param byDev 是否按设备分组
	 * @param byYearGroup 是否按年分组
     * @return
     */
	public List<Map<String,Object>> queryBizTypeByYear(Long orgid,Integer biztype,Integer year,Boolean byDev,Boolean byYearGroup,Boolean needTop10) {
		String yearField = "";
		String devField ="";
		String devName ="";
		if(byDev) {
			devField = ",dev_id";
			devName =",c.dev_sn";
		}

		if(byYearGroup) {
			yearField= ",DATE_FORMAT(access_time,'%Y')";
		}else {
			yearField= ",DATE_FORMAT(access_time,'%Y-%m')";
		}

		String sql ="select a.*,b.org_name "+devName+ " from ( select org_id "+devField + yearField+"  as bbq,count(*) as num from access_log where org_id>0 and dev_id>0 ";
		if(orgid!=null && orgid>0) {
			sql+= " and org_id="+orgid;
		}
		if(biztype!=null && biztype>0) {
			sql+= " and biz_type="+biztype;
		}
		if(year==null || year<=0) {
			year = Calendar.getInstance().get(Calendar.YEAR);
		}
		sql+= " and  DATE_FORMAT(access_time,'%Y')= '"+year+"' group by org_id"+ devField + yearField;
		sql+= " ) a  left join org_item b on a.org_id=b.id ";
		if(byDev) {
			sql+= "   left join app_device c on a.org_id=c.org_id and a.dev_id=c.id ";
		}
		if(byYearGroup) {
			sql += " order by a.num desc";
		}else {
			sql += " order by a.bbq asc";
		}
		if(needTop10) {
			sql += " limit 0,10 ";
		}
		System.out.println("queryBizTypeByYear : "+sql);
		return jdbcTemplate.queryForList(sql);
	}

	//机构终端访问排行
	public List<Map<String,Object>> queryOrgRank(){
		return accessRecordDao.queryOrgRank();
	}

	//终端总访问量
	public Long getVisitCount(){
		return accessRecordDao.getVisitCount();
	}

	//终端总访问量
	public Long getDevCount(){
		return accessRecordDao.getDevCount();
	}

	//图书点击榜
	public List<Map<String,Object>> queryBookRank(){
		return accessRecordDao.queryBookRank();
	}

	//地图数据
	public List<Map<String,Object>> queryGroupByPlace(){
		return accessRecordDao.queryGroupByPlace();
	}

	//时间段访问量
	public List<Map<String,Object>> queryGroupByHour(){
		return accessRecordDao.queryGroupByHour();
	}


}
