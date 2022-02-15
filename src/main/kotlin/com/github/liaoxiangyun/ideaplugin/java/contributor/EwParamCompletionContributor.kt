package com.github.liaoxiangyun.ideaplugin.java.contributor

import com.github.liaoxiangyun.ideaplugin.java.constant.Constant.Companion.EW_CLASS
import com.github.liaoxiangyun.ideaplugin.java.model.ModelField
import com.github.liaoxiangyun.ideaplugin.java.model.PsiModel
import com.github.liaoxiangyun.ideaplugin.java.setting.JavaSettingsState
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiClassImpl
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl
import com.intellij.psi.infos.MethodCandidateInfo
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import java.awt.Color


/**
 * The type Sql param completion contributor.
 *
 * @author yanglin
 */
class EwParamCompletionContributor : CompletionContributor() {

    init {
        val patterns = PlatformPatterns.psiElement()
                .withLanguage(JavaLanguage.INSTANCE)
//                .withElementType(JavaTokenType.VAR_KEYWORD)
        extend(CompletionType.BASIC, patterns, Provider())
    }

    private class Provider : CompletionProvider<CompletionParameters>() {
        private val state: JavaSettingsState = JavaSettingsState.instance

        override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
            if (!this.state.ewEnable) return
            val offset = parameters.offset
            val originalPosition = parameters.originalPosition ?: return

            val project = parameters.editor.project ?: return
            val javaPsiFacade = JavaPsiFacade.getInstance(project)
            val resolveHelper = javaPsiFacade.resolveHelper

            var fieldList = arrayListOf<ModelField>()


            //方法调用表达式
            val psiMethodCallExpression = PsiTreeUtil.getParentOfType(originalPosition, PsiMethodCallExpressionImpl::class.java)
                    ?: return
            //当前在第几个参数上
            var index = -1
            var count = 0
            val pe = PsiTreeUtil.getParentOfType(originalPosition, PsiLiteralExpression::class.java, false)
            val el = PsiTreeUtil.getParentOfType(originalPosition, PsiExpressionList::class.java)
            if (el != null) {
                val pl = PsiTreeUtil.findChildrenOfType(el, PsiLiteralExpression::class.java)
                if (pl.isNotEmpty()) {
                    count = pl.size
                    for ((i, ex) in pl.withIndex()) {
                        if (ex == pe) {
                            index = i;
                            break
                        }
                    }
                }
            }
            println("arguments index= $index")
            if (index < 0) {
                return
            }

            val candidates = resolveHelper.getReferencedMethodCandidates(psiMethodCallExpression, true, true)
            if (candidates.isEmpty()) return
            var candidateInfo: MethodCandidateInfo? = null
            val filter = candidates.filter {
                if (it is MethodCandidateInfo) {
                    it.argumentTypes.size == count
                }
                false
            }
            candidateInfo = if (filter.isNotEmpty()) {
                filter[0] as MethodCandidateInfo
            } else {
                candidates[0] as MethodCandidateInfo
            }
            val element = candidateInfo.element
            val qualifiedName = element.containingClass?.qualifiedName
            //判断java方法
            println("candidateInfo.element.containingClass.qualifiedName =$qualifiedName")
            if (qualifiedName != EW_CLASS) {
                return
            }
            //column参数 index
//            var columnIndex = -1
            for ((j, parameter) in element.parameters.withIndex()) {
                val name = parameter.name
                val type = parameter.type
                println("parameter{name=$name, type=$type}")
//                if (name == "column") {
//                    columnIndex = j
//                }
            }
//            if (index != columnIndex) {
//                return
//            }

            for (mutableEntry in candidateInfo.substitutor.substitutionMap) {
                val key = mutableEntry.key.owner as PsiClass
                val value = mutableEntry.value as PsiClassType
                val resolve = value.resolve() as PsiClassImpl
                println("${key.qualifiedName} > ${value.canonicalText}")
                println("resolve=$resolve")
                val psiModel = PsiModel(resolve)
                fieldList = psiModel.fieldList
            }

            val editor = parameters.editor
            val document = editor.document
            val lineStartOffset = document.getLineStartOffset(document.getLineNumber(offset))
            val text = document.getText(TextRange.create(lineStartOffset, offset))

            println("#addCompletions  offset=$offset,lineStartOffset=$lineStartOffset,text=$text")
            if (fieldList.isNotEmpty()) {
                for (modelField in fieldList) {
                    if (modelField.ignore) continue
                    result.addElement(LookupElementBuilder.create(modelField.dbTableColumn)
                            .withPresentableText(modelField.name)
                            .withLookupString(modelField.name)
                            .withLookupString(modelField.comment)
                            .withCaseSensitivity(true)//大小写不敏感
                            .appendTailText("  " + modelField.dbTableColumn, true)
                            .withItemTextForeground(Color.BLACK)//一级提示文本颜色
                            .withStrikeoutness(modelField.ignore)//添加表示废弃的删除线
                            .withTypeText(modelField.comment)
                            .withInsertHandler { insertionContext, lookupElement ->
                                val document: Document = insertionContext.document
                                var startPoint: Int = insertionContext.startOffset
                                val completionChar = insertionContext.completionChar
//                                insertionContext.trackOffset()
                                var endPoint: Int = insertionContext.selectionEndOffset
                                println("startPoint=$startPoint, endPoint=$endPoint")
                                document.insertString(endPoint, "")
//                                document.replaceString(originalPosition.startOffset,
//                                        originalPosition.endOffset,
//                                        "${modelField.dbTableColumn}")
                                insertionContext.editor.caretModel.currentCaret.moveToOffset(endPoint)
                            }
                            .bold());
                }
                result.stopHere()
            }
        }
    }

}