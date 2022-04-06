package com.github.liaoxiangyun.ideaplugin.coderaminder.util

import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import com.github.liaoxiangyun.ideaplugin.coderaminder.model.GitSummary
import com.github.liaoxiangyun.ideaplugin.coderaminder.model.gitlab.Branch
import com.github.liaoxiangyun.ideaplugin.coderaminder.model.gitlab.CommitDetail
import com.github.liaoxiangyun.ideaplugin.coderaminder.model.gitlab.CommitRecord
import com.github.liaoxiangyun.ideaplugin.coderaminder.model.gitlab.Project
import com.github.liaoxiangyun.ideaplugin.coderaminder.settings.CodeSettingsState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.time.LocalDateTime
import java.util.stream.Collectors


class HttpHelper {

    private var settings: CodeSettingsState? = null
    private val gson: Gson = Gson()
    private var origin = "http://gitlab.szewec.com/"
    private var branches = arrayListOf<String>()

    //第一步，生成私钥
    private var token = ""

    //第二步，获取当前用户可见的所有项目（即使用户不是成员）
    //接口地址：gitlab的地址/api/v4/projects/?private_token=xxx
    private var url_projects = "/api/v4/projects"

    //    第三步，遍历项目，根据项目id获取分支列表
//    接口地址：http://gitlab地址/api/v4/projects/项目id/repository/branches?private_token=xxx
    private var url_branches = "/api/v4/projects/{项目id}/repository/branches"

    //    第四步，遍历分支，根据分支name获取commits
//    注意，当title或message首单词为Merge，表示合并操作，剔除此代码量
//    接口地址：
//    http://gitlab地址/api/v4/projects/项目id/repository/commits?ref_name=master&private_token=xxx
    private var url_commits = "/api/v4/projects/{项目id}/repository/commits?ref_name={master}&since={since}"

    //    第五步，根据commits的id获取代码量
//    接口地址:
//    http://gitlab地址/api/v4/projects/项目id/repository/commits/commits的id?private_token=xxx
    private var url_commits_detail = "/api/v4/projects/{项目id}/repository/commits/{commitsId}"

    companion object {
        val httpClient: OkHttpClient = OkHttpClient.Builder()
                .build()
        var lastTime: Long = 0
    }

    init {
        val settings = CodeSettingsState.instance
        this.settings = settings
        origin = settings.origin
        token = settings.token
        branches = settings.branches.split("|").map { it.trim() } as ArrayList<String>

    }

    private fun getHtml(url: String): String {
        val httpUrl = url.toHttpUrl()
        println(httpUrl)
        val get: Request.Builder = Request.Builder().url(httpUrl)
                .addHeader("PRIVATE-TOKEN", token)
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


    fun getSummary(): GitSummary {
        val gitSummary = GitSummary()

        val now = LocalDateTime.now()
        val toDayEpochDay = now.toLocalDate().toEpochDay()

        var days = arrayListOf<GitSummary.Day>()
        var commits = arrayListOf<GitSummary.Commit>()

        val projects = getList<Project>(url_projects)
        for (project in projects) {
            val branches = getList<Branch>(url_branches.replace("{项目id}", project.id))
            for (branch in branches) {
                if (this.branches.contains(branch.name)) {
                    val commitRecords = getList<CommitRecord>(url_commits.replace("{项目id}", project.id)
                            .replace("{master}", branch.name))
                    for (record in commitRecords) {
                        val detail = getObj<CommitDetail>(url_commits_detail.replace("{项目id}", project.id)
                                .replace("{commitsId}", record.id)
                                .replace("{since}", now.plusDays(-14).format(Constant.FORMATTER)))
                        val stats = detail.stats
                        val commit = GitSummary.Commit(detail.id, project.id, project.nameWithNameSpace,
                                parseTime(detail.committer_date), stats.additions, stats.deletions, stats.total)
                        commits.add(commit)
                    }
                }
            }
        }


        //以每天分组
        val epochDayMap = commits.stream().collect(Collectors.groupingBy(GitSummary.Commit::epochDay))
        var epochDayDataMap = mutableMapOf<Long, GitSummary.Day>()

        for (entry in epochDayMap.entries) {
            val epochDay = entry.key
            val commits = entry.value
            val sum = commits.stream().mapToInt(GitSummary.Commit::add).sum()
            val day = GitSummary.Day(commits[0].date, epochDay, sum)
            days.add(day)
            epochDayDataMap[epochDay] = day
        }

        days.sortBy { it.epochDay }

        //统计今日
        if (epochDayDataMap.containsKey(toDayEpochDay)) {
            val day = epochDayDataMap[toDayEpochDay]!!
            gitSummary.today = day.total
        }
        //统计本周
        val weekDays = CalendarUtil.getWeekDays(0)
        for (weekDay in weekDays) {
            val day = epochDayDataMap[weekDay.toEpochDay()]
            if (day != null) {
                gitSummary.week.and(day.total)
            }
        }

        val messages = "今日代码量：${gitSummary.today}" +
                "本周代码量：${gitSummary.week}"
        gitSummary.messages = messages


        return gitSummary
    }

    private fun parseTime(date: String): LocalDateTime {
        val str = date.replace("T", " ").substring(0, 19)
        return LocalDateTime.parse(str, Constant.FORMATTER)
    }

    private fun <T> getObj(url: String): T {
        val html = getHtml(origin + url)
        if (html.startsWith("{\n    \"message")) {
            RuntimeException(html)
        }
        return gson.fromJson(html, object : TypeToken<T>() {}.type)
    }

    private fun <T> getList(url: String): List<T> {
        val html = getHtml(origin + url)
        if (html.startsWith("{\n    \"message")) {
            RuntimeException(html)
        }
        return gson.fromJson(html, object : TypeToken<T>() {}.type)
    }

    private fun <T> jsonToList(jsonList: String): List<T> {
        return gson.fromJson(jsonList, object : TypeToken<ArrayList<T>>() {}.type)
    }


}