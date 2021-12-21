package com.github.liaoxiangyun.ideaplugin.commit.util

import com.github.liaoxiangyun.ideaplugin.commit.settings.AppSettingsState
import com.google.gson.Gson
import com.intellij.ui.SystemNotifications
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException
import java.util.regex.Pattern


class HttpHelper {

    //  http://zentaopro.szewec.com/index.php?m=my&f=task

    private var origin = "http://zentaopro.szewec.com"

    private val cookieTemplate = "zentaosid=%s"
    private val loginTemplate = "/index.php?m=user&f=login&referer="


    private var getSessionUrl = "/index.php?m=api&f=getSessionID&t=json"

    //登录成功{status:"success"}
    private var loginUrl = "/index.php?m=user&f=login&t=json&account=%s&password=%s&zentaosid=%s"

    private var taskUrl = "/index.php?m=my&f=task&t=json"
    private var bugUrl = "/index.php?m=my&f=bug&t=json"

    private var cookie = ""
    private var storyUrl = "/index.php?m=my&f=story"
    private var otherQuery = "&type=assignedTo&orderBy=id_desc&recTotal=2&recPerPage=100&pageID=1"

    private val gson: Gson = Gson()
    private var settings: AppSettingsState = AppSettingsState()

    companion object {
        val httpClient: OkHttpClient = OkHttpClient.Builder()
            .build()
    }

    init {
        settings = AppSettingsState.instance
        origin = settings.origin.trim { it <= '/' }
        cookie = settings.cookie.trim()
    }

    private fun getHtml(url: String, checkLogin: Boolean): String {
        val html = getHtml(url)
        if (checkLogin) {
            if (loginCheck(html)) {
                return getHtml(url)
            }
        }
        return html
    }

    private fun getHtml(url: String): String {
        val httpUrl = url.toHttpUrl()
        println("========= getHtml, cookie=$cookie,  httpUrl=${httpUrl}")
        val get: Request.Builder = Request.Builder().url(httpUrl)
            .addHeader("Cookie", cookie) //zentaosid=%s
            .get()
        val request: Request = get.build()
        httpClient.newCall(request).execute().use { response ->
            return if (response.isSuccessful) {
                val body = response.body
                val string = body!!.string()
                if (string?.length < 200) {
                    println("${string.trim()}")
                } else {
                    println("html too long . . . . . .")
                }
                string
            } else {
                throw IOException("Unexpected code $response")
            }
        }
    }

    open fun getBugList(): ArrayList<Bug> {
        val bugHtml = getHtml(origin + bugUrl, true)
        val resp = gson.fromJson(bugHtml, Resp::class.java)
        println(gson.toJson(resp))
        val myTask = gson.fromJson(resp.data, MyTask::class.java)
        return myTask.bugs
    }

    open fun getTaskList(): ArrayList<Task> {
        var taskHtml = getHtml(origin + taskUrl, true)
        val resp = gson.fromJson(taskHtml, Resp::class.java)
        println(gson.toJson(resp))
        val myTask = gson.fromJson(resp.data, MyTask::class.java)
        return myTask.tasks
    }

    private fun loginCheck(html: String): Boolean {
        if (html.contains(loginTemplate)) {
            println("检查是否要登录, loginCheck=true")
            if (settings.user.isBlank() || settings.password.isBlank()) {
                throw RuntimeException("请先填写账号密码")
            }
            val getSessionHtml = getHtml(origin + getSessionUrl)
            val data = gson.fromJson(getSessionHtml, Resp::class.java).data
            val sessionID = gson.fromJson(data, Ses::class.java).sessionID
            println("data=${data}, sessionID=${sessionID}")

            val loginHtml = getHtml(origin + loginUrl.format(settings.user, settings.password, sessionID))
            val loginRes = gson.fromJson(loginHtml, Resp::class.java)
            if (loginRes.status != "success") {
                throw RuntimeException("自动登录失败，请检查账号密码，${loginRes.reason}")
            }
            println("禅道自动登录成功")
            SystemNotifications.getInstance().notify(
                "IDEA助手",
                "禅道自动登录成功", "${settings.user}禅道自动登录成功"
            )
            cookie = cookieTemplate.format(sessionID)
            settings.cookie = cookie
            return true
        }
        println("检查是否要登录, loginCheck=false")
        return false
    }


}