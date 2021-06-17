package com.github.liaoxiangyun.myideaplugin.commit.settings

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
    private val cookie = JBTextField()
    private val responsiblePerson = JBTextField()
    private val inspector = JBTextField()
    private val myIdeaUserStatus = JBCheckBox("Do you use IntelliJ IDEA? ")
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
                .addLabeledComponent(JBLabel("禅道地址："), origin, 1, false)
                .addLabeledComponent(JBLabel("Cookie："), cookie, 1, false)
                .addLabeledComponent(JBLabel("责任人："), responsiblePerson, 1, false)
                .addLabeledComponent(JBLabel("检视人："), inspector, 1, false)
                .addComponentFillVertically(JPanel(), 0)
                .panel
    }
}