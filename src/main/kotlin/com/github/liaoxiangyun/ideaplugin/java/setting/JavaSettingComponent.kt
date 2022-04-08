package com.github.liaoxiangyun.ideaplugin.java.setting

import com.github.liaoxiangyun.ideaplugin.common.util.ProjectUtils
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel


/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class JavaSettingComponent {
    val panel: JPanel
    private val myText = JBTextField()
    private val ewEnable = JBCheckBox("启用Wrapper自动补全column")

    val preferredFocusedComponent: JComponent
        get() = myText

    var ewEnableVal: Boolean
        get() = ewEnable.isSelected
        set(value) {
            ewEnable.isSelected = value
        }

    init {

        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(
                        JBLabel("当前项目："),
                        JBLabel("${ProjectUtils.currProject?.name}"), 1, false
                )
                .addSeparator()
                .addLabeledComponent(JBLabel("深高速后端项目管理："), JBLabel(), 1, false)
                .addComponent(ewEnable)
                .addSeparator()
                .addComponentFillVertically(JPanel(), 0)
                .panel
    }
}