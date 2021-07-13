package com.github.liaoxiangyun.ideaplugin.coderaminder.services

import com.github.liaoxiangyun.ideaplugin.MyBundle
import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant.Companion.projects
import com.github.liaoxiangyun.ideaplugin.coderaminder.settings.CodeSettingsState
import com.github.liaoxiangyun.ideaplugin.coderaminder.util.HttpHelper
import com.github.liaoxiangyun.ideaplugin.common.util.Notify
import com.intellij.openapi.project.Project

class ProjectOpened(project: Project) {

    init {
        PROJECTS.add(project)
        println(projects)
        println(MyBundle.message("projectService", project.name))
        println("STATIC_VAR: $STATIC_VAR")
        if (STATIC_VAR != 1) {
            STATIC_VAR = 1
            println("EXEC")
            Thread {
                Thread.sleep(1000 * 60 * 1)
                while (true) {
                    codingReminder()
                    val rate = CodeSettingsState.instance.getRate()
                    Thread.sleep(rate)
                }
            }.start()
        } else {
            println("NOT EXEC")
        }
    }

    private fun codingReminder() {
        try {
            println("codingReminder")
            val gitRecords = HttpHelper().getGitRecordList()
            println(gitRecords)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                Notify.showErrorNotification(
                        "" + e.message,
                        Constant.getAnyProject(), "${Constant.setttingName}："
                )
            } catch (e: Exception) {
            }
        }
    }


    companion object {
        var STATIC_VAR = 0
        var PROJECTS = mutableListOf<Project>()
    }
}
