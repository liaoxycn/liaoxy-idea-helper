package com.github.liaoxiangyun.ideaplugin.javascript.provider

import com.github.liaoxiangyun.ideaplugin.javascript.constant.Icons
import com.github.liaoxiangyun.ideaplugin.javascript.service.JsService
import com.github.liaoxiangyun.ideaplugin.javascript.setting.JsSettingsState
import com.intellij.ide.IconProvider
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.psi.PsiElement
import javax.swing.Icon

/**
 * model文件图标修改为m图标
 */
class ModelIconProvider : IconProvider() {
    private val instance: JsSettingsState = JsSettingsState.instance
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        if (!instance.modelIcon) {
            return null
        }
        if (element !is JSFile) {
            return null
        }
        val namespace = JsService.getNamespace(element)
        if (namespace.isNotBlank()) {
            return Icons.model
        }
        return null
    }
}