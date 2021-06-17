package com.github.liaoxiangyun.myideaplugin.commit.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Provides controller functionality for application settings.
 */
class AppSettingsConfigurable : Configurable {
    private var mySettingsComponent: AppSettingsComponent? = null

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP
    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Git提交模版配置"
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return mySettingsComponent!!.preferredFocusedComponent
    }

    override fun createComponent(): JComponent? {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings = AppSettingsState.instance
        var modified = mySettingsComponent!!.userNameText != settings.userId
        modified = modified or (mySettingsComponent!!.ideaUserStatus != settings.ideaStatus)
        modified = modified or (mySettingsComponent!!.originText != settings.origin)
        modified = modified or (mySettingsComponent!!.cookieText != settings.cookie)
        modified = modified or (mySettingsComponent!!.responsiblePersonText != settings.responsiblePerson)
        modified = modified or (mySettingsComponent!!.inspectorText != settings.inspector)
        return modified
    }

    override fun apply() {
        val settings = AppSettingsState.instance
        settings.userId = mySettingsComponent!!.userNameText
        settings.ideaStatus = mySettingsComponent!!.ideaUserStatus
        settings.origin = mySettingsComponent!!.originText
        settings.cookie = mySettingsComponent!!.cookieText
        settings.responsiblePerson = mySettingsComponent!!.responsiblePersonText
        settings.inspector = mySettingsComponent!!.inspectorText
    }

    override fun reset() {
        val settings = AppSettingsState.instance
        mySettingsComponent!!.userNameText = settings.userId
        mySettingsComponent!!.ideaUserStatus = settings.ideaStatus
        mySettingsComponent!!.originText = settings.origin
        mySettingsComponent!!.cookieText = settings.cookie
        mySettingsComponent!!.responsiblePersonText = settings.responsiblePerson
        mySettingsComponent!!.inspectorText = settings.inspector
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}