package com.github.liaoxiangyun.ideaplugin.commit

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.CommitMessageI
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.ui.Refreshable
import org.jetbrains.plugins.template.commit.CommitDialog

class CreateCommitAction : AnAction(), DumbAware {
    override fun actionPerformed(actionEvent: AnActionEvent) {
        println("==== actionPerformed")
        val commitPanel = getCommitPanel(actionEvent) ?: return
        val commitMessage = parseExistingCommitMessage(commitPanel)
        val dialog = CommitDialog(actionEvent.project, commitMessage)
        dialog.show()
        println("==== dialog.show()   dialog.exitCode=" + dialog.exitCode + "   DialogWrapper.OK_EXIT_CODE=" + DialogWrapper.OK_EXIT_CODE)
        if (dialog.exitCode == DialogWrapper.OK_EXIT_CODE) {
            commitPanel.setCommitMessage(dialog.commitMessage.toString())
        }
        println(dialog.commitMessage)
    }

    private fun parseExistingCommitMessage(commitPanel: CommitMessageI): CommitMessage? {
        println("==== parseExistingCommitMessage")
        if (commitPanel is CheckinProjectPanel) {
            val commitMessageString = commitPanel.commitMessage
            return CommitMessage.parse(commitMessageString)
        }
        return null
    }

    companion object {
        private fun getCommitPanel(e: AnActionEvent?): CommitMessageI? {
            println("==== getCommitPanel")
            if (e == null) {
                return null
            }
            val data = Refreshable.PANEL_KEY.getData(e.dataContext)
            return if (data is CommitMessageI) {
                data
            } else VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(e.dataContext)
        }
    }
}
