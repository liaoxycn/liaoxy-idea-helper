package com.github.liaoxiangyun.ideaplugin.common.services

import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import com.github.liaoxiangyun.ideaplugin.coderaminder.settings.CodeSettingsState
import com.github.liaoxiangyun.ideaplugin.coderaminder.util.CalendarUtil
import com.github.liaoxiangyun.ideaplugin.coderaminder.util.HttpHelper
import com.github.liaoxiangyun.ideaplugin.common.util.Notify
import com.github.liaoxiangyun.ideaplugin.common.util.ProjectUtils
import com.intellij.openapi.components.ServiceManager
import com.intellij.ui.SystemNotifications
import com.intellij.util.concurrency.AppExecutorUtil
import java.io.Closeable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class MyApplicationService : Closeable {
    private var executorService: ScheduledExecutorService = AppExecutorUtil.getAppScheduledExecutorService()


    private fun execTask() {
        println("#MyApplicationService execTask")
        codeStatisticsTask()
    }

    @Volatile
    private var codeStatisticsTask: ScheduledFuture<*>? = null

    @Synchronized
    open fun codeStatisticsTask() {
        //有任务先取消
        codeStatisticsTask?.cancel(true)
        if (!CodeSettingsState.instance.dailyReport) return
        try {
            //新建任务
            //确定时间
            val reTime = CodeSettingsState.instance.reTime.trim()
            val list = CalendarUtil.getWeekDays(0).filter { !CalendarUtil.isOffDay(it) }
            val date: LocalDate = list[list.size - 1]
            val now = LocalDateTime.now()
            val time = LocalDateTime.of(date, CalendarUtil.parseTime(reTime))
            var dateTime: LocalDateTime = if (now >= time) {//时间已过，算下一周
                val list2 = CalendarUtil.getWeekDays(1).filter { !CalendarUtil.isOffDay(it) }
                val date2: LocalDate = list2[list.size - 1]
                LocalDateTime.of(date2, CalendarUtil.parseTime(reTime))
            } else {
                time
            }
            Notify.showWarnNotification("将在 ${dateTime.format(Constant.FORMATTER)} 运行统计任务",
                    ProjectUtils.currProject, Constant.setttingName, 1)

            /**
             * 所有schedule方法都接受相对延迟和周期作为参数，而不是绝对时间或日期。
             * 将表示为java.util.Date的绝对时间转换为所需形式是一件简单的事情。
             * 例如，要安排在某个未来的date ，您可以使用： schedule(task,
             * date.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS) 。
             */
            //建立周期性任务
            val dayMS = 24 * 60 * 60 * 1000
            val initialDelay = dateTime.toEpochSecond(ZoneOffset.of("+8")) * 1000 - System.currentTimeMillis()

            this.codeStatisticsTask = executorService.schedule({
                val messages = HttpHelper().getSummary2().messages
                msg("${Constant.setttingName}：", messages)
                if (CodeSettingsState.instance.dailyReport) {
                    codeStatisticsTask()
                }
            }, initialDelay, TimeUnit.MILLISECONDS)

        } catch (e: Exception) {
            try {
                msg("${Constant.setttingName}：", "${e.message}")
            } catch (e: Exception) {
            }
        }
    }

    private fun msg(title: String, content: String) {
        SystemNotifications.getInstance().notify(
                "IDEA助手",
                "${title}：", "${content}"
        )
        Notify.showSuccessNotification(
                content,
                ProjectUtils.currProject, "${title}：", 2
        )
    }


    //非业务代码
    init {
        println("【MyApplicationService,,】 init")
        executorService.schedule({
            execTask()
        }, 3, TimeUnit.MINUTES)
    }

    override fun close() {
        try {
            executorService.shutdownNow()
        } catch (e: Exception) {
        }
    }

    companion object {

        val instance: MyApplicationService
            get() = ServiceManager.getService(MyApplicationService::class.java)
    }
}
