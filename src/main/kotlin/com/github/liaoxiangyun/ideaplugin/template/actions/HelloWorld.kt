package com.github.liaoxiangyun.ideaplugin.template.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.impl.DocumentImpl
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.ui.Messages

class HelloWorld : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(PlatformDataKeys.PROJECT)
        val editor = event.getData(PlatformDataKeys.EDITOR) ?: return
        val document = editor.document as DocumentImpl
        val file = FileDocumentManager.getInstance().getFile(document)
        println("file = $file")
        println("file = ${file?.name}")

//        document.getUserData(PlatformDataKeys.)
//        document

        val title = "标题"
        val msg = "2021,起航"
        Messages.showMessageDialog(project, msg, title, Messages.getInformationIcon())
    }
}