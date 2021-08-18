package com.github.liaoxiangyun.ideaplugin.javascript.service

import com.github.liaoxiangyun.ideaplugin.javascript.constant.Icons
import com.github.liaoxiangyun.ideaplugin.javascript.model.Dispatch
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
    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>) {
        //筛选 dispatch
        if (element.textLength == 8 && element is LeafPsiElement
                && element.text == "dispatch") {
            val dispatch = getDispatch(element)
            if (dispatch == null || !dispatch.valid) {
                return
            }
            println("========= dispatch = $dispatch")
            val jsService = JsService.getInstance(element.project)
            val jsFile = jsService.getJSFile(dispatch.namespace) ?: return
            val modelsFunc = jsService.getModelsFunc(jsFile, dispatch.function)
            //构建导航图标的builder
            val builder = NavigationGutterIconBuilder.create(Icons.down)
                    .setAlignment(GutterIconRenderer.Alignment.CENTER) //target是xmlTag
                    .setTargets(modelsFunc ?: jsFile)
                    .setTooltipTitle("导航到models function")
            val lineMarkerInfo = builder.createLineMarkerInfo(
                    Objects.requireNonNull(dispatch.typePsi.firstChild))
            result.add(lineMarkerInfo)
        }
    }

    companion object {
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