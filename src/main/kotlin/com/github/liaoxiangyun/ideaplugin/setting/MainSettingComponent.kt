package com.github.liaoxiangyun.ideaplugin.setting

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class MainSettingComponent {
    val panel: JPanel
    private val myText = JBTextField()
    val preferredFocusedComponent: JComponent
        get() = myText

    init {
        val jbLabel = JBLabel("1.1.1")
        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(JBLabel("版本："), jbLabel, 1, false)
                .addComponentFillVertically(JPanel(), 0)
                .panel
    }
}