package com.github.liaoxiangyun.myideaplugin.services

import com.github.liaoxiangyun.myideaplugin.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
