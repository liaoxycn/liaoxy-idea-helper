package com.github.liaoxiangyun.ideaplugin.coderaminder.util

import cn.hutool.json.JSONUtil
import com.github.liaoxiangyun.ideaplugin.coderaminder.settings.CodeSettingsState
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.ParseException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


object CalendarUtil {
    @Throws(ParseException::class)
    @JvmStatic
    fun main(args: Array<String>) {


        println("s = ${parseTime("18:30")}")

    }

    open val calendarMap = mutableMapOf<String, Int>()


    private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    open fun dateStr(date: LocalDate): String {
        return date.format(dateFormat)
    }

    open fun parseTime(str: String): LocalTime {
        if (str.length == 5) {
            return LocalTime.parse("$str:00", timeFormat)
        }
        return LocalTime.parse(str, timeFormat)
    }

    open fun isOffDay(localDate: LocalDate): Boolean {
        val key = localDate.format(dateFormat)
        if (calendarMap.containsKey(key)) {
            val i = calendarMap[key]
            if (i !== 1) {
                return true
            }
        } else {
            val week: DayOfWeek = localDate.dayOfWeek
            if (week == DayOfWeek.SATURDAY || week == DayOfWeek.SUNDAY) {
                return true
            }
        }
        return false
    }

    open fun getWeekDays(week: Int): ArrayList<LocalDate> {
        val now = LocalDate.now()
        loadData(now)
        var weekDays = getWeekDays2(now)
        if (week == 0) {
            return weekDays
        }
        if (week > 0) {
            for (index in 1..week) {
                println("#getWeekDays week=$week index=$index")
                weekDays = getWeekDays2(weekDays.last().plusDays(1))
            }
        } else {
            for (index in 1..-week) {
                println("#getWeekDays week=$week index=$index")
                weekDays = getWeekDays2(weekDays.first().plusDays(-1))
            }
        }
        return weekDays
    }

    private fun getWeekDays2(date: LocalDate): ArrayList<LocalDate> {
        val list = arrayListOf<LocalDate>()
        var begin: LocalDate = date
        var end: LocalDate = date
        //向前查
        var max = 10
        while (true) {
            val b = isOffDay(begin)
            val last = begin.plusDays(-1)
            val b1 = isOffDay(last)
//            println("$begin b=$b ; last b1=$b1 ; ${if (!b && b1) "r=true" else ""}")
            if (!b && b1) {
                break
            }
            begin = last
            if (--max <= 0) {
                break
            }
        }
        max = 10
        //向后查
        while (true) {
            val b = isOffDay(end)
            val next = end.plusDays(1)
            val b1 = isOffDay(next)
//            println("$end b=$b ; next b1=$b1 ; ${if (b && !b1) "r=true" else ""}")
            if (b && !b1) {
                break
            }
            end = next
            if (--max <= 0) {
                break
            }
        }
        do {
            list.add(begin)
            begin = begin.plusDays(1)
        } while (begin <= end)

        return list
    }


    open fun getDefaultContent(): String {
        return getContent("/json/2022.json")
    }

    private fun getContent(path: String): String {
        val stream = CalendarUtil.javaClass.getResourceAsStream(path)

        val buff = BufferedReader(InputStreamReader(stream))
        val sb = StringBuffer()
        var line: String?
        do {
            line = buff.readLine()
            if (line != null) {
                sb.append(line).append("\n")
            } else {
                break
            }
        } while (true)
        return sb.toString()
    }


    private fun <T> getObj(json: String, clazz: Class<T>): T {
        return JSONUtil.toBean(json, clazz)
    }

    private fun setMapByJson(str: String) {
        try {
            if (str.isNotBlank()) {
                val toObj = getObj(str, Map::class.java)
                for (entry in toObj) {
                    if (entry.value is Int) {
                        calendarMap[entry.key as String] = entry.value as Int
                    } else if (entry.value is Map<*, *>) {
                        for (e in (entry.value as Map<Any, Any>)) {
                            calendarMap[e.key as String] = e.value.toString().toInt()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadData(now: LocalDate) {
        val settingsState = CodeSettingsState.instance
        if (now.year == 2022) {
            setMapByJson(getContent("/json/2022.json"))
        }
        setMapByJson(settingsState.calendar)
    }

}