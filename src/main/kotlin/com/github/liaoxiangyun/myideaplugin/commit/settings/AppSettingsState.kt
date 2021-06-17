package com.github.liaoxiangyun.myideaplugin.commit.settings

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
@State(name = "com.github.liaoxiangyun.myideaplugin.commit.settings.AppSettingsState", storages = [Storage("SdkSettingsPlugin.xml")])
class AppSettingsState : PersistentStateComponent<AppSettingsState> {

    var userId = "John Q. Public"
    var ideaStatus = false

    var origin = "http://zentaopro.szewec.com/"
    var cookie = ""

    var taskList = arrayListOf<String>()
    var bugList = arrayListOf<String>()
    var storyList = arrayListOf<String>()

    /**
     * 责任人
     */
    var responsiblePerson = "张三"

    /**
     * 检视人
     */
    var inspector = "李四"

    override fun getState(): AppSettingsState {
        return this
    }

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: AppSettingsState
            get() = ServiceManager.getService(AppSettingsState::class.java)
    }

    init {
    }
}