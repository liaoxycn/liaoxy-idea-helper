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
        private var TITLE: String = "SmartFox Intellij IDEA Plugin";
        private val sticky: NotificationGroup = NotificationGroup("SmartFox Intellij IDEA Notification",
                NotificationDisplayType.STICKY_BALLOON, true)
        private val ball: NotificationGroup = NotificationGroup("SmartFox Intellij IDEA Balloon Notification",
                NotificationDisplayType.BALLOON, true)

        private fun showNotification(message: String, project: Project? = ProjectManager.getInstance().defaultProject,
                                     title: String = TITLE,
                                     notificationType: NotificationType = NotificationType.INFORMATION,
                                     notificationListener: NotificationListener? = null, sticky: Boolean = false) {
            val group = if (sticky) this.sticky else ball
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

        fun showSuccessNotification(message: String, project: Project? = ProjectManager.getInstance().defaultProject,
                                    title: String = TITLE, sticky: Boolean = false) {
            showNotification(message, project, title, NotificationType.INFORMATION, null, sticky)
        }

        fun showWarnNotification(message: String, project: Project? = ProjectManager.getInstance().defaultProject,
                                 title: String = TITLE, sticky: Boolean = false) {
            showNotification(message, project, title, NotificationType.WARNING, null, sticky)
        }

        fun showErrorNotification(message: String, project: Project? = ProjectManager.getInstance().defaultProject,
                                  title: String = TITLE, sticky: Boolean = false) {
            showNotification(message, project, title, NotificationType.ERROR, null, sticky)
        }

        fun showSuccessNotification(message: String, project: Project?,
                                    notificationListener: NotificationListener, title: String = TITLE, sticky: Boolean = false) {
            showNotification(message, project, title, NotificationType.INFORMATION, notificationListener, sticky)
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