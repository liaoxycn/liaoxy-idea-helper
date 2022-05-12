package com.github.liaoxiangyun.ideaplugin.coderaminder.settings

import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import com.github.liaoxiangyun.ideaplugin.coderaminder.util.CalendarUtil
import com.github.liaoxiangyun.ideaplugin.common.services.MyApplicationService
import com.intellij.openapi.options.Configurable
import org.gitlab.api.GitlabAPI
import org.jetbrains.annotations.Nls
import java.util.regex.Pattern
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
        modified = modified or (mySettingsComponent!!.titleIgnoreText != settings.titleIgnore)
        modified = modified or (mySettingsComponent!!.messageIgnoreText != settings.messageIgnore)
        modified = modified or (mySettingsComponent!!.branchesText != settings.branches)
        modified = modified or (mySettingsComponent!!.enableStatus != settings.enableStatus)
        modified = modified or (mySettingsComponent!!.dailyReportStatus != settings.dailyReport)
        return modified
    }

    override fun apply() {
        val settings = CodeSettingsState.instance
        settings.userId = mySettingsComponent!!.userNameText
        settings.origin = mySettingsComponent!!.originText
        settings.token = mySettingsComponent!!.tokenText
        settings.calendar = mySettingsComponent!!.calendarText
        settings.reTime = mySettingsComponent!!.reTimeText
        settings.titleIgnore = mySettingsComponent!!.titleIgnoreText
        settings.messageIgnore = mySettingsComponent!!.messageIgnoreText
        settings.branches = mySettingsComponent!!.branchesText
        settings.enableStatus = mySettingsComponent!!.enableStatus
        settings.dailyReport = mySettingsComponent!!.dailyReportStatus


        handler(settings)
    }

    private fun handler(state: CodeSettingsState) {
        val settings = CodeSettingsState.instance
        val gitlabAPI = GitlabAPI.connect(state.origin, state.token)

        Pattern.compile(state.titleIgnore)
        Pattern.compile(state.messageIgnore)
        settings.gitlabUser = gitlabAPI.user
        MyApplicationService.instance.codeStatisticsTask()
    }

    override fun reset() {
        val settings = CodeSettingsState.instance
        mySettingsComponent!!.userNameText = settings.userId
        mySettingsComponent!!.originText = settings.origin
        mySettingsComponent!!.tokenText = settings.token
        mySettingsComponent!!.calendarText = settings.calendar.ifBlank { CalendarUtil.getDefaultContent() };
        mySettingsComponent!!.reTimeText = settings.reTime
        mySettingsComponent!!.titleIgnoreText = settings.titleIgnore
        mySettingsComponent!!.messageIgnoreText = settings.messageIgnore
        mySettingsComponent!!.branchesText = settings.branches
        mySettingsComponent!!.enableStatus = settings.enableStatus
        mySettingsComponent!!.dailyReportStatus = settings.dailyReport
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}