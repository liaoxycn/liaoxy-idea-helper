package com.github.liaoxiangyun.ideaplugin.javascript.provider

import com.github.liaoxiangyun.ideaplugin.common.util.ProjectUtils
import com.github.liaoxiangyun.ideaplugin.javascript.constant.Icons
import com.github.liaoxiangyun.ideaplugin.javascript.service.JsService
import com.github.liaoxiangyun.ideaplugin.javascript.setting.JsSettingsState
import com.intellij.ide.IconProvider
import com.intellij.lang.javascript.psi.impl.JSFileImpl
import com.intellij.psi.PsiElement
import javax.swing.Icon

/**
 * model文件图标修改为m图标
 */
class ModelIconProvider : IconProvider() {
    private var jsService: JsService? = null
    fun getJsService(): JsService? {
        if (jsService == null) {
            jsService = JsService.getInstance(ProjectUtils.currProject)
        }
        return jsService
    }

    private val instance: JsSettingsState = JsSettingsState.instance
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        if (!instance.modelIcon || element !is JSFileImpl) {
            return null
        }
        val name = element.name
        if (name.length > 2 && name.contains(".")) {
            val namespace = name.split(".")[0]
            val modelsMap = getJsService()?.getModelsMap()
            if (namespace.isNotBlank() && modelsMap != null && modelsMap[namespace] != null) {
                return Icons.model
            }
        }
        return null
    }
}