package com.earth.angel.util

import android.text.TextUtils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    var ONE_DAY_TIME = 24 * 60 * 60 * 1000.toLong()

    var time_format_time_zone = "yyyy-MM-dd'T'HH:mm:ss"

    private val format: DateFormat = SimpleDateFormat(time_format_time_zone)

    private const val time_format1 = "HH:mm"

    private val sdf1 = SimpleDateFormat(time_format1)

    private const val time_format2 = "HH:mm"

    private val sdf2 = SimpleDateFormat(time_format2)

    private const val time_format3 = "HH:mm"

    private val sdf3 = SimpleDateFormat(time_format3)

    private const val time_format4 = "MM/dd HH:mm"

    private val sdf4 = SimpleDateFormat(time_format4)

    private const val time_format5 = "MM/dd/yy HH:mm"

    private val sdf5 = SimpleDateFormat(time_format5)

    private const val time_format6 = "MM/dd/yy hh:mm aa"

    private val sdf6 = SimpleDateFormat(time_format6)

    private const val time_format7 = "yyyy-MM-dd HH:mm:ss"

    private val sdf7 = SimpleDateFormat(time_format7)

    private const val time_format8 = "HH:mm yyyy/MM/dd"

    private val sdf8 = SimpleDateFormat(time_format8)

    fun transformTime12(time: String?): String? {
        return try {
            val date = sdf7.parse(time)
            synchronized(sdf6) { return sdf6.format(date) }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /*  fun transformTimeInside(
          time: String?,
          isLocalTime: Boolean
      ): String? {
          if (time == null || "" == time) return ""
          try {
              var date = format.parse(time)
              date = Date(date.time + TimeZone.getDefault().rawOffset)
              var ss = ""
              val tempTime = System.currentTimeMillis()
              if ((tempTime - date.time) / (24 * 60 * 60 * 1000) == 0L) {
                  val calendar = Calendar.getInstance()
                  calendar.time = date
                  val day1 = calendar[Calendar.DAY_OF_MONTH]
                  calendar.timeInMillis = System.currentTimeMillis()
                  val day2 = calendar[Calendar.DAY_OF_MONTH]
                  ss = if (day2 - day1 > 0) {
                      ViewUtils.getString(R.string.Yesterday).toString() + " " + sdf2.format(date)
                  } else {
                      sdf1.format(date)
                  }
              } else if ((tempTime - date.time) / (24 * 60 * 60 * 1000) == 1L) {
                  ss = ViewUtils.getString(R.string.Yesterday).toString() + " " + sdf2.format(date)
              } else if ((tempTime - date.time) / (24 * 60 * 60 * 1000) <= 6) {
                  ss = getWeekOfDate(date) + " " + sdf3.format(date)
              } else {
                  val today = Date()
                  val tcalendar = Calendar.getInstance()
                  tcalendar.time = today
                  val dcalendar = Calendar.getInstance()
                  dcalendar.time = date
                  if (dcalendar[Calendar.YEAR] == tcalendar[Calendar.YEAR]) {
                      ss = sdf4.format(date)
                      val months = ss.split("/".toRegex()).toTypedArray()[1]
                      ss = getMonthOfDate(date) + " " + months
                  } else {
                      ss = sdf5.format(date)
                  }
              }
              return ss
          } catch (e: ParseException) {
              e.printStackTrace()
          }
          return ""
      }*/


    /*  fun getWeekOfDate(dt: Date?): String {
          val weekDays: Array<String> = ViewUtils.getStringArray(R.array.week)
          val cal = Calendar.getInstance()
          cal.time = dt
          var w = cal[Calendar.DAY_OF_WEEK] - 1
          if (w < 0) w = 0
          return weekDays[w]
      }

      fun getMonthOfDate(dt: Date?): String {
          val monthDays: Array<String> = ViewUtils.getStringArray(R.array.month)
          val cal = Calendar.getInstance()
          cal.time = dt
          var m = cal[Calendar.MONTH] - 1
          if (m < 0) m = 0
          return monthDays[m]
      }*/

    fun getTime(timestamp: Long): String? {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date(timestamp))
    }
    fun getHMSTime(timestamp: Long): String? {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            .format(Date(timestamp))
    }
    /* fun getTime(timestamp: String?): String? {
         return getTime(StringUtils.toInt(timestamp))
     }*/

    fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar[Calendar.YEAR]
    }

    /**
     * @return int 返回类型
     * @throws
     * @Title: getCuurWeek
     * @Description: 获取当前是第几周
     * @author xiangjin.tian@sotao.com
     * @date 2014-11-11 上午11:20:03
     */
    fun getCuurWeek(): Int {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.firstDayOfWeek = Calendar.SUNDAY
        return calendar[Calendar.WEEK_OF_YEAR]
    }

    fun getCuurWeek(date: Date?): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.firstDayOfWeek = Calendar.SUNDAY
        return calendar[Calendar.WEEK_OF_YEAR]
    }

    fun getCurrentYear(date: Date?): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar[Calendar.YEAR]
    }

    /**
     * @param date
     * @return long 返回类型
     * @throws
     * @Title: timestamp
     * @Description: 获取时间戳 （毫秒级）
     * @author xiangjin.tian@sotao.com
     * @date 2015-3-23 下午12:32:10
     */
    fun getTimestampMillis(date: Date?): Long {
        val cal = Calendar.getInstance()
        cal.time = date
        val timestamp = cal.timeInMillis.toString()
        return java.lang.Long.valueOf(timestamp)
    }

    fun isPassAtLeastADay(time: Long): Boolean {
        val currentCal = Calendar.getInstance()
        currentCal[Calendar.HOUR] = 0
        currentCal[Calendar.MINUTE] = 0
        currentCal[Calendar.SECOND] = 0
        currentCal[Calendar.MILLISECOND] = 0
        val aDayCal = Calendar.getInstance()
        aDayCal.timeInMillis = time
        aDayCal[Calendar.HOUR] = -12 //set 0:0:0
        aDayCal[Calendar.MINUTE] = 0
        aDayCal[Calendar.SECOND] = 0
        aDayCal[Calendar.MILLISECOND] = 0
        val intervalMilli = currentCal.timeInMillis - aDayCal.timeInMillis
        val xcts = (intervalMilli / (24 * 60 * 60 * 1000)).toInt()
        return xcts >= 1
    }

    var weekDays = arrayOf(
        "Sunday",
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday"
    )

    /*   fun getWeekDay(date: String?): String? {
           val formatter =
               SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
           var org_date: Date? = null
           var timeStr: String? = null
           try {
               org_date = formatter.parse(date)
           } catch (e: ParseException) {
               e.printStackTrace()
           }
           org_date = changeTimeZone(
               org_date,
               TimeZone.getTimeZone("GMT"),
               TimeZone.getDefault()
           )
           if (org_date != null) {
               val orgMills = Timestamp.valueOf(date).time
               val currMills = Calendar.getInstance().timeInMillis
               val days = (currMills - orgMills) / 1000 / 60 / 60 / 24
               val calendar = Calendar.getInstance()
               if (days < 2) {
                   val currDay = calendar[Calendar.DAY_OF_YEAR]
                   calendar.time = org_date
                   val orgDay = calendar[Calendar.DAY_OF_YEAR]
                   timeStr = if (currDay == orgDay) {
                       "Today"
                   } else {
                       "Yesterday"
                   }
               } else {
                   if (com.bana.dating.lib.utils.DateUtils.getCurrentYear() == com.bana.dating.lib.utils.DateUtils.getCurrentYear(
                           org_date
                       )
                   ) {
                       if (com.bana.dating.lib.utils.DateUtils.getCuurWeek() == com.bana.dating.lib.utils.DateUtils.getCuurWeek(
                               org_date
                           )
                       ) {
                           calendar.time = org_date
                           var w = calendar[Calendar.DAY_OF_WEEK] - 1
                           if (w < 0) w = 0
                           timeStr = weekDays[w]
                       } else if (com.bana.dating.lib.utils.DateUtils.getCuurWeek() - com.bana.dating.lib.utils.DateUtils.getCuurWeek(
                               org_date
                           ) == 1
                       ) {
                           timeStr = "Last week"
                       } else if (com.bana.dating.lib.utils.DateUtils.getCuurWeek() - com.bana.dating.lib.utils.DateUtils.getCuurWeek(
                               org_date
                           ) > 1 && days < 30
                       ) {
                           timeStr = "A few weeks ago"
                       } else {
                           timeStr = "And beyond"
                       }
                   } else {
                       if (days < 7) {
                           val c = Calendar.getInstance()
                           val curWeek = c[Calendar.DAY_OF_WEEK]
                           c.time = org_date
                           val orgWeek = c[Calendar.DAY_OF_WEEK]
                           if (curWeek > orgWeek) {
                               var w = c[Calendar.DAY_OF_WEEK] - 1
                               if (w < 0) w = 0
                               timeStr = weekDays[w]
                           } else {
                               timeStr = "Last week"
                           }
                       } else if (days >= 7 && days < 2 * 7) {
                           timeStr = "Last week"
                       } else if (days >= 2 * 7 && days < 30) {
                           timeStr = "A few weeks ago"
                       } else {
                           timeStr = "And beyond"
                       }
                   }
               }
           }
           return timeStr
       }
   */
    fun getMonthDay(date: String?): String? {
        if (TextUtils.isEmpty(date)) {
            return ""
        }
        val formatter =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var org_date: Date? = null
        try {
            org_date = formatter.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dateFormat =
            SimpleDateFormat("MMM d", Locale.ENGLISH)
        return dateFormat.format(org_date)
    }

    /**
     * 获取更改时区后的日期
     *
     * @param date    日期
     * @param oldZone 旧时区对象
     * @param newZone 新时区对象
     * @return 日期
     */
    fun changeTimeZone(
        date: Date?,
        oldZone: TimeZone,
        newZone: TimeZone
    ): Date? {
        var dateTmp: Date? = null
        if (date != null) {
            val timeOffset = oldZone.rawOffset - newZone.rawOffset
            dateTmp = Date(date.time - timeOffset)
        }
        return dateTmp
    }


    fun getTimeMillisBeforeYear(years: Int): Long {
        val c = Calendar.getInstance()
        c.add(Calendar.YEAR, -years)
        return c.time.time
    }

    fun getAge(year: Int, month: Int, day: Int): Int {
        var age = 0
        val cal = Calendar.getInstance()
        val yearNow = cal[Calendar.YEAR]
        val monthNow = cal[Calendar.MONTH] + 1
        val dayOfMonthNow = cal[Calendar.DAY_OF_MONTH]
        age = yearNow - year
        if (monthNow <= month) {
            if (monthNow == month) {
                if (dayOfMonthNow < day) {
                    age--
                }
            } else {
                age--
            }
        }
        return age
    }

    fun getTodayDate(): String? {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date())
    }

    fun stringToDateLocal(time: String?): Date? {
        val format: DateFormat = SimpleDateFormat(time_format7)
        format.timeZone = TimeZone.getTimeZone("UTC")
        try {
            if (!TextUtils.isEmpty(time)) {
                return format.parse(time)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return Date(System.currentTimeMillis())
    }


    fun getPastTime(logTime: Long): String? {
        if (logTime != 0L) {
            val timeHours =
                (System.currentTimeMillis() / 1000 - logTime) / 60 / 60
            return if (timeHours >= 24) { //超过一天的
                val days = timeHours / 24
                if (days >= 7) "A week ago" else if (days == 1L) "1 day ago" else "$days days ago"
            } else {
                if (timeHours <= 1) "1 hour ago" else "$timeHours hours ago"
            }
        }
        return ""
    }

    fun getPastTimeForViewMe(logTime: Long): String? {
        if (logTime != 0L) {
            val timeHours =
                (System.currentTimeMillis() / 1000 - logTime) / 60 / 60
            return if (timeHours >= 24) {
                sdf8.format(logTime * 1000)
            } else {
                if (timeHours >= 1) {
                    "$timeHours hour ago"
                } else {
                    val minutes = timeHours * 60
                    if (minutes > 1) {
                        "$timeHours min ago"
                    } else {
                        "just now!"
                    }
                }
            }
        }
        return ""
    }

    fun getPastTimeForStandardViewMe(logTime: Long): String? {
        if (logTime != 0L) {
            val timeHours =
                (System.currentTimeMillis() / 1000 - logTime) / 60 / 60
            return if (timeHours >= 24) {
                "xxxxxx"
            } else {
                if (timeHours >= 1) {
                    "$timeHours hours ago"
                } else {
                    val minutes = timeHours * 60
                    if (minutes > 1) {
                        "$timeHours min ago"
                    } else {
                        "just now!"
                    }
                }
            }
        }
        return ""
    }

    fun isToday(timestamp: Long): Boolean {
        val c = Calendar.getInstance()
        clearCalendar(
            c,
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND,
            Calendar.MILLISECOND
        )
        val firstOfDay = c.timeInMillis // 今天最早时间
        c.timeInMillis = timestamp
        clearCalendar(
            c,
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND,
            Calendar.MILLISECOND
        ) // 指定时间戳当天最早时间
        return firstOfDay == c.timeInMillis
    }

    fun isBeforeToday(timestamp: Long): Boolean {
        val c = Calendar.getInstance()
        clearCalendar(
            c,
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND,
            Calendar.MILLISECOND
        )
        val firstOfDay = c.timeInMillis // 今天最早时间
        c.timeInMillis = timestamp
        clearCalendar(
            c,
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND,
            Calendar.MILLISECOND
        ) // 指定时间戳当天最早时间
        return c.timeInMillis < firstOfDay
    }

    fun isXDaysAgo(days: Int, timestamp: Long): Boolean {
        val c = Calendar.getInstance()
        clearCalendar(
            c,
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND,
            Calendar.MILLISECOND
        )
        val firstOfDay = c.timeInMillis // 今天最早时间
        c.timeInMillis = timestamp
        clearCalendar(
            c,
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND,
            Calendar.MILLISECOND
        ) // 指定时间戳当天最早时间
        return c.timeInMillis < firstOfDay - ONE_DAY_TIME * days
    }

    fun clearCalendar(c: Calendar, vararg fields: Int) {
        for (f in fields) {
            c[f] = 0
        }
    }

    fun convertSecToTimeString(lSeconds: Long): String? {
        val nHour = lSeconds / 3600
        var nMin = lSeconds % 3600
        val nSec = nMin % 60
        nMin = nMin / 60
        return String.format("%02d:%02d:%02d", nHour, nMin, nSec)
    }

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private const val MIN_CLICK_DELAY_TIME = 1000
    private var lastClickTime: Long = 0

    fun isFastClick(): Boolean {
        var flag = false
        val curClickTime = System.currentTimeMillis()
        if (curClickTime - lastClickTime >= MIN_CLICK_DELAY_TIME) {
            flag = true
        }
        lastClickTime = curClickTime
        return flag
    }

    fun getAge( birthDay: Date?): Int {
        var age=0
        val cal = Calendar.getInstance()
        if (cal.before(birthDay)) { //出生日期晚于当前时间，无法计算
            throw  IllegalArgumentException(
                "The birthDay is before Now.It's unbelievable!"
            )
            return age
        }
        val yearNow = cal[Calendar.YEAR] //当前年份

        val monthNow = cal[Calendar.MONTH] //当前月份

        val dayOfMonthNow = cal[Calendar.DAY_OF_MONTH] //当前日期
        cal.time = birthDay
        val yearBirth = cal[Calendar.YEAR]
        val monthBirth = cal[Calendar.MONTH]
        val dayOfMonthBirth = cal[Calendar.DAY_OF_MONTH]
         age = yearNow - yearBirth //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--//当前日期在生日之前，年龄减一
            } else {
                age--
            }
        }
        return age
    }

    @Throws(ParseException::class)
    fun parse(strDate: String?): Date? {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.parse(strDate)
    }
}