package com.github.liaoxiangyun.ideaplugin.coderaminder.settings

import cn.hutool.json.JSONUtil
import com.github.liaoxiangyun.ideaplugin.coderaminder.util.CalendarUtil
import com.github.liaoxiangyun.ideaplugin.coderaminder.util.HttpHelper
import com.github.liaoxiangyun.ideaplugin.common.util.ProjectUtils
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.util.stream.Collectors
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
    private val dailyReport = JBCheckBox(" 每周报告 ")
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

    init {
        calendar.text = calendar.text.ifBlank { CalendarUtil.getDefaultContent() }
        branches.toolTipText = "多分支用英文符号'|'分隔，如 develop|master"
        enableCheck.isVisible = false
        calendar.wrapStyleWord = true
        val scrollPane = JScrollPane(calendar);
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED


        val jbLabel = JBLabel("")
        val jButton2 = JButton("两周内修改分支")
        jButton2.addActionListener {
            var strs = HttpHelper().getStrs("branch", null);
            println("strs=$strs")
            strs.add("develop")
            val branchNames = strs.stream().filter { it.isNotBlank() }.distinct().collect(Collectors.joining("|"))
            branches.text = branchNames
        }
        val jButton3 = JButton("默认分支")
        jButton3.addActionListener {
            branches.text = "develop|release|preprod|master"
        }
        val jPanel = JPanel()
        jPanel.add(jButton2)
        jPanel.add(jButton3)

        val jButton = JButton("统计")
        jButton.addActionListener {
            val summary = HttpHelper().getSummary2()
            println("summary =======================================\n ${JSONUtil.toJsonStr(summary)}")
//            jbLabel.text = summary.messages
            val title = "日均代码量统计结果（数据来自Gitlab）"
            Messages.showMessageDialog(ProjectUtils.currProject, summary.messages, title, Messages.getInformationIcon())
        }
        val jbLabel1 = JBLabel(" TOKEN")
        jbLabel1.toolTipText = "Gitlab个人头像>Setting>Access Tokens"
        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(
                        JBLabel("当前项目："),
                        JBLabel("${ProjectUtils.currProject?.name}"), 1, false
                )
                .addComponent(enableCheck)
                .addSeparator()
                .addLabeledComponent(JBLabel("API配置："), JBLabel(), 1, false)
                .addLabeledComponent(JBLabel(" Gitlab地址"), origin, 1, false)
                .addLabeledComponent(jbLabel1, token, 1, false)
                .addSeparator()
                .addLabeledComponent(JBLabel("配置："), jButton3, 1, false)
                .addLabeledComponent(JBLabel(" 统计分支"), branches, 1, false)
                .addLabeledComponent(JBLabel(" 公休日历"), scrollPane, 1, false)
                .addLabeledComponent(JBLabel(" 提醒时间"), reTime, 1, false)
                .addComponent(dailyReport)
                .addSeparator()
                .addLabeledComponent(JBLabel("代码量："), jButton, 1, false)
                .addLabeledComponent(JBLabel(""), jbLabel, 1, false)
                .addComponentFillVertically(JPanel(), 0)
                .panel
    }
}