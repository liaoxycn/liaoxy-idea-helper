package com.github.liaoxiangyun.ideaplugin.coderaminder.util

import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant.Companion.PATTERN_M
import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant.Companion.sdf
import com.github.liaoxiangyun.ideaplugin.coderaminder.model.GitRecord
import com.github.liaoxiangyun.ideaplugin.coderaminder.settings.CodeSettingsState
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.intellij.ui.SystemNotifications
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException
import java.util.*


class HttpHelper {

    private var origin = "http://gitlab.szewec.com/e.liaoxiangyun"
    private var session = "session"
    private var enableStatus = false
    private var days = 1
    private var minutes: List<List<Int>> = listOf(listOf())
    private var taskUrl = "?limit=20&offset=0"

    companion object {
        val httpClient: OkHttpClient = OkHttpClient.Builder()
                .build()
        var lastTime: Long = 0
    }

    init {
        val settings = CodeSettingsState.instance
        println(settings.toString())
        try {
            days = Integer.valueOf(settings.days) ?: 1
            if (days <= 0) {
                days = 0
            }
        } catch (e: Exception) {
            days = 1
        }
        origin = settings.origin
        session = settings.session
        val time = settings.reTimeStr
        val split = time.split("-").filter { PATTERN_M.matcher(it).matches() }.map {
            val split = it.split(":")
            listOf(Integer.valueOf(split[0]), Integer.valueOf(split[1]))
        }
        minutes = split
        enableStatus = settings.enableStatus
    }

    private fun getHtml(url: String): String {
        val httpUrl = url.toHttpUrl()
        println("========= getHtml")
        println(httpUrl)
        println(httpUrl.host)
        val get: Request.Builder = Request.Builder().url(httpUrl)
                .addHeader("Accept", "application/json, text/javascript, */*; q=0.01") //Accept: text/htm,application/xhtml+:xml,application/xml;g=0.9,image/webp,image/apng,*/*;g=0.8,application/signed-exchange;,r=b3;q=0.9

                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")//Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6

//                .addHeader("Connection", "Keep-Alive") //Connection: Keep-Alive
                .addHeader("Cookie", "_gitlab_session=${session}") //Connection: Keep-Alive
                .addHeader("Host", httpUrl.host) //Connection: Keep-Alive
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NF 10.0; Win64; x64) AppleWebKit/537.36(KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36 Edg/88.0.705.50") //User-Agent: Mozilla/5.0 (Windows NF 10.0; Win64; x64) AppleWebKit/537.36(KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36 Edg/88.0.705.50

                .addHeader("X-Requested-With", "XMLHttpRequest") //
                .get()
        val request: Request = get.build()
        httpClient.newCall(request).execute().use { response ->
            return if (response.isSuccessful) {
                val body = response.body
                body!!.string()
            } else {
                throw IOException("Unexpected code $response")
            }
        }
    }

    private fun getItem(itemDiv: Element?): GitRecord? {
        if (itemDiv == null) {
            println("item is null")
            return null
        }
        val gitRecord = GitRecord()

        var datetime = itemDiv.selectFirst(".event-item-timestamp time")?.attr("datetime")
        datetime?.replace(Regex("[TZ]"), " ")?.trim()?.let { gitRecord.datetime = Date(sdf.parse(it).time + (8 * 1000 * 3600)) }

        itemDiv.selectFirst(".event-title .author_name")?.text()?.trim()?.let { gitRecord.authorName = it }

        itemDiv.selectFirst(".event-title strong")?.text()?.trim()?.let { gitRecord.branch = it }

        itemDiv.selectFirst(".project-name")?.text()?.trim()?.let { gitRecord.projectName = it }


        return gitRecord
    }

    fun getGitRecordList(): ArrayList<GitRecord> {
        var arr: ArrayList<GitRecord> = arrayListOf()

        val now = Date()
        val calendar = Calendar.getInstance()
        calendar.time = now
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        if (!enableStatus) {
            println("not in enabled")
            return arr
        }

        val any = (hours * 60 + minute) >= (minutes[0][0] * 60 + minutes[0][1])
                && (hours * 60 + minute) <= (minutes[1][0] * 60 + minutes[1][1])
        if (!any) {
            println("now[${hours}:${minute}] not in reTime[${minutes[0][0]}:${minutes[0][1]}-${minutes[1][0]}:${minutes[1][1]}]")
            return arr
        } else {
            println("now[${hours}:${minute}] in reTime[${minutes[0][0]}:${minutes[0][1]}-${minutes[1][0]}:${minutes[1][1]}]")
        }

        val taskHtml: String = this.getHtml("$origin$taskUrl")
//        val taskHtml: String = this.getHtml("$origin$taskUrl")
        val g = Gson()
        val obj: JsonObject = g.fromJson(taskHtml, JsonObject::class.java)
        val htmlStr = obj.get("html").asString
        if (htmlStr == "\\n" || htmlStr.length <= 3) {
            throw RuntimeException("GitLab Session已失效！")
        }
        val document: Document = Jsoup.parse(htmlStr)
        val items = document.select(".event-block.event-item")
        println("items.size = ${items.size}")
        for (item in items) {
            getItem(document)?.let { arr.add(it) }
        }
//        arr.add(GitRecord(sdf.parse("2021-07-09 18:00:00"), "lxy", "project", "develop"))

        if (arr.size > 0) {
            val gitRecord = arr[0]

            //现在距上一次提交时间差
            val js = CalendarUtil.js(gitRecord.datetime, now)

            val msg = "最近一次提交是: ${sdf.format(gitRecord.datetime)}, 你已经${js}天未提交代码了, 请尽快提交代码！"
            if (js >= days) {
                SystemNotifications.getInstance().notify("${Constant.setttingName}",
                        "你已经${js}天未提交代码了", msg)
                throw RuntimeException(msg)
            } else {
                println(msg)
            }
        }
        return arr
    }

    private fun getCalendar(date: Date?): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date ?: Date()
        return calendar
    }

}