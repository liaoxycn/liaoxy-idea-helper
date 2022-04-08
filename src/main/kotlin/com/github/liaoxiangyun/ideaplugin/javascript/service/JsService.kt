package com.github.liaoxiangyun.ideaplugin.javascript.service

import com.github.liaoxiangyun.ideaplugin.javascript.model.Dispatch
import com.intellij.lang.ecmascript6.psi.ES6ExportDefaultAssignment
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSFunctionProperty
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.impl.JSObjectLiteralExpressionImpl
import com.intellij.lang.javascript.psi.impl.JSPropertyImpl
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.text.StringUtilRt
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import java.lang.ref.WeakReference

class JsService(private val project: Project) {
    //是否umi项目
    var isUmi: Boolean = false
    var total: Int = 0
    val modelsMap: MutableMap<String, WeakReference<JSFile>> = mutableMapOf()
    val modelsPathMap: MutableMap<String, WeakReference<JSFile>> = mutableMapOf()
    private val dispatchMap: MutableMap<String, MutableSet<PsiElement>> = mutableMapOf()
    private var time: Long = 0

    init {
        println("============================================================")
        println("【${project.name}】 #JsService init ")
        loadModelsIndex()
    }

    fun clearMap() {
        total = 0
        modelsMap.clear()
        modelsPathMap.clear()
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

    fun getJSFileBy(key: String): JSFile? {
        val ref = modelsMap[key]
        return ref?.get()
    }

    private fun searchModelsDir(dir: PsiDirectory) {
        for (child in dir.subdirectories) {
            if (child.name == MODELS) {
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
        val namespace = getNamespace(jsFile)
        if (namespace.isNotBlank()) {
            val weakReference = WeakReference(jsFile)
            val moduleName = getModulePath(jsFile.virtualFile)
            modelsMap[namespace] = weakReference
            modelsMap["${moduleName}:${namespace}"] = weakReference
            modelsPathMap[jsFile.virtualFile.path] = weakReference
            total++
            println("#Find models namespace=$namespace jsFile=$jsFile")
        }
    }

    open fun getModelsFunc(jsFile: JSFile?, func: String): PsiElement? {
        if (jsFile == null) return null
        val export = PsiTreeUtil.getChildOfType(jsFile, ES6ExportDefaultAssignment::class.java) ?: return null
        val jsObj = PsiTreeUtil.getChildOfType(export, JSObjectLiteralExpression::class.java) ?: return null
        for (jsP in PsiTreeUtil.getChildrenOfType(jsObj, JSPropertyImpl::class.java)) {
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
        return null
    }

    private fun loadloadModelsIndexBy(dir: PsiDirectory?) {
        val src = dir?.findSubdirectory(SRC) ?: return
        val models = src.findSubdirectory(MODELS)
        if (models != null) {
            searchModelJSFile(models)
        }
        val pages = src.findSubdirectory(PAGES)
        if (pages != null) {
            searchModelsDir(pages)
        }
    }

    fun loadModelsIndex(): String {
        val dumb = DumbService.getInstance(project).isDumb
        println("#loadIndex project=${project.name} dumb=$dumb")
        if (!dumb) {
            val s = System.currentTimeMillis()
            this.clearMap()
            val psiManager = PsiManager.getInstance(project)
            val virtualFile = project.guessProjectDir() ?: return ""
            val directory = psiManager.findDirectory(virtualFile) ?: return ""
            directory.findFile(UMI_CON) ?: return ""
            isUmi = true
            loadloadModelsIndexBy(directory)

            val modules = ModuleManager.getInstance(project).modules.filter { it.name != virtualFile.name }
            for (module in modules) {
                if (!(module.isLoaded && !module.isDisposed)) return continue
                val moduleFile = module.moduleFile ?: continue
                val directory1 = psiManager.findDirectory(moduleFile.parent) ?: continue
                loadloadModelsIndexBy(directory1)
            }
            val l = System.currentTimeMillis() - s
            val msg = "找到${this.total}个model，共耗时${l}ms"
            println("【loadIndex】 $msg")
            println(this.modelsMap)
            return msg
        }
        return ""
    }

    companion object {
        private const val REDUCERS: String = "reducers"
        private const val SUBSCRIPTIONS: String = "subscriptions"
        private const val EFFECTS: String = "effects"
        private const val STATE: String = "state"
        private const val NAMESPACE: String = "namespace"
        private const val MODELS: String = "models"
        private const val PAGES: String = "pages"
        private const val SRC: String = "src"
        private const val UMI_CON: String = ".umirc.js"

        open fun getInstance(project: Project): JsService {
            return ServiceManager.getService(project, JsService::class.java)
        }

        open fun getNamespace(jsFile: JSFile): String {
            val export = PsiTreeUtil.getChildOfType(jsFile, ES6ExportDefaultAssignment::class.java) ?: return ""
            val jsObj = PsiTreeUtil.getChildOfType(export, JSObjectLiteralExpression::class.java) ?: return ""
            return jsObj.findProperty(NAMESPACE)?.value?.text?.let { StringUtilRt.unquoteString(it) } ?: ""
        }

        open fun getModulePath(file: VirtualFile?): String {
            if (file == null) return ""
            var temp: VirtualFile? = file
            while (true) {
                val parent = temp?.parent
                if (parent?.name == PAGES || parent?.name == SRC) {
                    return if (temp?.name == MODELS) {
                        parent.path
                    } else if (temp?.isDirectory == true) {
                        temp.path
                    } else {
                        parent.path
                    }
                }
                temp = parent
            }
            return ""
        }
    }
}