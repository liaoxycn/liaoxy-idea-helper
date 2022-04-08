package com.github.liaoxiangyun.ideaplugin.java.setting

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
@State(name = "com.github.liaoxiangyun.ideaplugin.java.setting.SettingsState", storages = [Storage("JavaSettingsState.xml")])
class JavaSettingsState : PersistentStateComponent<JavaSettingsState> {
    var ewEnable = false


    override fun getState(): JavaSettingsState {
        return this
    }

    override fun loadState(state: JavaSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }


    companion object {
        val instance: JavaSettingsState
            get() = ServiceManager.getService(JavaSettingsState::class.java)
    }

}