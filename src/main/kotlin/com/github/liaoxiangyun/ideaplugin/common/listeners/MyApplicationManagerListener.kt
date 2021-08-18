package com.github.liaoxiangyun.ideaplugin.common.listeners

import com.github.liaoxiangyun.ideaplugin.common.services.MyApplicationService
import com.intellij.ide.AppLifecycleListener
import com.intellij.ide.ApplicationInitializedListener
import com.intellij.ide.ApplicationLoadListener
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project

class MyApplicationManagerListener : ApplicationInitializedListener, ApplicationLoadListener, AppLifecycleListener {


    /**
     * Invoked when all application level components are initialized in the same thread where components are initializing (EDT is not guaranteed).
     * Write actions and time-consuming activities are not recommended because listeners are invoked sequentially and directly affects application start time.
     */
    override fun componentsInitialized() {
        val application = ApplicationManager.getApplication()
        println("#ApplicationInitializedListener application = $application")
        application.invokeLater {
            println("#invokeLater")
            MyApplicationService.instance
        }
    }

    override fun beforeApplicationLoaded(application: Application, configPath: String) {
        println("#ApplicationLoadListener application = $application")
        super.beforeApplicationLoaded(application, configPath)
    }

    /**
     * Called before an application frame is shown.
     */
    override fun appFrameCreated(commandLineArgs: MutableList<String>) {
        println("#AppLifecycleListener appFrameCreated")
        super.appFrameCreated(commandLineArgs)
    }

    /**
     * Called when the welcome screen is displayed (not called if the application opens a project).
     */
    override fun welcomeScreenDisplayed() {
        println("#AppLifecycleListener welcomeScreenDisplayed")
        super.welcomeScreenDisplayed()
    }

    /**
     * Called after an application frame is shown.
     */
    override fun appStarting(projectFromCommandLine: Project?) {
        println("#AppLifecycleListener appStarting")
        super.appStarting(projectFromCommandLine)
    }

    /**
     * Called after all application startup tasks, including opening projects, are processed (i.e. either completed or running in background).
     */
    override fun appStarted() {
        println("#AppLifecycleListener appStarted")
        super.appStarted()
        MyApplicationService.instance
    }

    /**
     * Called when a project frame is closed.
     */
    override fun projectFrameClosed() {
        println("#AppLifecycleListener projectFrameClosed")
        super.projectFrameClosed()
    }

    /**
     * Called if the project opening was cancelled or failed because of an error.
     */
    override fun projectOpenFailed() {
        println("#AppLifecycleListener projectOpenFailed")
        super.projectOpenFailed()
    }

    /**
     * Fired before saving settings and before final 'can exit?' check. App may end up not closing if some of the
     * [com.intellij.openapi.application.ApplicationListener] listeners return false from their `canExitApplication`
     * method.
     */
    override fun appClosing() {
        println("#AppLifecycleListener appClosing")
        super.appClosing()
    }

    /**
     * Fired after saving settings and after final 'can exit?' check.
     */
    override fun appWillBeClosed(isRestart: Boolean) {
        println("#AppLifecycleListener appWillBeClosed")
        MyApplicationService.instance.close()
        super.appWillBeClosed(isRestart)
    }
}
