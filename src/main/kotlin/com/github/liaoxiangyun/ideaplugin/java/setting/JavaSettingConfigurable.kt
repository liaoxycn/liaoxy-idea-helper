package com.github.liaoxiangyun.ideaplugin.java.setting

import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Provides controller functionality for application settings.
 */
class JavaSettingConfigurable : Configurable {
    private var mySettingsComponent: JavaSettingComponent? = null

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
        mySettingsComponent = JavaSettingComponent()
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings = JavaSettingsState.instance
        return mySettingsComponent!!.ewEnableVal != settings.ewEnable
    }

    override fun apply() {
        val settings = JavaSettingsState.instance
        settings.ewEnable = mySettingsComponent!!.ewEnableVal
    }

    override fun reset() {
        val settings = JavaSettingsState.instance
        mySettingsComponent!!.ewEnableVal = settings.ewEnable
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}