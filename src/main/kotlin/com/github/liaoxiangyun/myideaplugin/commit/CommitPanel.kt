package com.github.liaoxiangyun.myideaplugin.commit

import com.github.liaoxiangyun.myideaplugin.commit.settings.AppSettingsState
import com.github.liaoxiangyun.myideaplugin.commit.util.HttpHelper
import com.intellij.openapi.project.Project
import com.intellij.util.containers.map2Array
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import java.io.File
import java.util.function.Consumer
import java.util.regex.Pattern
import javax.swing.*
import javax.swing.text.JTextComponent


/**
 * @author Damien Arrachequesne
 */
class CommitPanel constructor(project: Project?, commitMessage: CommitMessage?) {
    var settings: AppSettingsState = AppSettingsState.instance

    var mainPanel: JPanel? = null
    var label21: JLabel? = null
    var shortDescription: JTextField? = null
    var label22: JLabel? = null
    var longDescription: JTextArea? = null
    var label24: JLabel? = null
    var breakingChanges: JTextArea? = null
    var label25: JLabel? = null
    var closedIssues: JTextField? = null
    var wrapTextCheckBox: JCheckBox? = null
    var skipCICheckBox: JCheckBox? = null


    //row1  【修改类型】：开发新功能（必填）
    var label1: JLabel? = null
    var type1Rb: JRadioButton? = null
    var type2Rb: JRadioButton? = null
    var type3Rb: JRadioButton? = null
    var type4Rb: JRadioButton? = null
    var type5Rb: JRadioButton? = null
    var type6Rb: JRadioButton? = null
    var type7Rb: JRadioButton? = null
    var type8Rb: JRadioButton? = null

    //row2
    // 【任务描述】：人机定位管理优化（必填）
    var label2: JLabel? = null
    var textField1: JTextField? = null

    // 【Bug描述】：【在线填报】demo环境中，批量下载报表报错（必填）
    var label9: JLabel? = null
    var textField6: JTextField? = null

    //row3
    // 【任务ID】：55225（禅道的开发任务ID）（必填）
    var label3: JLabel? = null
    var changeScope: JComboBox<String>? = null
    var button1: JButton? = null

    // 【BugID】：81090（禅道上的缺陷ID）（必填）
    var label10: JLabel? = null
    var comboBox1: JComboBox<String>? = null
    var button2: JButton? = null

    // 【重构对象】：档案管理模块代码（必填）
    var label12: JLabel? = null
    var textField10: JTextField? = null

    // 【优化对象】：实体类型库（必填）
    var label15: JLabel? = null
    var textField13: JTextField? = null

    //row4
    // 【需求ID】：xxx（禅道的需求ID）（必填）
    var label4: JLabel? = null
    var comboBox2: JComboBox<String>? = null
    var button3: JButton? = null

    // 【问题原因】：表格中有一个字段缺少缺省值，导出程序抛出空指针异常（必填）
    var label11: JLabel? = null
    var textField9: JTextField? = null

    // 【重构原因】：代码走查发现该模块多个文件超长，且不容易解耦（必填）
    var label13: JLabel? = null
    var textField11: JTextField? = null

    // 【优化原因】：页面打开、条件查询等操作需要时间很长完成（必填）
    var label16: JLabel? = null
    var textField14: JTextField? = null

    //row5
    // 【修改描述】：增加定位设备管理、二维码、标签码（必填）
    var label5: JLabel? = null
    var textField7: JTextField? = null

    // 【优化描述】：修改每个文件不超过200行，并做解耦处理（必填）
    var label14: JLabel? = null
    var textField12: JTextField? = null

    //row6  【影响范围】：影响原有的设备管理功能（必填）
    var label6: JLabel? = null
    var textField3: JTextField? = null

    //row7  【责任人】：xxx （非必填）
    var label7: JLabel? = null
    var textField4: JTextField? = null

