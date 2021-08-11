package com.github.liaoxiangyun.ideaplugin.js.constant

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

interface Icons {
    companion object {
        val down: Icon
            get() = IconLoader.getIcon("/images/down.png")
        val up: Icon
            get() = IconLoader.getIcon("/images/up.png")
    }
}