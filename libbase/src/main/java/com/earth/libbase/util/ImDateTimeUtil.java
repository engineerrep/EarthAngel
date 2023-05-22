package com.earth.libbase.util;

import android.content.Context;

import com.earth.libbase.R;
import com.earth.libbase.base.BaseApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ImDateTimeUtil {

    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    /**
     * 返回文字描述的日期
     *
     * @param date
     * @return
     */
    public static String getTimeFormatText(Date date) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();

        int currentDayIndex = calendar.get(Calendar.DAY_OF_YEAR);
        int currentYear = calendar.get(Calendar.YEAR);

        calendar.setTime(date);
        int msgYear = calendar.get(Calendar.YEAR);
        int msgDayIndex = calendar.get(Calendar.DAY_OF_YEAR);
        int msgMinute = calendar.get(Calendar.MINUTE);

        String msgTimeStr = calendar.get(Calendar.HOUR_OF_DAY) + ":";

        if (msgMinute < 10) {
            msgTimeStr = msgTimeStr + "0" + msgMinute;
        } else {
            msgTimeStr = msgTimeStr + msgMinute;
        }

        int msgDayInWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (currentDayIndex == msgDayIndex) {
            return msgTimeStr;
        } else {
            Context context = BaseApplication.instance;
            if (currentDayIndex - msgDayIndex == 1 && currentYear == msgYear) {
                msgTimeStr = context.getString(R.string.base_date_yesterday) + msgTimeStr;
            } else if (false/*currentDayIndex - msgDayIndex > 1 && currentYear == msgYear*/) { //本年消息,注释掉统一按照 "年/月/日" 格式显示
                //不同周显示具体月，日，注意函数：calendar.get(Calendar.MONTH) 一月对应0，十二月对应11
                msgTimeStr = (Integer.valueOf(calendar.get(Calendar.MONTH) + 1)) + "/"+ calendar.get(Calendar.DAY_OF_MONTH) + " " + msgTimeStr + " ";
                //msgTimeStr = (Integer.valueOf(calendar.get(Calendar.MONTH) + 1)) + context.getString(R.string.date_month_short) + " "+ calendar.get(Calendar.DAY_OF_MONTH) + context.getString(R.string.date_day_short) + " " + msgTimeStr + " ";
            } else { // 1、非正常时间，如currentYear < msgYear，或者currentDayIndex < msgDayIndex
                //2、非本年消息（currentYear > msgYear），如：历史消息是2018，今年是2019，显示年、月、日
                msgTimeStr = msgYear + "/" + (Integer.valueOf(calendar.get(Calendar.MONTH) + 1)) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + " " + msgTimeStr + " ";
                //msgTimeStr = msgYear + context.getString(R.string.date_year_short) + (Integer.valueOf(calendar.get(Calendar.MONTH) + 1)) + context.getString(R.string.date_month_short) + calendar.get(Calendar.DAY_OF_MONTH) + context.getString(R.string.date_day_short) + msgTimeStr + " ";
            }
        }
        return msgTimeStr;
    }



    /**
     * 将字符串转为时间戳
     * @param dateString
     * @param pattern
     * @return
     */
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }
}
