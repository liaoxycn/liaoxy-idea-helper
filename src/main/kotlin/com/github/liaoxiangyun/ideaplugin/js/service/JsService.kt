package com.github.liaoxiangyun.ideaplugin.js.service

import com.intellij.lang.ecmascript6.psi.ES6ExportDefaultAssignment
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.impl.JSObjectLiteralExpressionImpl
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.text.StringUtilRt
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager

class JsService(private val project: Project) {
    private val map: MutableMap<String, JSFile> = mutableMapOf()

    init {
        loadIndex()
    }

    fun getJSFile(namespace: String): JSFile? {
        return map[namespace]
    }

    fun searchModels(dir: PsiDirectory) {
        for (child in dir.subdirectories) {
            if (child.name == models) {
                //
                searchModelJs(child)
            } else {
                searchModels(child)
            }
        }
    }

    fun searchModelJs(dir: PsiDirectory) {
        for (child in dir.children) {
            if (child is PsiDirectory) {
                searchModelJs(child)
            } else if (child is JSFile) {
                isModelJs(child)
            }
        }
    }

    private fun isModelJs(jsFile: JSFile) {
        for (child in jsFile.children) {
            if (child is ES6ExportDefaultAssignment) { //是否 export default
                if (child.lastChild is JSObjectLiteralExpression) {
                    val jsObj = child.lastChild as JSObjectLiteralExpression
                    val namespace = jsObj.findProperty(NAMESPACE) ?: return
                    val state = jsObj.findProperty(STATE) ?: return
                    val effects = jsObj.findProperty(EFFECTS) ?: return
                    val reducers = jsObj.findProperty(REDUCERS) ?: return
                    namespace.value?.text?.let {
                        map[StringUtilRt.unquoteString(it)] = jsFile
                    }
                }
            }
        }
    }

    open fun getModelsFunc(jsFile: JSFile?, func: String): PsiElement? {
        if (jsFile == null) return null
        for (child in jsFile.children) {
            if (child is ES6ExportDefaultAssignment) { //是否 export default
                if (child.lastChild is JSObjectLiteralExpression) {
                    val jsObj = child.lastChild as JSObjectLiteralExpression
                    val effects = jsObj.findProperty(EFFECTS) ?: return null
                    val reducers = jsObj.findProperty(REDUCERS) ?: return null
                    if (effects.value is JSObjectLiteralExpressionImpl) {
                        val findProperty = (effects.value as JSObjectLiteralExpressionImpl).findProperty(func)
                        if (findProperty != null) {
                            return findProperty
                        }
                    }
                    if (reducers.value is JSObjectLiteralExpressionImpl) {
                        val findProperty = (reducers.value as JSObjectLiteralExpressionImpl).findProperty(func)
                        if (findProperty != null) {
                            return findProperty
                        }
                    }
                }
            }
        }
        return null
    }

    fun loadIndex(): String {
        val s = System.currentTimeMillis();
        val dumb = DumbService.getInstance(project).isDumb
        println("#loadIndex project=${project.name} dumb=$dumb")
        if (!dumb) {
            val virtualFile = project.guessProjectDir()
            val directory = PsiManager.getInstance(project).findDirectory(virtualFile!!)
            val src = directory!!.findSubdirectory(src)
            val models = src!!.findSubdirectory(models)
            if (models != null) {
                searchModelJs(models)
            }
            val pages = src.findSubdirectory(pages)
            if (pages != null) {
                searchModels(pages)
            }
        }
        val l = System.currentTimeMillis() - s
        val msg = "找到${this.map.keys.size}个model，共耗时${l}ms"
        println(msg)
        return msg
    }

    companion object {
        private const val REDUCERS: String = "reducers"
        private const val EFFECTS: String = "effects"
        private const val STATE: String = "state"
        private const val NAMESPACE: String = "namespace"
        private const val models: String = "models"
        private const val pages: String = "pages"
        private const val src: String = "src"

        open fun getInstance(project: Project): JsService {
            return ServiceManager.getService(project, JsService::class.java)
        }
    }
}