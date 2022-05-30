package com.github.liaoxiangyun.ideaplugin.javascript.service

import com.github.liaoxiangyun.ideaplugin.javascript.constant.Icons
import com.github.liaoxiangyun.ideaplugin.javascript.model.Dispatch
import com.github.liaoxiangyun.ideaplugin.javascript.setting.JsSettingsState
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.lang.javascript.psi.impl.JSCallExpressionImpl
import com.intellij.lang.javascript.psi.impl.JSObjectLiteralExpressionImpl
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import java.util.*

class DispatchLineMarkerProvider : RelatedItemLineMarkerProvider() {
    init {
        setting = JsSettingsState.instance
    }

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>
    ) {
        //筛选 dispatch
        if (element is LeafPsiElement && element.textLength == 8 && element.text == "dispatch") {
            dispatchMarkers(element, result)
        } else if (element is LeafPsiElement && element.textLength == 7 && element.text == "current") {
            refMarkers(element, result)
        }
    }

    private fun refMarkers(element: LeafPsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>) {
        if (!setting.refStatus) {
            return
        }

    }


    private fun dispatchMarkers(element: LeafPsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>) {
        if (!setting.enableStatus) {
            return
        }
        val dispatch = getDispatch(element)
        if (dispatch == null || !dispatch.valid) {
            return
        }
        val moduleName = JsService.getModulePath(element.containingFile.virtualFile)
        val jsService = JsService.getInstance(element.project)
        if (!jsService.isUmi) {
            return
        }
        val jsFile = jsService.getJSFileBy("$moduleName:${dispatch.namespace}") ?: return
        val modelsFunc = jsService.getModelsFunc(jsFile, dispatch.function)
        //构建跳转图标的builder
        val builder = NavigationGutterIconBuilder.create(Icons.down)
            .setAlignment(GutterIconRenderer.Alignment.CENTER) //target是xmlTag
            .setTargets(modelsFunc ?: jsFile)
            .setTooltipTitle("跳转到models function")
        val lineMarkerInfo = builder.createLineMarkerInfo(
            Objects.requireNonNull(dispatch.typePsi.firstChild)
        )
        result.add(lineMarkerInfo)
    }


    companion object {
        var setting: JsSettingsState = JsSettingsState()
        private fun getDispatch(element: LeafPsiElement): Dispatch? {
            val c1 = element.context
            if (c1 == null || c1 !is JSReferenceExpressionImpl) {
                return null
            }
            val c2 = c1.getContext()
            if (c2 == null || c2 !is JSCallExpressionImpl) {
                return null
            }
            val argumentList = c2.argumentList ?: return null
            val arguments = argumentList.arguments
            if (arguments == null || arguments.isEmpty() || arguments[0] !is JSObjectLiteralExpressionImpl) {
                return null
            }
            val params = arguments[0] as JSObjectLiteralExpressionImpl
            val type = params.findProperty("type") ?: return null
            val value = type.value ?: return null
            return Dispatch(type, value.text)
        }
    }
}