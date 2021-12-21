package com.github.liaoxiangyun.ideaplugin.javascript.setting

import com.github.liaoxiangyun.ideaplugin.common.util.ProjectUtils
import com.github.liaoxiangyun.ideaplugin.javascript.service.JsService
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel


/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class JsSettingComponent {
    val panel: JPanel
    private val myText = JBTextField()

    private val enableCheck = JBCheckBox("开启? ")

    private val templateUrl = JBTextField()

    val preferredFocusedComponent: JComponent
        get() = myText

    var enableVal: Boolean
        get() = enableCheck.isSelected
        set(value) {
            enableCheck.isSelected = value
        }

    var templateUrlVal: String
        get() = templateUrl.text
        set(value) {
            templateUrl.text = value
        }


    init {
        val jbLabel = JBLabel("")
        val jButton = JButton("扫描models")
        jButton.addActionListener {
            val jsService = JsService.getInstance(ProjectUtils.currProject)
            val msg = jsService.loadModelsIndex()

            jbLabel.text = msg
        }

        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("当前项目："), JBLabel("${ProjectUtils.currProject?.name}"), 1, false)
            .addSeparator()
            .addComponent(enableCheck)
            .addLabeledComponent(JBLabel("React umi 管理："), jButton, 1, false)
            .addLabeledComponent(JBLabel(""), jbLabel, 1, false)
            .addSeparator()
            .panel
    }
}