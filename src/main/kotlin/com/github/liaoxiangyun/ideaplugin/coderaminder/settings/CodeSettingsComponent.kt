package com.github.liaoxiangyun.ideaplugin.coderaminder.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class CodeSettingsComponent {
    val panel: JPanel
    private val myUserNameText = JBTextField()
    private val origin = JBTextField()
    private val session = JBTextField()
    private val days = JBTextField()
    private val reTime = JBTextField()
    private val rate = JBTextField()
    private val enableCheck = JBCheckBox("开启? ")
    val preferredFocusedComponent: JComponent
        get() = myUserNameText
    var userNameText: String
        get() = myUserNameText.text
        set(newText) {
            myUserNameText.text = newText
        }
    var originText: String
        get() = origin.text
        set(newText) {
            origin.text = newText
        }
    var sessionText: String
        get() = session.text
        set(newText) {
            session.text = newText
        }
    var daysText: String
        get() = days.text
        set(newText) {
            days.text = newText
        }
    var reTimeText: String
        get() = reTime.text
        set(newText) {
            reTime.text = newText
        }
    var rateText: String
        get() = rate.text
        set(newText) {
            rate.text = newText
        }
    var enableStatus: Boolean
        get() = enableCheck.isSelected
        set(newStatus) {
            enableCheck.isSelected = newStatus
        }

    init {
        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(JBLabel("Gitlab Profile 地址："), origin, 1, false)
                .addLabeledComponent(JBLabel("Session："), session, 1, false)
                .addLabeledComponent(JBLabel("Git未提交天数："), days, 1, false)
                .addLabeledComponent(JBLabel("提醒时间段："), reTime, 1, false)
                .addLabeledComponent(JBLabel("提醒频率(每分钟)："), rate, 1, false)
                .addComponent(enableCheck)
                .addComponentFillVertically(JPanel(), 0)
                .panel
    }
}