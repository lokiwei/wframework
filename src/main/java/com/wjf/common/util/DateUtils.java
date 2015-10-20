package com.jd.rainbow.common.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jd.official.core.exception.BusinessException;

public class DateUtils {

	private static final Logger logger = Logger.getLogger(DateUtils.class);

	private static final String MONTH_FORMAT = "yyyy/MM";

	/**
	 * 获取当前年月
	 * 
	 * @return String
	 */
	public static String getCurrentMonth() {
		SimpleDateFormat sdf = new SimpleDateFormat(MONTH_FORMAT);
		return sdf.format(new Date());
	}

	/**
	 * 获取当前年月
	 * 
	 * @return String
	 */
	public static String getCurrentYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		return sdf.format(new Date());
	}

	/**
	 * 获取前一个月
	 * 
	 * @param dateStr
	 * @return String
	 */
	public static String getPrvesMonth(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(MONTH_FORMAT);
		Calendar c = Calendar.getInstance();
		if (StringUtils.isBlank(dateStr))
			c.setTime(new Date());
		else
			try {
				c.setTime(sdf.parse(dateStr));
			} catch (ParseException e) {
				logger.error("============================ getPrvesMonth faile ==================", e);
			}
		c.add(Calendar.MONTH, -1);
		return sdf.format(c.getTime());
	}

	/**
	 * 获取最近两个月的月份集合
	 * 
	 * @return List<String>
	 */
	public static List<String> getLast2MonthsList() {
		List<String> monthList = new ArrayList<String>();
		monthList.add(getCurrentMonth());
		monthList.add(getPrvesMonth(getCurrentMonth()));
		return monthList;
	}

	/**
	 * 如果参数日期大于等于当前日期，则为可用，返回true 接收日期类型2014-09-09 或20140909
	 * 
	 * @author wangchanghui
	 * @param date
	 * @return boolean
	 * @date 2014年11月3日
	 */
	public static boolean isVailableDate(String date) {
		if (StringUtils.isBlank(date))
			throw new BusinessException("日期为空");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String currentDate = sdf.format(new Date());
		String cpDate = date.replaceAll("-", "");
		if (cpDate.compareToIgnoreCase(currentDate) < 0)
			return false;
		return true;
	}

	public static String getPrevDay(String date, int days) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			c.setTime(sdf.parse(date));
		} catch (ParseException e) {
			logger.error(e);
			throw new BusinessException("业务异常");
		}
		c.add(Calendar.DAY_OF_MONTH, -days);
		return sdf.format(c.getTime());
	}

	public static String getPrevWeekDay(String date, int days) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			c.setTime(sdf.parse(date));
		} catch (ParseException e) {
			logger.error(e);
			throw new BusinessException("业务异常");
		}
		c.add(Calendar.DAY_OF_MONTH, -days);
		String newDate = sdf.format(c.getTime());
		if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			return getPrevWeekDay(newDate, 1);
		return newDate;
	}

	public static String getDateStr(Date date, String patten) {
		if (date == null)
			return null;
		if (StringUtils.isBlank(patten))
			patten = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(patten);
		return sdf.format(date);
	}

	public static String getDateStr(String date, String patten) {
		if (date == null)
			return null;
		if (StringUtils.isBlank(patten))
			patten = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date tmpDate = sdf.parse(date) ;
			sdf = new SimpleDateFormat(patten);
			return sdf.format(tmpDate);
		} catch (ParseException e) {
			logger.error(e);
			throw new BusinessException("业务异常");
		}
	}
	
	
	 /**
	  * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	  * 
	  * @param strDate
	  * @return
	  */
	 public static Date strToDateLong(String strDate) {
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  ParsePosition pos = new ParsePosition(0);
	  Date strtodate = formatter.parse(strDate, pos);
	  return strtodate;
	 }

}
