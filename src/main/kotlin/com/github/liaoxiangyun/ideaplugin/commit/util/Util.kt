package com.github.liaoxiangyun.ideaplugin.commit.util

import com.github.liaoxiangyun.ideaplugin.commit.ChangeType
import java.util.regex.Pattern
import javax.swing.ButtonGroup
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.text.JTextComponent

class Util {
    companion object {

        private var ID_P: Pattern = Pattern.compile("#(\\d+)")

        open fun isEmpty(v: Any?): Boolean {
            if (v == null) {
                return true
            }
            if (v is String && v.trim() == "") {
                return true
            }
            return false
        }

        /**
         * 设置值
         */
        open fun getValue(component: JComponent): Any? {
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

        /**
         * 设置值
         */
        open fun setValue(component: Any, value: Any) {
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
    }
}