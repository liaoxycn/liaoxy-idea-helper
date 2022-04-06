package com.github.liaoxiangyun.ideaplugin.coderaminder.settings

import com.github.liaoxiangyun.ideaplugin.coderaminder.util.HttpHelper
import com.github.liaoxiangyun.ideaplugin.common.util.ProjectUtils
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane

/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class CodeSettingsComponent {
    val panel: JPanel
    private val myUserNameText = JBTextField()
    private val origin = JBTextField()
    private val token = JBTextField()
    private val calendar = JBTextArea(2, 10)
    private val branches = JBTextField("develop")
    private val reTime = JBTextField("18:30")
    private val enableCheck = JBCheckBox("开启? ")
    private val dailyReport = JBCheckBox(" 每日报告 ")
    private val weeklyReport = JBCheckBox(" 每周报告 ")
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
    var tokenText: String
        get() = token.text
        set(newText) {
            token.text = newText
        }
    var calendarText: String
        get() = calendar.text
        set(newText) {
            calendar.text = newText
        }
    var reTimeText: String
        get() = reTime.text
        set(newText) {
            reTime.text = newText
        }
    var branchesText: String
        get() = branches.text
        set(newText) {
            branches.text = newText
        }
    var enableStatus: Boolean
        get() = enableCheck.isSelected
        set(newStatus) {
            enableCheck.isSelected = newStatus
        }
    var dailyReportStatus: Boolean
        get() = dailyReport.isSelected
        set(newStatus) {
            dailyReport.isSelected = newStatus
        }
    var weeklyReportStatus: Boolean
        get() = weeklyReport.isSelected
        set(newStatus) {
            weeklyReport.isSelected = newStatus
        }

    init {
        calendar.wrapStyleWord = true
        val scrollPane = JScrollPane(calendar);
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED

        val jbLabel = JBLabel("")
        val jButton = JButton("统计")
        jButton.addActionListener {
            val msg = ""
            val summary = HttpHelper().getSummary()
            jbLabel.text = summary.messages
        }
        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(
                        JBLabel("当前项目："),
                        JBLabel("${ProjectUtils.currProject?.name}"), 1, false
                )
                .addComponent(enableCheck)
                .addSeparator()
                .addLabeledComponent(JBLabel("API配置："), JBLabel(), 1, false)
                .addLabeledComponent(JBLabel(" Gitlab地址"), origin, 1, false)
                .addLabeledComponent(JBLabel(" TOKEN"), token, 1, false)
                .addSeparator()
                .addLabeledComponent(JBLabel("配置："), JBLabel(), 1, false)
                .addLabeledComponent(JBLabel(" 统计分支"), scrollPane, 1, false)
                .addLabeledComponent(JBLabel(" 公休日历"), scrollPane, 1, false)
                .addLabeledComponent(JBLabel(" 提醒时间"), reTime, 1, false)
                .addComponent(dailyReport)
                .addComponent(weeklyReport)
                .addSeparator()
                .addLabeledComponent(JBLabel("代码量："), jButton, 1, false)
                .addLabeledComponent(JBLabel(""), jbLabel, 1, false)
                .addComponentFillVertically(JPanel(), 0)
                .panel
    }
}