package com.cjsz.tech.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
	
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy");

	public static String getDateTime(Date date) {
		return sdf.format(date);
	}

	public static String getCurrentDate() {
		return sdf2.format(new Date());
	}

	public static String getYear(Date date) {
		return sdf3.format(date);
	}
	
	public static String getDays(Date date, Integer day) {
		Calendar calendar = Calendar.getInstance();
	    //过去七天 day = -7, 未来7天 day = 7
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
	    return sdf2.format(calendar.getTime());
	}
	public static Date getDaysTime(Date date, Integer day) {
		Calendar calendar = Calendar.getInstance();
		//过去七天 day = -7, 未来7天 day = 7
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		return calendar.getTime();
	}

	public static String getMinute(Date date, Integer minute) {
		Calendar calendar = Calendar.getInstance();
		//未来半小时minute = 30；
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minute);
		return sdf.format(calendar.getTime());
	}
	public static Date getMinuteTime(Date date, Integer minute) {
		Calendar calendar = Calendar.getInstance();
		//未来半小时minute = 30；
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minute);
		return calendar.getTime();
	}

	public static String getSecond(Date date, Integer second) {
		Calendar calendar = Calendar.getInstance();
		//未来半分钟second = 30；
		calendar.setTime(date);
		calendar.add(Calendar.SECOND, second);
		return sdf.format(calendar.getTime());
	}

	public static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyMMddHHmmss");
		}
	};
}
