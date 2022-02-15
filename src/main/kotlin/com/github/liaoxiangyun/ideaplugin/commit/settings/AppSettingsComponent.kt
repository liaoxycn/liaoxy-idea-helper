package com.github.liaoxiangyun.ideaplugin.commit.settings

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
class AppSettingsComponent {
    val panel: JPanel
    private val myUserNameText = JBTextField()
    private val origin = JBTextField()
    private val user = JBTextField()
    private val password = JBTextField()
    private val cookie = JBTextField()
    private val responsiblePerson = JBTextField()
    private val inspector = JBTextField()
    private val myIdeaUserStatus = JBCheckBox("Do you use IntelliJ IDEA? ")


    private val q1 = JBCheckBox("自动填入(问题原因,修改描述,影响范围)")
    private val q2 = JBCheckBox("")

    var q1Val: Boolean
        get() = q1.isSelected
        set(value) {
            q1.isSelected = value
        }

    var q2Val: Boolean
        get() = q2.isSelected
        set(value) {
            q2.isSelected = value
        }

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
    var userText: String
        get() = user.text
        set(newText) {
            user.text = newText
        }
    var passwordText: String
        get() = password.text
        set(newText) {
            password.text = newText
        }
    var cookieText: String
        get() = cookie.text
        set(newText) {
            cookie.text = newText
        }
    var responsiblePersonText: String
        get() = responsiblePerson.text
        set(newText) {
            responsiblePerson.text = newText
        }
    var inspectorText: String
        get() = inspector.text
        set(newText) {
            inspector.text = newText
        }
    var ideaUserStatus: Boolean
        get() = myIdeaUserStatus.isSelected
        set(newStatus) {
            myIdeaUserStatus.isSelected = newStatus
        }

    init {
        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(
                        JBLabel("当前项目："),
                        JBLabel("${ProjectUtils.currProject?.name}"), 1, false
                )
                .addSeparator()
                .addLabeledComponent(JBLabel("禅道管理："), JBLabel(), 1, false)
                .addLabeledComponent(JBLabel("禅道地址："), origin, 1, false)
                .addLabeledComponent(JBLabel("账号："), user, 1, false)
                .addLabeledComponent(JBLabel("密码："), password, 1, false)
                .addLabeledComponent(JBLabel("Cookie："), cookie, 1, false)
                .addLabeledComponent(JBLabel("责任人："), responsiblePerson, 1, false)
                .addLabeledComponent(JBLabel("检视人："), inspector, 1, false)
                .addSeparator()
                .addLabeledComponent(JBLabel("更多："), JBLabel(), 1, false)
                .addComponent(q1)
//                .addComponent(q2)
                .addComponentFillVertically(JPanel(), 0)
                .panel
    }
}