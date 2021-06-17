package com.github.liaoxiangyun.myideaplugin.commit.util

import com.github.liaoxiangyun.myideaplugin.commit.settings.AppSettingsState
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException


class HttpHelper {

    //  http://zentaopro.szewec.com/index.php?m=my&f=task
    private var origin = "http://zentaopro.szewec.com/"
    private var cookie = ""
    private var taskUrl = "/index.php?m=my&f=task"
    private var bugUrl = "/index.php?m=my&f=bug"
    private var storyUrl = "/index.php?m=my&f=story"
    private var otherQuery = "&type=assignedTo&orderBy=id_desc&recTotal=2&recPerPage=100&pageID=1"
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
            .build()

    init {
        val settings = AppSettingsState.instance
        origin = settings.origin
        cookie = settings.cookie
    }

    private fun getHtml(url: String): String {
        val httpUrl = url.toHttpUrl()
        val get: Request.Builder = Request.Builder().url(httpUrl)
                .addHeader("Accept", "text/htm,application/xhtml+:xml,application/xml;g=0.9,image/webp,image/apng,*/*;g=0.8,application/signed-exchange;,r=b3;q=0.9") //Accept: text/htm,application/xhtml+:xml,application/xml;g=0.9,image/webp,image/apng,*/*;g=0.8,application/signed-exchange;,r=b3;q=0.9

                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")//Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6

                .addHeader("Connection", "Keep-Alive") //Connection: Keep-Alive
                .addHeader("Cookie", cookie) //Connection: Keep-Alive
                .addHeader("Host", httpUrl.host) //Connection: Keep-Alive
                .addHeader("Referer", httpUrl.encodedPath) //Connection: Keep-Alive
                .addHeader("Upgrade-Insecure-Requests", "1") //Upgrade-Insecure-Requests: 1
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NF 10.0; Win64; x64) AppleWebKit/537.36(KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36 Edg/88.0.705.50") //User-Agent: Mozilla/5.0 (Windows NF 10.0; Win64; x64) AppleWebKit/537.36(KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36 Edg/88.0.705.50

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


    open fun getBugList(): ArrayList<ArrayList<String>> {
        val bugHtml = getHtml(origin + bugUrl)
        //return arrayListOf("id14533", "id235332", "id236573", "id1745")
        val document: Document = Jsoup.parse(bugHtml)
        val tbody = document.getElementsByTag("tbody").first()
        var trList = arrayListOf<ArrayList<String>>()
        for (trIndex in tbody.getElementsByTag("tr").withIndex()) {
            var tdList = arrayListOf<String>()
            for (tdIndex in trIndex.value.getElementsByTag("td").withIndex()) {
                tdList.add(tdIndex.value.text())
            }
            trList.add(tdList)
        }
        return trList
    }

    open fun getStoryList(): ArrayList<ArrayList<String>> {
        val storyHtml = getHtml(origin + storyUrl)
        //return arrayListOf("id14533", "id235332", "id236573", "id1745")
        val document: Document = Jsoup.parse(storyHtml)
        val tbody = document.getElementsByTag("tbody").first()
        var trList = arrayListOf<ArrayList<String>>()
        for (trIndex in tbody.getElementsByTag("tr").withIndex()) {
            var tdList = arrayListOf<String>()
            for (tdIndex in trIndex.value.getElementsByTag("td").withIndex()) {
                tdList.add(tdIndex.value.text())
            }
            trList.add(tdList)
        }
        return trList
    }

    open fun getTaskList(): ArrayList<ArrayList<String>> {
        val taskHtml = getHtml(origin + taskUrl)
        //return arrayListOf("id14533", "id235332", "id236573", "id1745")
        val document: Document = Jsoup.parse(taskHtml)
        val tbody = document.getElementsByTag("tbody").first()
        var trList = arrayListOf<ArrayList<String>>()
        for (trIndex in tbody.getElementsByTag("tr").withIndex()) {
            var tdList = arrayListOf<String>()
            for (tdIndex in trIndex.value.getElementsByTag("td").withIndex()) {
                tdList.add(tdIndex.value.text())
            }
            trList.add(tdList)
        }
        return trList
    }


}