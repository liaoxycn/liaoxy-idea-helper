package com.github.liaoxiangyun.ideaplugin.javascript.setting

import com.github.liaoxiangyun.ideaplugin.javascript.service.JsService
import com.github.liaoxiangyun.ideaplugin.common.util.ProjectUtils
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
    val preferredFocusedComponent: JComponent
        get() = myText

    init {
        val jbLabel = JBLabel("")
        val jButton = JButton("扫描models");
        jButton.addActionListener {
            jButton.isEnabled = false

            val jsService = JsService.getInstance(ProjectUtils.currProject)
            val msg = jsService.loadModelsIndex()

            jButton.isEnabled = true
            jbLabel.text = msg
        }
        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(JBLabel("当前项目："), JBLabel("${ProjectUtils.currProject?.name}"), 1, false)
                .addLabeledComponent(JBLabel("React umi 管理："), jButton, 1, false)
                .addLabeledComponent(JBLabel(""), jbLabel, 1, false)
                .addComponentFillVertically(JPanel(), 0)
                .panel
    }
}