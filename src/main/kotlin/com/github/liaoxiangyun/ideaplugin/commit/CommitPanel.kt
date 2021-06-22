package com.github.liaoxiangyun.ideaplugin.commit

import com.github.liaoxiangyun.ideaplugin.commit.settings.AppSettingsState
import com.github.liaoxiangyun.ideaplugin.commit.util.HttpHelper
import com.github.liaoxiangyun.ideaplugin.commit.util.Util
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
    var settings: AppSettingsState = AppSettingsState.getConfig()

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
            val changeType = selectedChangeType!!
            var line = 0
            var map: MutableMap<String, Any> = mutableMapOf()
            getJComponentByType(changeType).forEach {
                if (isJComponent(it)) {
                    map[changeType.commitLines()[line].lineName] = Util.getValue(it!!) ?: ""
                    line++
                }
            }
            return CommitMessage(
                    selectedChangeType,
                    map
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
                button.isSelected = button.actionCommand.equals(commitMessage.changeType!!.label(), ignoreCase = true)
            }
            var line = 0
            getJComponentByType(commitMessage.changeType!!).forEach {
                if (isJComponent(it)) {
                    val get = commitMessage.map[commitMessage.changeType!!.commitLines()[line].lineName]
                    Util.setValue(it!!, get)
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
                typeChange(ChangeType.valueOf(e.actionCommand.toUpperCase()))
            }
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
        setDefItem(changeScope, settings.taskList)
        setDefItem(comboBox1, settings.bugList)
        setDefItem(comboBox2, settings.storyList)
        button1?.addActionListener { e -> loadTaskID(e, "任务ID") }
        button2?.addActionListener { e -> loadTaskID(e, "BugID") }
        button3?.addActionListener { e -> loadTaskID(e, "需求ID") }

        typeChange(commitMessage?.changeType ?: ChangeType.TYPE1)
    }

    private fun typeChange(changeType: ChangeType) {
        hideAll()
        val list = getJComponentByType(changeType)
        var lines = 0
        list.forEach {
            it?.show()
            if (isJComponent(it)) {
                val value = Util.getValue(it!!)
                var commitLine = changeType.commitLines()[lines]
                if (!Util.isEmpty(commitLine.fixedVal)) {
                    Util.setValue(it!!, commitLine.fixedVal!!)
                } else if (Util.isEmpty(value) && !Util.isEmpty(commitLine.defVal)) {
                    Util.setValue(it!!, commitLine.defVal!!)
                }
                lines++
            }
        }
        showBottom(7 - lines)
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

    private fun setDefItem(jComboBox: JComboBox<String>?, array: List<String>) {
        val selectedItem = jComboBox?.selectedItem ?: ""
        var list: ArrayList<String> = arrayListOf()
        list.add(selectedItem as String)
        list.addAll(array)
        val cbm: ComboBoxModel<String> = DefaultComboBoxModel(list.map2Array { s -> s })
        jComboBox?.model = cbm
        jComboBox?.selectedItem = selectedItem
    }

    private fun loadTaskID(e: ActionEvent, name: String) {
        when (name) {
            "任务ID" -> {
                val dataList = HttpHelper().getTaskList()
                var list = dataList.map { tr -> "任务#${tr[0]} ${tr[3]}" }
                settings.taskList = list as ArrayList<String>
                setDefItem(changeScope, list)
            }
            "BugID" -> {
                val dataList = HttpHelper().getBugList()
                val list = dataList.map { tr -> "Bug#${tr[0]} 【${tr[3]}】 ${tr[4]}" }
                settings.bugList = list as ArrayList<String>
                setDefItem(comboBox1, list)
            }
            "需求ID" -> {
                val dataList = HttpHelper().getStoryList()
                val list = dataList.map { tr -> "需求#${tr[0]} ${tr[3]}" }
                settings.storyList = list as ArrayList<String>
                setDefItem(comboBox2, list)
            }
        }
    }

    private fun getJComponentByType(changeType: ChangeType): ArrayList<JComponent?> {
        when (changeType) {
            ChangeType.TYPE1 -> {
                return arrayListOf(
                        label2, textField1,
                        label3, changeScope, button1,
                        label4, comboBox2, button3,
                        label5, textField7,
                        label6, textField3,
                        label7, textField4,
                        label8, textField5,
                )
            }
            ChangeType.TYPE2 -> {
                return arrayListOf(
                        label9, textField6,
                        label10, comboBox1, button2,
                        label11, textField9,
                        label5, textField7,
                        label6, textField3,
                        label7, textField4,
                        label8, textField5,
                )
            }
            ChangeType.TYPE3 -> {
                return arrayListOf(
                        label3, changeScope, button1,
                        label12, textField10,
                        label13, textField11,
                        label14, textField12,
                        label6, textField3,
                        label7, textField4,
                        label8, textField5,
                )
            }
            ChangeType.TYPE4 -> {
                return arrayListOf(
                        label3, changeScope, button1,
                        label12, textField10,
                        label13, textField11,
                        label14, textField12,
                        label6, textField3,
                        label7, textField4,
                        label8, textField5,
                )
            }
            ChangeType.TYPE5 -> {
                return arrayListOf(
                        label3, changeScope, button1,
                        label15, textField13,
                        label16, textField14,
                        label14, textField12,
                        label6, textField3,
                        label7, textField4,
                        label8, textField5,
                )
            }
            ChangeType.TYPE6 -> {
                return arrayListOf(
                        label3, changeScope, button1,
                        label17, textField15,
                )
            }
            ChangeType.TYPE7 -> {
                return arrayListOf(
                        label3, changeScope, button1,
                        label17, textField15,
                )
            }
            ChangeType.TYPE8 -> {
                return arrayListOf(
                        label5, textField7,
                        label6, textField3,
                )
            }
        }
        return arrayListOf()
    }

    private fun hideAll() {
        ChangeType.values().forEach { it -> getJComponentByType(it).forEach { it?.hide() } }
    }
}