    //row8  【责任人】：xxx （非必填）
    var label8: JLabel? = null
    var textField5: JTextField? = null

    // 备注
    var label17: JLabel? = null
    var textField15: JTextField? = null


    var t1: JTextField? = null
    var t2: JTextField? = null
    var t3: JTextField? = null
    var t4: JTextField? = null
    var t5: JTextField? = null
    var lList = arrayOf(t1, t2, t3, t4, t5)

    var changeTypeGroup: ButtonGroup? = null

    val commitMessage: CommitMessage
        get() {
            var arr = arrayListOf<Any>()
            getJComponentByType(selectedChangeType!!).forEach {
                if (isJComponent(it)) {
                    arr.add(getValue(it!!) ?: "")
                }
            }
            return CommitMessage(
                    selectedChangeType,
                    arr
            )
        }

    private val selectedChangeType: ChangeType?
        private get() {
            val buttons = changeTypeGroup!!.elements
            while (buttons.hasMoreElements()) {
                val button = buttons.nextElement()
                if (button.isSelected) {
                    return ChangeType.valueOf(button.actionCommand.toUpperCase())
                }
            }
            return null
        }


    /**
     * 从CommitMessage中恢复值
     */
    private fun restoreValuesFromParsedCommitMessage(commitMessage: CommitMessage) {
        if (commitMessage.changeType != null) {
            val buttons = changeTypeGroup!!.elements
            while (buttons.hasMoreElements()) {
                val button = buttons.nextElement()
                if (button.actionCommand.equals(commitMessage.changeType!!.label(), ignoreCase = true)) {
                    button.isSelected = true
                }
            }
            var line = 0
            getJComponentByType(commitMessage.changeType!!).forEach {
                if (isJComponent(it)) {
                    var get = commitMessage.lines?.get(line)
                    setValue(it!!, get!!)
                    line++
                }
            }
        }
    }

    private fun isJComponent(c: Any?): Boolean {
        if (c == null) return false
        if (c is JTextComponent || c is JCheckBox || c is JComboBox<*>) {
            return true
        }
        return false
    }

    /**
     * 设置值
     */
    private fun setValue(component: Any, value: Any) {
        when (component) {
            is JTextComponent -> {
                component.text = value.toString()
            }
            is JCheckBox -> {
                component.isSelected = (value as Boolean)
            }
            is JComboBox<*> -> {
                component.selectedItem = value
            }
            is ButtonGroup -> {
                val buttons = component.elements
                while (buttons.hasMoreElements()) {
                    val button = buttons.nextElement()
                    if (button.actionCommand.equals((value as ChangeType).label(), ignoreCase = true)) {
                        button.isSelected = true
                    }
                }
            }
        }
    }

    private var ID_P: Pattern = Pattern.compile("#(\\d+)")

    /**
     * 设置值
     */
    private fun getValue(component: JComponent): Any? {
        when (component) {
            is JTextComponent -> {
                return component.text
            }
            is JCheckBox -> {
                return component.isSelected
            }
            is JComboBox<*> -> {
                if (component.selectedItem != null) {
                    val matcher = ID_P.matcher((component.selectedItem as String))
                    if (matcher.find()) {
                        return matcher.group(1)
                    }
                }
                return component.selectedItem
            }
            is ButtonGroup -> {
                val buttons = component.elements
                while (buttons.hasMoreElements()) {
                    val button = buttons.nextElement()
                    if (button.isSelected) {
                        return ChangeType.valueOf(button.actionCommand.toUpperCase())
                    }
                }
            }
        }
        return null
    }

