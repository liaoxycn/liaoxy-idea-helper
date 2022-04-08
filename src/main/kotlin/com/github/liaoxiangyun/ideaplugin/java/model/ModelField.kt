package com.github.liaoxiangyun.ideaplugin.java.model

import com.github.liaoxiangyun.ideaplugin.common.util.StringUtils
import com.github.liaoxiangyun.ideaplugin.java.constant.Constant.Companion.IGNORE_NAME
import com.github.liaoxiangyun.ideaplugin.java.constant.Constant.Companion.SWAGGER_NAME
import com.github.liaoxiangyun.ideaplugin.java.constant.Constant.Companion.SWAGGER_P1
import com.github.liaoxiangyun.ideaplugin.java.constant.Constant.Companion.SWAGGER_P2
import com.intellij.psi.PsiField
import com.intellij.psi.impl.source.javadoc.PsiDocTokenImpl
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.util.PsiTreeUtil

class ModelField(field: PsiField) {

    /**字段名*/
    var name: String = ""

    /**注释*/
    var comment: String = ""

    /**文档注释*/
    private var docComment: String = ""

    /**swagger注释*/
    private var swaggerComment: String = ""

    /**忽略*/
    var ignore: Boolean = false

    var dbTableColumn: String = ""
        get() = "f" + StringUtils.camelToSlash(name)


    override fun toString(): String {
        return "$name(${comment}) ignore=$ignore"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelField

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    init {
        name = field.name
        //获取文档注释
        field.docComment?.let {
            val psiDocTokenImpl = PsiTreeUtil.findChildOfType(it.context, PsiDocTokenImpl::class.java)
            if (psiDocTokenImpl != null) {
                val siblingForward = PsiTreeUtil.findSiblingForward(it, ElementType.DOC_COMMENT_DATA, true, null)
                this.docComment = siblingForward?.text ?: ""
            }
        }


        //判断是否忽略
        field.annotations?.let {
            for (annotation in it) {
                val name = annotation.qualifiedName
                val node = annotation.node
                val text = annotation.text
                println("annotation text=${text}")
                if (IGNORE_NAME == name && text.contains("exist = false")) {
                    ignore = true
                }
                //获取swagger注释
                if (SWAGGER_NAME == name) {
                    val matcher1 = SWAGGER_P1.matcher(text)
                    var group1 = ""
                    if (matcher1.find()) {
                        group1 = matcher1.group(1) ?: ""
                    }
                    val matcher2 = SWAGGER_P2.matcher(text)
                    var group2 = ""
                    if (matcher2.find()) {
                        group2 = matcher2.group(1) ?: ""
                    }
                    this.swaggerComment = group1.ifBlank { group2 }
                }
            }
        }


        //优先swagger注释
        comment = swaggerComment.ifBlank { this.docComment }
    }

}