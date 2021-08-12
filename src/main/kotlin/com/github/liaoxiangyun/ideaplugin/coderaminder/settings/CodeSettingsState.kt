package com.github.liaoxiangyun.ideaplugin.coderaminder.settings

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
@State(name = "com.github.liaoxiangyun.ideaplugin.coderaminder.settings.CodeSettingsState", storages = [Storage("coderaminder.xml")])
class CodeSettingsState : PersistentStateComponent<CodeSettingsState> {

    var userId = "John Q. Public"
    var enableStatus = false

    var origin = "http://gitlab.szewec.com/e.liaoxiangyun"
    var session = "xxx"

    /**
     * 间隔天数
     */
    var days = "1"

    /**
     * 时间
     */
    var reTimeStr = "17:00-18:30"

    /**
     * 提醒频率
     */
    var rateStr = "10"

    fun getRate(): Int {
        try {
            val valueOf = Integer.valueOf(rateStr)
            if (valueOf > 0) {
                return (valueOf * 1000 * 60)
            }
        } catch (e: Exception) {
        }
        return (1 * 1000 * 60)
    }


    override fun getState(): CodeSettingsState {
        return this
    }

    override fun loadState(state: CodeSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    override fun toString(): String {
        return "CodeSettingsState(userId='$userId', enableStatus=$enableStatus, origin='$origin', session='$session', days='$days', reTimeStr='$reTimeStr')"
    }

    companion object {
        val instance: CodeSettingsState
            get() = ServiceManager.getService(CodeSettingsState::class.java)
    }

}