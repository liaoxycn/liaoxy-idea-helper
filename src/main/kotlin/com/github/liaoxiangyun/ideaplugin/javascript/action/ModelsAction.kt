package com.github.liaoxiangyun.ideaplugin.javascript.action

import com.github.liaoxiangyun.ideaplugin.javascript.service.JsService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class ModelsAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project!!
        val jsService = JsService.getInstance(project)
        val msg = jsService.loadModelsIndex()

        val title = "扫描结果"
        Messages.showMessageDialog(project, msg, title, Messages.getInformationIcon())
    }
}