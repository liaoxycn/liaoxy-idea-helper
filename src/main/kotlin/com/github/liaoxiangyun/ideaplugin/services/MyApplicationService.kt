package com.github.liaoxiangyun.ideaplugin.services

import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import com.github.liaoxiangyun.ideaplugin.coderaminder.settings.CodeSettingsState
import com.github.liaoxiangyun.ideaplugin.coderaminder.util.HttpHelper
import com.github.liaoxiangyun.ideaplugin.common.util.Notify
import com.github.liaoxiangyun.ideaplugin.common.util.ProjectUtils
import com.github.liaoxiangyun.ideaplugin.js.service.JsService
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.util.concurrency.AppExecutorUtil
import java.io.Closeable
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class MyApplicationService : Closeable {
    private var schedule: ScheduledFuture<*>? = null

    init {
        println("【MyApplicationService,,】 init")
        execTask()
    }

    private fun execTask() {
        println("#MyApplicationService execTask")
        schedule = AppExecutorUtil.getAppScheduledExecutorService().schedule({
            codingReminderTask()
            jsTask()
        }, 1, TimeUnit.MINUTES)
    }

    override fun close() {
        val cancel = schedule?.cancel(true)
        println("#MyApplicationService close=$cancel")
    }

    private fun jsTask() {
        c0--
        if (c0 > 0) {
            return
        }
        ApplicationManager.getApplication().runReadAction {
            JsService.getInstance(ProjectUtils.currProject).loadModelsIndex()
        }
        c0 = 10
    }

    private fun codingReminderTask() {
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
        var c0: Int = 10
        var c1: Int = 10

        val instance: MyApplicationService
            get() = ServiceManager.getService(MyApplicationService::class.java)
    }
}
