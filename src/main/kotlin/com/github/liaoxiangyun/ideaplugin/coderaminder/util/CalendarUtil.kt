package com.github.liaoxiangyun.ideaplugin.coderaminder.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object CalendarUtil {
    @Throws(ParseException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val d1 = sdf.parse("2018-12-01 22:20:22")
        val d2 = sdf.parse("2018-12-31 02:20:22")
        val js = js(d1, d2)
        println("天数差是：$js")
    }

    /**
     * 计算两个日期的天数差
     *
     * @param d1
     * @param d2
     * @return
     */
    fun js(d1: Date?, d2: Date?): Int {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        //long类型的日期也支持
//    cal1.setTimeInMillis(long);
//    cal2.setTimeInMillis(long);
        cal1.time = d1
        cal2.time = d2

        //获取日期在一年(月、星期)中的第多少天
        val day1 = cal1[Calendar.DAY_OF_YEAR] //第335天
        val day2 = cal2[Calendar.DAY_OF_YEAR] //第365天

        //获取当前日期所在的年份
        val year1 = cal1[Calendar.YEAR]
        val year2 = cal2[Calendar.YEAR]

        //如果两个日期的是在同一年，则只需要计算两个日期在一年的天数差；
        //不在同一年，还要加上相差年数对应的天数，闰年有366天
        return if (year1 != year2) //不同年
        {
            var timeDistance = 0
            for (i in year1 until year2) {
                timeDistance += if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) //闰年
                {
                    366
                } else  //不是闰年
                {
                    365
                }
            }
            println(timeDistance + (day2 - day1))
            timeDistance + (day2 - day1)
        } else  //同年
        {
            println("判断day2 - day1 : " + (day2 - day1))
            day2 - day1
        }
    }
}