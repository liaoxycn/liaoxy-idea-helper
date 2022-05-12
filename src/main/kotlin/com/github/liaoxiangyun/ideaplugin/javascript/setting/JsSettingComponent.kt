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
    private val modelIcon = JBCheckBox("启用model专属图标")
    private val enableCheck = JBCheckBox("dispatch跳转")
    private val enableLoading = JBCheckBox("loading跳转")

    val preferredFocusedComponent: JComponent
        get() = myText

    var enableVal: Boolean
        get() = enableCheck.isSelected
        set(value) {
            enableCheck.isSelected = value
        }

    var enableLoadingVal: Boolean
        get() = enableLoading.isSelected
        set(value) {
            enableLoading.isSelected = value
        }

    var modelIconVal: Boolean
        get() = modelIcon.isSelected
        set(value) {
            modelIcon.isSelected = value
        }

    var jsService = JsService.getInstance(ProjectUtils.currProject)

    init {
        val jbLabel = JBLabel("")
        val jButton = JButton("扫描models")
        jButton.addActionListener {
            val msg = jsService.loadModelsIndex()

            jbLabel.text = msg
        }
        if (!jsService.isUmi) {
            jButton.isEnabled = false
            modelIcon.isEnabled = false
            enableCheck.isEnabled = false
            enableLoading.isEnabled = false
        }

        enableLoading.isEnabled = false
        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(
                        JBLabel("当前项目(${if (jsService.isUmi) "Umi" else "非Umi"})："),
                        JBLabel("${ProjectUtils.currProject?.name}"), 1, false
                )
                .addSeparator()
                .addLabeledComponent(JBLabel("dva model 管理："), jButton, 1, false)
                .addLabeledComponent(JBLabel(""), jbLabel, 1, false)
                .addComponent(modelIcon)
                .addComponent(enableCheck)
                .addComponent(enableLoading)
                .addSeparator()
                .addComponentFillVertically(JPanel(), 0)
                .panel
    }
}