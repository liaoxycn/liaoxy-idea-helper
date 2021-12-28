package com.github.liaoxiangyun.ideaplugin.commit.util

import com.github.liaoxiangyun.ideaplugin.commit.settings.AppSettingsState
import com.google.gson.Gson
import com.intellij.ui.SystemNotifications
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


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

    private fun httpGetJSON(url: String, checkLogin: Boolean): Resp {
        var html = httpGet(url)
        var json = gson.fromJson(html, Resp::class.java)
        if (checkLogin && loginCheck(json.data)) {
            html = httpGet(url)
            return gson.fromJson(html, Resp::class.java)
        }
        return json
    }

    private fun httpGet(url: String): String {
        val httpUrl = url.toHttpUrl()
        println("========= httpGet, cookie=\"$cookie\",  httpUrl=${httpUrl}")
        val get: Request.Builder = Request.Builder().url(httpUrl)
            .addHeader("Cookie", cookie) //zentaosid=%s
            .get()
        val request: Request = get.build()
        httpClient.newCall(request).execute().use { response ->
            return if (response.isSuccessful) {
                val body = response.body
                val string = body!!.string()
                if (string?.length < 300) {
                    println("${string.trim()}")
                }
                string
            } else {
                throw IOException("Unexpected code $response")
            }
        }
    }

    open fun getBugList(): ArrayList<Bug> {
        val resp = httpGetJSON(origin + bugUrl, true)
        println("resp status=${gson.toJson(resp.status)}")
        val myTodo = gson.fromJson(resp.data, MyTodo::class.java)
        return myTodo.bugs
    }

    open fun getTaskList(): ArrayList<Task> {
        var resp = httpGetJSON(origin + taskUrl, true)
        println("resp status=${gson.toJson(resp.status)}")
        val myTodo = gson.fromJson(resp.data, MyTodo::class.java)
        return myTodo.tasks
    }

    private fun loginCheck(data: String): Boolean {
        println(
            "检查是否要登录, (html.length < 300)${data.length < 300} " +
                    "&& ${data.contains("locate")}(html.contains(\"locate\"))"
        )
        if (data.length < 300 && data.contains("locate")) {
            if (settings.user.isBlank() || settings.password.isBlank()) {
                throw RuntimeException("请先填写账号密码")
            }
            val getSessionHtml = httpGet(origin + getSessionUrl)
            val data = gson.fromJson(getSessionHtml, Resp::class.java).data
            val sessionID = gson.fromJson(data, Ses::class.java).sessionID
            println("getSessionID成功！ sessionID=${sessionID}")

            val loginHtml = httpGet(origin + loginUrl.format(settings.user, settings.password, sessionID))
            val loginRes = gson.fromJson(loginHtml, Resp::class.java)
            if (loginRes.status != "success") {
                throw RuntimeException("自动登录失败，请检查账号密码，${loginRes.reason}")
            }
            println("禅道自动登录成功！")
            SystemNotifications.getInstance().notify(
                "IDEA助手",
                "禅道自动登录成功", "${settings.user}禅道自动登录成功"
            )
            cookie = cookieTemplate.format(sessionID)
            settings.cookie = cookie
            return true
        }

        return false
    }


}