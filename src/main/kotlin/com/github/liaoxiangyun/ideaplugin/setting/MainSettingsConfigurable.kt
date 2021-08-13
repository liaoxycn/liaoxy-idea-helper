package com.github.liaoxiangyun.ideaplugin.setting

import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Provides controller functionality for application settings.
 */
class MainSettingsConfigurable : Configurable {
    private var mySettingsComponent: MainSettingComponent? = null

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
        mySettingsComponent = MainSettingComponent()
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun apply() {
    }

    override fun reset() {
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}