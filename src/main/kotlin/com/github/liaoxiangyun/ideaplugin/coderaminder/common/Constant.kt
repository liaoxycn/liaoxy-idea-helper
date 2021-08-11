package com.github.liaoxiangyun.ideaplugin.coderaminder.common

import com.intellij.openapi.project.Project
import java.text.SimpleDateFormat
import java.util.regex.Pattern

class Constant {
    companion object {
        open val setttingName: String = "代码提交提醒"
        open val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        open val PATTERN_M = Pattern.compile("^\\d\\d:\\d\\d$")

    }
}