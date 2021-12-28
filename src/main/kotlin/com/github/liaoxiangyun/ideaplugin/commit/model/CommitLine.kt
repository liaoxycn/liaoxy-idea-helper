package com.github.liaoxiangyun.ideaplugin.commit.model

import com.github.liaoxiangyun.ideaplugin.commit.settings.AppSettingsState
import java.util.*
import java.util.regex.Pattern

class CommitLine {
    var lineName: String = ""
    var require: Boolean = false
    var regex: Pattern = REGEX
    var defVal: Any? = null
    var fixedVal: Any? = null

    var component: Any? = null

    fun getDefValue(): Any? {
        return if (Objects.equals(this.defVal, "responsiblePerson")) {
            AppSettingsState.getConfig().responsiblePerson
        } else if (Objects.equals(this.defVal, "inspector")) {
            AppSettingsState.getConfig().inspector
        } else {
            defVal
        }
    }

    constructor(
        lineName: String, require: Boolean, regex: Pattern?,
        defVal: Any? = null,
        fixedVal: Any? = null,
    ) {
        this.lineName = lineName
        this.require = require
        this.regex = regex ?: REGEX
        this.defVal = defVal
        this.fixedVal = fixedVal
    }

    override fun toString(): String {
        return "$lineName [" +
                (if (require) "require=$require, " else "") +
                (if (regex != REGEX) "regex=${regex.toRegex().pattern}, " else "") +
                (if (defVal != null) "defVal=$defVal, " else "") +
                (if (fixedVal != null) "fixedVal=$fixedVal, " else "") +
                "]"
    }

    companion object {
        val REGEX: Pattern = Pattern.compile(".*")
    }
}