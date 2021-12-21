package com.github.liaoxiangyun.ideaplugin.javascript.setting

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * Supports storing the application settings in a persistent way.
 * The [State] and [Storage] annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(name = "com.github.liaoxiangyun.ideaplugin.js.settings.JsSettingsState", storages = [Storage("JsSettingsState.xml")])
class JsSettingsState : PersistentStateComponent<JsSettingsState> {
    var enableStatus = false

    override fun getState(): JsSettingsState {
        return this
    }

    override fun loadState(state: JsSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }


    companion object {
        val instance: JsSettingsState
            get() = ServiceManager.getService(JsSettingsState::class.java)
    }

}