    init {
        lList = arrayOf(t1, t2, t3, t4, t5)
        val workingDirectory = File(project?.basePath)
        val result = GitLogQuery(workingDirectory).execute()
        if (result.isSuccess) {
            changeScope!!.addItem("") // no value by default
            result.scopes.forEach(Consumer { item: String -> changeScope!!.addItem(item) })
        }
        commitMessage?.let { restoreValuesFromParsedCommitMessage(it) }

        val buttons = changeTypeGroup!!.elements
        var selected: AbstractButton? = null
        while (buttons.hasMoreElements()) {
            val button = buttons.nextElement()
            if (button.isSelected) {
                selected = button
            }
            button.addActionListener { e ->
                hideAll()
                val list = getJComponentByType(ChangeType.valueOf(e.actionCommand.toUpperCase()))
                var count = 0
                list.forEach {
                    it?.show()
                    if (isJComponent(it)) {
                        count++
                    }
                }
                showBottom(7 - count)
            }
        }
        if (textField4?.text?.trim()?.length ?: 0 == 0) {
            textField4?.text = settings.responsiblePerson
        }
        if (textField5?.text?.trim()?.length ?: 0 == 0) {
            textField5?.text = settings.inspector
        }
        val pattern: Pattern = Pattern.compile("#\\d+(.*)")
        //
        changeScope?.addItemListener { e ->
            run {
                if (e.stateChange == ItemEvent.SELECTED) {
                    val s = changeScope?.selectedItem as String
                    val matcher = pattern.matcher(s)
                    if (matcher.find()) {
                        val group = matcher.group(1)
                        textField1?.text = group
                    }
                }
            }
        }
        //
        comboBox1?.addItemListener { e ->
            run {
                if (e.stateChange == ItemEvent.SELECTED) {
                    val s = comboBox1?.selectedItem as String
                    val matcher = pattern.matcher(s)
                    if (matcher.find()) {
                        val group = matcher.group(1)
                        textField6?.text = group
                    }
                }
            }
        }
        //
        comboBox2?.addItemListener { e ->
            run {
                if (e.stateChange == ItemEvent.SELECTED) {
                    val s = comboBox2?.selectedItem as String
                    val matcher = pattern.matcher(s)
                    if (matcher.find()) {
                        val group = matcher.group(1)
                        textField1?.text = group
                    }
                }
            }
        }
        changeScope?.model = DefaultComboBoxModel(settings.taskList.map2Array { s -> s })
        comboBox1?.model = DefaultComboBoxModel(settings.bugList.map2Array { s -> s })
        comboBox2?.model = DefaultComboBoxModel(settings.storyList.map2Array { s -> s })
        button1?.addActionListener { e -> loadTaskID(e, "任务ID") }
        button2?.addActionListener { e -> loadTaskID(e, "BugID") }
        button3?.addActionListener { e -> loadTaskID(e, "需求ID") }

        val changeType = ChangeType.valueOf(selected!!.actionCommand.toUpperCase())
        hideAll()
        getJComponentByType(changeType).forEach { it?.show() }
        showBottom(0)
        //mainPanel?.add(JLabel("hello"))
    }

    private fun showBottom(i: Int) {
        for ((index, value) in lList.withIndex()) {
            if (index < i) {
                value?.show()
            } else {
                value?.hide()
            }
        }
    }

    private fun loadTaskID(e: ActionEvent, name: String) {
        when (name) {
            "任务ID" -> {
                val taskList = HttpHelper().getTaskList()
                val list = taskList.map { tr -> "任务#${tr[0]} ${tr[3]}" }
                if (taskList.size > 0) {
                    settings.taskList = list as ArrayList<String>
                }
                val cbm: ComboBoxModel<String> = DefaultComboBoxModel(list.map2Array { s -> s })
                changeScope?.model = cbm
            }
            "BugID" -> {
                val bugList = HttpHelper().getBugList()
                val list = bugList.map { tr -> "Bug#${tr[0]} 【${tr[3]}】 ${tr[4]}" }
                if (bugList.size > 0) {
                    settings.bugList = list as ArrayList<String>
                }
                val cbm: ComboBoxModel<String> = DefaultComboBoxModel(list.map2Array { s -> s })
                comboBox1?.model = cbm
            }
            "需求ID" -> {
                val storyList = HttpHelper().getStoryList()
                val list = storyList.map { tr -> "需求#${tr[0]} ${tr[3]}" }
                if (storyList.size > 0) {
                    settings.storyList = list as ArrayList<String>
                }
                val cbm: ComboBoxModel<String> = DefaultComboBoxModel(list.map2Array { s -> s })
                comboBox2?.model = cbm
            }
        }
    }

