package com.github.liaoxiangyun.ideaplugin.coderaminder.settings

import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import com.intellij.openapi.options.Configurable
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
        modified = modified or (mySettingsComponent!!.sessionText != settings.session)
        modified = modified or (mySettingsComponent!!.daysText != settings.days)
        modified = modified or (mySettingsComponent!!.reTimeText != settings.reTimeStr)
        modified = modified or (mySettingsComponent!!.rateText != settings.rateStr)
        modified = modified or (mySettingsComponent!!.enableStatus != settings.enableStatus)
        return modified
    }

    override fun apply() {
        val settings = CodeSettingsState.instance
        settings.userId = mySettingsComponent!!.userNameText
        settings.origin = mySettingsComponent!!.originText
        settings.session = mySettingsComponent!!.sessionText
        settings.days = mySettingsComponent!!.daysText
        settings.reTimeStr = mySettingsComponent!!.reTimeText
        settings.rateStr = mySettingsComponent!!.rateText
        settings.enableStatus = mySettingsComponent!!.enableStatus
    }

    override fun reset() {
        val settings = CodeSettingsState.instance
        mySettingsComponent!!.userNameText = settings.userId
        mySettingsComponent!!.originText = settings.origin
        mySettingsComponent!!.sessionText = settings.session
        mySettingsComponent!!.daysText = settings.days
        mySettingsComponent!!.reTimeText = settings.reTimeStr
        mySettingsComponent!!.rateText = settings.rateStr
        mySettingsComponent!!.enableStatus = settings.enableStatus
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}