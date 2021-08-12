package com.github.liaoxiangyun.ideaplugin.services

import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import com.github.liaoxiangyun.ideaplugin.coderaminder.settings.CodeSettingsState
import com.github.liaoxiangyun.ideaplugin.coderaminder.util.HttpHelper
import com.github.liaoxiangyun.ideaplugin.common.util.Notify
import com.github.liaoxiangyun.ideaplugin.common.util.ProjectUtils
import com.github.liaoxiangyun.ideaplugin.js.service.JsService
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.wm.IdeFrame
import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.wm.WindowManagerListener
import com.intellij.util.concurrency.AppExecutorUtil
import java.util.concurrent.TimeUnit


class MyApplicationService {

    init {
        val wm = WindowManager.getInstance()
        wm.addListener(object : WindowManagerListener {
            override fun frameCreated(frame: IdeFrame) {
                println("#WindowManagerListener frameCreated = $frame")
            }

            override fun beforeFrameReleased(frame: IdeFrame) {
                println("#WindowManagerListener beforeFrameReleased = $frame")
            }
        })
        println("MyApplicationService}")
        AppExecutorUtil.getAppScheduledExecutorService().schedule({
            codingReminderTask()
            jsTask()
        }, 1, TimeUnit.MINUTES)
    }

    open fun jsTask() {
        c0--
        if (c0 > 0) {
            return
        }
        ApplicationManager.getApplication().runReadAction {
            JsService.getInstance(ProjectUtils.currProject).loadIndex()
        }
        c0 = 10
    }

    open fun codingReminderTask() {
        try {
            c1--
            if (c1 > 0) {
                return
            }
            println("codingReminder")
            val gitRecords = HttpHelper().getGitRecordList()
            println(gitRecords)
            c1 = CodeSettingsState.instance.getRate()
        } catch (e: Exception) {
            try {
                Notify.showErrorNotification(
                        "" + e.message,
                        ProjectUtils.currProject, "${Constant.setttingName}：", 3
                )
            } catch (e: Exception) {
            }
        }
    }


    companion object {
        var c0: Int = 0
        var c1: Int = 0

        val instance: MyApplicationService
            get() = ServiceManager.getService(MyApplicationService::class.java)
    }
}
