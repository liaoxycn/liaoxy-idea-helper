package org.jetbrains.plugins.template.commit

import com.github.liaoxiangyun.myideaplugin.commit.CommitMessage
import com.github.liaoxiangyun.myideaplugin.commit.CommitPanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent

/**
 * @author Damien Arrachequesne
 */
class CommitDialog(project: Project?, commitMessage: CommitMessage?) : DialogWrapper(project) {
    private val panel: CommitPanel
    override fun createCenterPanel(): JComponent? {
        return panel.mainPanel
    }

    val commitMessage: CommitMessage
        get() = panel.commitMessage

    init {
        panel = project?.let { CommitPanel(it, commitMessage) }!!
        title = "Commit"
        setOKButtonText("OK")
        init()
    }
}