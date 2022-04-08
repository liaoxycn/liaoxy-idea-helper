package com.github.liaoxiangyun.ideaplugin.javascript.action

import com.github.liaoxiangyun.ideaplugin.coderaminder.util.HttpHelper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class CodeStatisticsAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project!!
        val summary = HttpHelper().getSummary2()

        val title = "日均代码量统计结果（数据来自Gitlab）"
        Messages.showMessageDialog(project, summary.messages, title, Messages.getInformationIcon())
    }
}