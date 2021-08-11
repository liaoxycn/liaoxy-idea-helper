package com.github.liaoxiangyun.ideaplugin.listeners

import com.github.liaoxiangyun.ideaplugin.services.MyApplicationService
import com.intellij.ide.ApplicationInitializedListener
import com.intellij.ide.ApplicationLoadListener
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager

class MyApplicationManagerListener : ApplicationInitializedListener, ApplicationLoadListener {


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
}
