package com.github.liaoxiangyun.ideaplugin.services

import com.github.liaoxiangyun.ideaplugin.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
