package com.github.liaoxiangyun.ideaplugin.java.model

import com.intellij.psi.PsiClass

class PsiModel(psiClass: PsiClass) {

    var name = ""

    var fieldList = arrayListOf<ModelField>()


    init {
        var superClass = psiClass
        while (superClass.qualifiedName != "java.lang.object") {
            val fields = superClass.fields
            for (field in fields) {
                val modelField = ModelField(field)
                if (!fieldList.contains(modelField)) {
                    fieldList.add(modelField)
                }
            }
            superClass = superClass.superClass ?: break
        }
    }
}