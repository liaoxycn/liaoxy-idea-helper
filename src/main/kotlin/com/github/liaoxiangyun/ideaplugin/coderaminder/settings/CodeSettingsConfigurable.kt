package com.github.liaoxiangyun.ideaplugin.coderaminder.settings

import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import com.github.liaoxiangyun.ideaplugin.common.services.MyApplicationService
import com.intellij.openapi.options.Configurable
import org.gitlab.api.GitlabAPI
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Provides controller functionality for application settings.
 */
class CodeSettingsConfigurable : Configurable {
    private var mySettingsComponent: CodeSettingsComponent? = null

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP
    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return Constant.setttingName
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return mySettingsComponent!!.preferredFocusedComponent
    }

    override fun createComponent(): JComponent? {
        mySettingsComponent = CodeSettingsComponent()
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings = CodeSettingsState.instance
        var modified = mySettingsComponent!!.userNameText != settings.userId
        modified = modified or (mySettingsComponent!!.originText != settings.origin)
        modified = modified or (mySettingsComponent!!.tokenText != settings.token)
        modified = modified or (mySettingsComponent!!.calendarText != settings.calendar)
        modified = modified or (mySettingsComponent!!.reTimeText != settings.reTime)
        modified = modified or (mySettingsComponent!!.branchesText != settings.branches)
        modified = modified or (mySettingsComponent!!.enableStatus != settings.enableStatus)
        modified = modified or (mySettingsComponent!!.dailyReportStatus != settings.dailyReport)
        modified = modified or (mySettingsComponent!!.weeklyReportStatus != settings.weeklyReport)
        return modified
    }

    override fun apply() {
        val settings = CodeSettingsState.instance
        settings.userId = mySettingsComponent!!.userNameText
        settings.origin = mySettingsComponent!!.originText
        settings.token = mySettingsComponent!!.tokenText
        settings.calendar = mySettingsComponent!!.calendarText
        settings.reTime = mySettingsComponent!!.reTimeText
        settings.branches = mySettingsComponent!!.branchesText
        settings.enableStatus = mySettingsComponent!!.enableStatus
        settings.dailyReport = mySettingsComponent!!.dailyReportStatus
        settings.weeklyReport = mySettingsComponent!!.weeklyReportStatus


        handler(settings)
    }

    private fun handler(state: CodeSettingsState) {
        val settings = CodeSettingsState.instance
        val gitlabAPI = GitlabAPI.connect(state.origin, state.token)
        settings.gitlabUser = gitlabAPI.user
        MyApplicationService.instance.codeStatisticsTask()
    }

    override fun reset() {
        val settings = CodeSettingsState.instance
        mySettingsComponent!!.userNameText = settings.userId
        mySettingsComponent!!.originText = settings.origin
        mySettingsComponent!!.tokenText = settings.token
        mySettingsComponent!!.calendarText = settings.calendar
        mySettingsComponent!!.reTimeText = settings.reTime
        mySettingsComponent!!.branchesText = settings.branches
        mySettingsComponent!!.enableStatus = settings.enableStatus
        mySettingsComponent!!.dailyReportStatus = settings.dailyReport
        mySettingsComponent!!.weeklyReportStatus = settings.weeklyReport
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}