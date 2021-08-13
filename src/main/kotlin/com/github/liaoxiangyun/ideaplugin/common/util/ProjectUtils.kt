package com.github.liaoxiangyun.ideaplugin.common.util

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager

/**
 * IDEA项目相关工具
 *
 * @author tangcent
 * @version 1.0.0
 * @since 2020/02/14 18:35
 */
object ProjectUtils {// 只存在一个打开的项目则使用打开的项目

    //如果有项目窗口处于激活状态

    //否则使用默认项目
//正常情况下不会发生
    /**
     * 获取当前项目对象
     *
     * @return 当前项目对象
     */
    val currProject: Project
        get() {
            val projectManager = ProjectManager.getInstance()
            val openProjects = projectManager.openProjects
            if (openProjects.size == 0) {
                return projectManager.defaultProject //正常情况下不会发生
            } else if (openProjects.size == 1) {
                // 只存在一个打开的项目则使用打开的项目
                return openProjects[0]
            }

            //如果有项目窗口处于激活状态
            try {
                val wm = WindowManager.getInstance()
                for (project in openProjects) {
                    val window = wm.suggestParentWindow(project)
                    if (window != null && window.isActive) {
                        return project
                    }
                }
            } catch (ignored: Exception) {
            }

            //否则使用默认项目
            return projectManager.defaultProject
        }

    /**
     * 进行旧版本兼容，该方法已经存在 @see [com.intellij.openapi.project.ProjectUtil.guessProjectDir]
     *
     * @param project 项目对象
     * @return 基本目录
     */
    fun getBaseDir(project: Project): VirtualFile? {
        if (project.isDefault) {
            return null
        }
        val modules = ModuleManager.getInstance(project).modules
        var module: Module? = null
        if (modules.size == 1) {
            module = modules[0]
        } else {
            for (item in modules) {
                if (item.name == project.name) {
                    module = item
                    break
                }
            }
        }
        if (module != null) {
            val moduleRootManager = ModuleRootManager.getInstance(module)
            for (contentRoot in moduleRootManager.contentRoots) {
                if (contentRoot.isDirectory && contentRoot.name == module.name) {
                    return contentRoot
                }
            }
        }
        val basePath = project.basePath ?: throw NullPointerException()
        return LocalFileSystem.getInstance().findFileByPath(basePath)
    }
}