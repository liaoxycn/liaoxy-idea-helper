package com.github.liaoxiangyun.ideaplugin.listeners

import com.github.liaoxiangyun.ideaplugin.coderaminder.services.ProjectOpened
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

internal class MyProjectManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        println("==============projectOpened")
        project.service<ProjectOpened>()
    }

    /**
     * Invoked on project close.
     *
     * @param project closing project
     */
    override fun projectClosed(project: Project) {
        println("==============projectClosed")
        ProjectOpened.PROJECTS.remove(project)
    }
}
