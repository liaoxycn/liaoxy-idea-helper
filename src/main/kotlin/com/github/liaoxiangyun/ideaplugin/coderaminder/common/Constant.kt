package com.github.liaoxiangyun.ideaplugin.coderaminder.common

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class Constant {
    companion object {
        const val setttingName: String = "代码量统计"
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val PATTERN_M: Pattern = Pattern.compile("^\\d\\d:\\d\\d$")
        val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}