    private fun getChangeTypeJComponents(): ArrayList<JComponent?> {
        val arr = arrayListOf<JComponent?>()
        val buttons = changeTypeGroup!!.elements
        while (buttons.hasMoreElements()) {
            val button = buttons.nextElement()
            arr.add(button)
        }
        arr.add(label1)
        return arr
    }

    private fun getJComponentByType(changeType: ChangeType): ArrayList<JComponent?> {
        when (changeType) {
            ChangeType.TYPE1 -> {
                val arr = getChangeTypeJComponents()
                arr.addAll(arrayListOf(
                        label2, textField1,
                        label3, changeScope, button1,
                        label4, comboBox2, button3,
                        label5, textField7,
                        label6, textField3,
                        label7, textField4,
                        label8, textField5,
                ))
                return arr
            }
            ChangeType.TYPE2 -> {
                val arr = getChangeTypeJComponents()
                arr.addAll(arrayListOf(
                        label9, textField6,
                        label10, comboBox1, button2,
                        label11, textField9,
                        label5, textField7,
                        label6, textField3,
                        label7, textField4,
                        label8, textField5,
                ))
                return arr
            }
            ChangeType.TYPE3 -> {
                val arr = getChangeTypeJComponents()
                arr.addAll(arrayListOf(
                        label3, changeScope, button1,
                        label12, textField10,
                        label13, textField11,
                        label14, textField12,
                        label6, textField3,
                        label7, textField4,
                        label8, textField5,
                ))
                return arr
            }
            ChangeType.TYPE4 -> {
                val arr = getChangeTypeJComponents()
                arr.addAll(arrayListOf(
                        label3, changeScope, button1,
                        label12, textField10,
                        label13, textField11,
                        label14, textField12,
                        label6, textField3,
                        label7, textField4,
                        label8, textField5,
                ))
                return arr
            }
            ChangeType.TYPE5 -> {
                val arr = getChangeTypeJComponents()
                arr.addAll(arrayListOf(
                        label3, changeScope, button1,
                        label15, textField13,
                        label16, textField14,
                        label14, textField12,
                        label6, textField3,
                        label7, textField4,
                        label8, textField5,
                ))
                return arr
            }
            ChangeType.TYPE6 -> {
                val arr = getChangeTypeJComponents()
                arr.addAll(arrayListOf(
                        label2, textField1,
                        label17, textField15,
                ))
                return arr
            }
            ChangeType.TYPE7 -> {
                val arr = getChangeTypeJComponents()
                arr.addAll(arrayListOf(
                        label2, textField1,
                        label17, textField15,
                ))
                return arr
            }
            ChangeType.TYPE8 -> {
                val arr = getChangeTypeJComponents()
                arr.addAll(arrayListOf(
                        label5, textField7,
                        label6, textField3,
                ))
                return arr
            }
        }
        return arrayListOf()
    }

    private fun hideAll() {
        getJComponentByType(ChangeType.TYPE1).forEach { it?.hide() }
        getJComponentByType(ChangeType.TYPE2).forEach { it?.hide() }
        getJComponentByType(ChangeType.TYPE3).forEach { it?.hide() }
        getJComponentByType(ChangeType.TYPE4).forEach { it?.hide() }
        getJComponentByType(ChangeType.TYPE5).forEach { it?.hide() }
        getJComponentByType(ChangeType.TYPE6).forEach { it?.hide() }
        getJComponentByType(ChangeType.TYPE7).forEach { it?.hide() }
        getJComponentByType(ChangeType.TYPE8).forEach { it?.hide() }
    }
}