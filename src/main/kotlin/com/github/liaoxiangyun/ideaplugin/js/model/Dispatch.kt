package com.github.liaoxiangyun.ideaplugin.js.model

import com.intellij.openapi.util.text.StringUtilRt
import com.intellij.psi.PsiElement

class Dispatch(typePsi: PsiElement, type: String?) {
    var valid: Boolean = false
    var typePsi: PsiElement
    var type: String? = ""
    var namespace: String = ""
    var function: String = ""

    override fun toString(): String {
        return if (valid) "{ typePsi=${typePsi}, type=${type}}" else ""
    }

    init {
        var type = type
        this.typePsi = typePsi
        this.type = type
        if (type != null && StringUtilRt.isQuotedString(type) && type.contains("/")) {
            type = StringUtilRt.unquoteString(type)
            val split = type.split("/".toRegex()).toTypedArray()
            if (split.size > 1) {
                namespace = split[0]
                function = split[1]
            }
            valid = true
        }
    }
}