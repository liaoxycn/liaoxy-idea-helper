package com.github.liaoxiangyun.ideaplugin.common.util

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.Messages
import java.awt.Component
import java.net.UnknownHostException

class Notify {
    companion object {
        private var TITLE: String = "Intellij IDEA Plugin";
        private val none: NotificationGroup = NotificationGroup("Intellij IDEA NONE Notification",
                NotificationDisplayType.NONE, true)
        private val ball: NotificationGroup = NotificationGroup("Intellij IDEA Balloon Notification",
                NotificationDisplayType.BALLOON, true)
        private val sticky: NotificationGroup = NotificationGroup("Intellij IDEA Notification",
                NotificationDisplayType.STICKY_BALLOON, true)
        private val window: NotificationGroup = NotificationGroup("Intellij IDEA Window Notification",
                NotificationDisplayType.TOOL_WINDOW, true, "Intellij IDEA Window Notification")

        private fun showNotification(message: String, project: Project? = ProjectManager.getInstance().defaultProject,
                                     title: String = TITLE,
                                     notificationType: NotificationType = NotificationType.INFORMATION,
                                     notificationListener: NotificationListener? = null, level: Int = 2) {
            var group = sticky
            when (level) {
                0 -> { group = none }
                1 -> { group = ball }
                2 -> { group = sticky }
                3 -> { group = window }
            }
            group.createNotification(title, message, notificationType, notificationListener).notify(project)
        }

        fun showInfoDialog(component: Component, title: String, message: String) {
            Messages.showInfoMessage(component, message, title)
        }

        fun showErrorDialog(component: Component, title: String, errorMessage: String) {
            Messages.showErrorDialog(component, errorMessage, title)
        }

        fun showErrorDialog(component: Component, title: String, e: Exception) {
            if (isOperationCanceled(e)) {
                return
            }
            Messages.showErrorDialog(component, getErrorTextFromException(e), title)
        }

        open fun showSuccessNotification(message: String, project: Project? = ProjectManager.getInstance().defaultProject,
                                         title: String = TITLE, level: Int = 2) {
            showNotification(message, project, title, NotificationType.INFORMATION, null, level)
        }

        fun showWarnNotification(message: String, project: Project? = ProjectManager.getInstance().defaultProject,
                                 title: String = TITLE, level: Int = 2) {
            showNotification(message, project, title, NotificationType.WARNING, null, level)
        }

        fun showErrorNotification(message: String, project: Project? = ProjectManager.getInstance().defaultProject,
                                  title: String = TITLE, level: Int = 2) {
            showNotification(message, project, title, NotificationType.ERROR, null, level)
        }

        fun showSuccessNotification(message: String, project: Project?,
                                    notificationListener: NotificationListener, title: String = TITLE, level: Int = 2) {
            showNotification(message, project, title, NotificationType.INFORMATION, notificationListener, level)
        }

        private fun isOperationCanceled(e: Exception): Boolean {
            return e is ProcessCanceledException
        }

        private fun getErrorTextFromException(e: Exception): String {
            if (e is UnknownHostException) {
                return "Unknown host: " + e.message
            }
            return e.message ?: ""
        }
    }
}