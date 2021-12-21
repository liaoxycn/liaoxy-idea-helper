package com.github.liaoxiangyun.ideaplugin.javascript.setting

import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Provides controller functionality for application settings.
 */
class JsSettingsConfigurable : Configurable {
    private var mySettingsComponent: JsSettingComponent? = null

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
        mySettingsComponent = JsSettingComponent()
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings = JsSettingsState.instance
        println("mySettingsComponent!!.enableVal = ${mySettingsComponent!!.enableVal}")
        var modified = false
        modified = mySettingsComponent!!.enableVal != settings.enableStatus
        //
        println("modified = $modified")
        return modified
    }

    override fun apply() {
        val settings = JsSettingsState.instance
        settings.enableStatus = mySettingsComponent!!.enableVal
    }

    override fun reset() {
        val settings = JsSettingsState.instance
        mySettingsComponent!!.enableVal = settings.enableStatus
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}