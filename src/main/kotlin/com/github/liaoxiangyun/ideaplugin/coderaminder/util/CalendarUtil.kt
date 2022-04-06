package com.github.liaoxiangyun.ideaplugin.coderaminder.util

import com.github.liaoxiangyun.ideaplugin.coderaminder.settings.CodeSettingsState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.ParseException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter


object CalendarUtil {
    @Throws(ParseException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val content = getContent("/json/2022.json")
        println("content = ${content}")


    }

    private val gson = Gson()

    open val calendarMap = mutableMapOf<String, Int>()

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


    private fun <T> toObj(json: String): T {
        return gson.fromJson(json, object : TypeToken<T>() {}.type)
    }

    open fun load(): MutableMap<String, Int> {
        val content = getContent("/json/2022.json")
        println("content = $content")

        val toObj = toObj<Map<String, Map<String, Int>>>(content)
        for (entry in toObj) {
            for (e in entry.value) {
                calendarMap[e.key] = e.value
            }
        }

        try {
            val str = CodeSettingsState.instance.calendar
            if (str.trim().isNotBlank()) {
                val toObj1 = toObj<Map<String, Map<String, Int>>>(str)
                for (entry in toObj1) {
                    for (e in entry.value) {
                        calendarMap[e.key] = e.value
                    }
                }
            }

        } catch (e: Exception) {
        }
        return calendarMap
    }

    init {
        val load = load()
        println(load)
    }


    private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private fun isOffDay(localDate: LocalDate): Boolean {
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
        var weekDays = getWeekDays(now)

        for (index in week..0) {
            weekDays = if (index > 0) {
                getWeekDays(weekDays.last().plusDays(1))
            } else if (index < 0) {
                getWeekDays(weekDays.first().plusDays(-1))
            } else {
                break;
            }
        }

        return weekDays
    }

    open fun getWeekDays(date: LocalDate): ArrayList<LocalDate> {
        val list = arrayListOf<LocalDate>()
        var begin: LocalDate = date
        var end: LocalDate = date
        //向前查
        while (true) {
            val b = isOffDay(begin)
            val last = begin.plusDays(-1)
            val b1 = isOffDay(last)
            if (!b && b1) {
                break
            }
            begin = last
        }
        //向后查
        while (true) {
            val b = isOffDay(end)
            val next = begin.plusDays(1)
            val b1 = isOffDay(next)
            if (b && !b1) {
                break
            }
            end = next
        }
        do {
            list.add(begin)
            begin.plusDays(1)
        } while (begin < end)

        return list
    }

}