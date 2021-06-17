package com.github.liaoxiangyun.myideaplugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.Messages

class HelloWorld : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(PlatformDataKeys.PROJECT)
        val title = "标题"
        val msg = "2021,起航"
        Messages.showMessageDialog(project, msg, title, Messages.getInformationIcon())
    }
}