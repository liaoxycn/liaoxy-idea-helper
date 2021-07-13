package com.github.liaoxiangyun.ideaplugin.coderaminder.common

import com.intellij.openapi.project.Project
import java.text.SimpleDateFormat
import java.util.regex.Pattern

class Constant {
    companion object {
        open val setttingName: String = "福报提醒"
        open val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        open var projects = arrayListOf<Project>()
        open val PATTERN_M = Pattern.compile("^\\d\\d:\\d\\d$")

        open fun getAnyProject(): Project? {
            if (projects.size > 0) {
                return projects[0]
            }
            return null
        }
    }
}