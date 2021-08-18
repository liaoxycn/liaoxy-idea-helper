package com.github.liaoxiangyun.ideaplugin.javascript.service

import com.github.liaoxiangyun.ideaplugin.javascript.model.Dispatch
import com.intellij.lang.ecmascript6.psi.ES6ExportDefaultAssignment
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSFunctionProperty
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.impl.JSObjectLiteralExpressionImpl
import com.intellij.lang.javascript.psi.impl.JSPropertyImpl
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.text.StringUtilRt
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager

class JsService(private val project: Project) {
    //是否umi项目
    private var isUmi: Boolean = false
    private val modelsMap: MutableMap<String, JSFile> = mutableMapOf()
    private val dispatchMap: MutableMap<String, MutableSet<PsiElement>> = mutableMapOf()
    private var time: Long = 0

    init {
        println("============================================================")
        println("【${project.name}】 #JsService init ")
        loadModelsIndex()
    }

    fun addDispatch(dispatch: Dispatch) {
        if (dispatch.valid) {
            var list = dispatchMap[dispatch.type!!]
            if (list == null) {
                list = mutableSetOf()
            }
            list.add(dispatch.typePsi)
            dispatchMap[dispatch.type!!] = list
        }
    }

    fun getJSFile(namespace: String): JSFile? {
        return modelsMap[namespace]
    }

    private fun searchModelsDir(dir: PsiDirectory) {
        for (child in dir.subdirectories) {
            if (child.name == models) {
                println("#Dir  P:${dir.name}  T:y  C:${child.name}")
                searchModelJSFile(child)
            } else if (!child.name.startsWith(".")) {
                println("#Dir  P:${dir.name}  T:n  C:${child.name}")
                searchModelsDir(child)
            }
        }
    }

    private fun searchModelJSFile(dir: PsiDirectory) {
        for (child in dir.children) {
            if (child is PsiDirectory) {
                println("#JSFile  P:${dir.name}  T:dir  C:${child.name}")
                searchModelJSFile(child)
            } else if (child is JSFile) {
                println("#JSFile  P:${dir.name}  T:js   C:${child.name}")
                addModelJSFile(child)
            } else {
                println("#JSFile  P:${dir.name}  T:other   C:${child}")
            }
        }
    }

    private fun addModelJSFile(jsFile: JSFile) {
        for (child in jsFile.children) {
            if (child is ES6ExportDefaultAssignment) { //是否 export default
                for (child in child.children) {
                    if (child is JSObjectLiteralExpression) {
                        val jsObj = child
                        val namespace = jsObj.findProperty(NAMESPACE) ?: return
                        jsObj.findProperty(STATE) ?: return
                        jsObj.findProperty(EFFECTS) ?: return
                        jsObj.findProperty(REDUCERS) ?: return
                        val text = namespace.value?.text
                        if (text != null) {
                            modelsMap[StringUtilRt.unquoteString(text)] = jsFile
                            println("#Find models namespace=$text jsFile=$jsFile")
                            continue
                        }
                    }
                }
            }
        }
    }

    open fun getModelsFunc(jsFile: JSFile?, func: String): PsiElement? {
        if (jsFile == null) return null
        for (li in jsFile.children) {
            if (li is ES6ExportDefaultAssignment) { //是否 export default
                for (exportParam in li.children) {
                    if (exportParam is JSObjectLiteralExpression) {
                        for (jsP in exportParam.children) {
                            if (jsP is JSPropertyImpl) {
                                if (jsP.name == EFFECTS || jsP.name == REDUCERS) {
                                    val jsExpression = jsP.value ?: return null
                                    if (jsExpression is JSObjectLiteralExpressionImpl) {
                                        for (functionP in jsExpression.children) {
                                            if (functionP is JSFunctionProperty) {
                                                if (functionP.name == func) {
                                                    return functionP
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    fun loadModelsIndex(): String {
        val s = System.currentTimeMillis();
        val dumb = DumbService.getInstance(project).isDumb
        println("#loadIndex project=${project.name} dumb=$dumb")
        if (!dumb) {
            val virtualFile = project.guessProjectDir() ?: return ""
            val directory = PsiManager.getInstance(project).findDirectory(virtualFile!!) ?: return ""
            val src = directory.findSubdirectory(src) ?: return ""
            directory.findFile(umirc) ?: return ""
            isUmi = true

            val models = src.findSubdirectory(models)
            if (models != null) {
                searchModelJSFile(models)
            }
            val pages = src.findSubdirectory(pages)
            if (pages != null) {
                searchModelsDir(pages)
            }
        }
        val l = System.currentTimeMillis() - s
        val msg = "找到${this.modelsMap.keys.size}个model，共耗时${l}ms"
        println("【loadIndex】 $msg")
        println(this.modelsMap)
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
        private const val umirc: String = ".umirc.js"

        open fun getInstance(project: Project): JsService {
            return ServiceManager.getService(project, JsService::class.java)
        }
    }
}