package com.github.liaoxiangyun.ideaplugin.coderaminder.util

import cn.hutool.json.JSONUtil
import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import com.github.liaoxiangyun.ideaplugin.coderaminder.model.GitSummary
import com.github.liaoxiangyun.ideaplugin.coderaminder.model.gitlab.CommitDetail
import com.github.liaoxiangyun.ideaplugin.coderaminder.model.gitlab.CommitRecord
import com.github.liaoxiangyun.ideaplugin.coderaminder.model.gitlab.Event
import com.github.liaoxiangyun.ideaplugin.coderaminder.settings.CodeSettingsState
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.regex.Pattern
import java.util.stream.Collectors


class HttpHelper {

    private var settings: CodeSettingsState = CodeSettingsState.instance
    private var branches = arrayListOf<String>()
    private var titleIgnorePattern: Pattern = Pattern.compile("")
    private var messageIgnorePattern: Pattern = Pattern.compile("")
    private var branch = ""

    //第一步，生成私钥

    //第二步，获取当前用户可见的所有项目（即使用户不是成员）
    //接口地址：gitlab的地址/api/v4/projects/?private_token=xxx
    private var url_projects = "/api/v4/projects?pagination=keyset&per_page=100&order_by=created_at&sort=desc"

    //    第三步，遍历项目，根据项目id获取分支列表
//    接口地址：http://gitlab地址/api/v4/projects/项目id/repository/branches?private_token=xxx
    private var url_branches = "/api/v4/projects/{项目id}/repository/branches"

    //    第四步，遍历分支，根据分支name获取commits
//    注意，当title或message首单词为Merge，表示合并操作，剔除此代码量
//    接口地址：
//    http://gitlab地址/api/v4/projects/项目id/repository/commits?ref_name=master&private_token=xxx
    private var url_commits = "/api/v4/projects/%s/repository/commits?ref_name=%s&since=%s&per_page=100&page=%s"

    //    第五步，根据commits的id获取代码量
//    接口地址:
//    http://gitlab地址/api/v4/projects/项目id/repository/commits/commits的id?private_token=xxx
    private val url_commits_detail = "/api/v4/projects/%s/repository/commits/%s"

    private var url_events = "/api/v4/users/{用户id}/events?per_page=100&page={page}"

    companion object {
        val httpClient: OkHttpClient = OkHttpClient.Builder()
                .build()
    }

    init {
        branches = settings.branches.split("|").map { it.trim() }.filter { it.isNotBlank() } as ArrayList<String>
        println("branches=$branches")
        branch = if (branches.isNotEmpty()) {
            branches[0].trim()
        } else {
            "develop"
        }

        titleIgnorePattern = Pattern.compile(settings.titleIgnore)
        messageIgnorePattern = Pattern.compile(settings.messageIgnore)
    }

    private fun getHtml(url: String): String {
        val httpUrl = url.toHttpUrl()
        println(httpUrl)
        val get: Request.Builder = Request.Builder().url(httpUrl)
                .addHeader("PRIVATE-TOKEN", settings.token)
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


    fun getField(filed: String, since: LocalDateTime?): ArrayList<String> {
        var sinceDateTime = if (since !== null) since else LocalDateTime.of(CalendarUtil.getWeekDays(-1)[0], LocalTime.MIN)

        var list: List<Event>?
        var strs: ArrayList<String> = arrayListOf()
        var page = 0
        val replace = url_events.replace("{用户id}", "${settings.getUser().id}")
        do {
            ++page
            list = getList(replace.replace("{page}", page.toString()), Event::class.java)
            val createdAt = parseTime(list.last().created_at)
            println("#遍历用户事件 page=$page 本页最早时间=${CalendarUtil.dateStr(createdAt.toLocalDate())}")
            //过滤 actionName=pushed to
            for (e in list) {
                if (filed == "projectId")
                    strs.add(e.project_id)
                else if (filed == "branch") {
                    var bn = e.data?.ref ?: ""
                    if (bn.contains('/')) {
                        bn = bn.substring(bn.lastIndexOf('/'), bn.length)
                        strs.add(bn)
                    }
                }
            }
        } while (createdAt > sinceDateTime)
        strs = strs.filter { it.isNotBlank() }.distinct() as ArrayList<String>
        return strs
    }

    private fun getCommitIds(projectId: String, branch: String, sinceDateTime: LocalDateTime): ArrayList<String> {
        val ll = arrayListOf<String>()
        var page = 0
        do {
            val url = url_commits.format(projectId, branch, sinceDateTime.format(Constant.FORMATTER), ++page)
            val list = getList(url, CommitRecord::class.java)
            val cs = list.filter {
                !it.isMerge() && !it.message.startsWith("Revert")
                        && !titleIgnorePattern.matcher(it.title).matches()
                        && !messageIgnorePattern.matcher(it.message).matches()
                        && it.committer_email == settings.getUser().email
            }.parallelStream().map { commit ->
                commit.id
            }.collect(Collectors.toList())
            ll.addAll(cs)
        } while (list.isNotEmpty())
        println("projectId=$projectId branch=$branch 最近两周共${ll.size}条提交记录")
        return ll
    }

    fun getSummary2(): GitSummary {
        val start = System.currentTimeMillis();

        val gitSummary = GitSummary()
        val now = LocalDateTime.now()
        val toDayEpochDay = now.toLocalDate().toEpochDay()
        val sinceDateTime = LocalDateTime.of(CalendarUtil.getWeekDays(-1)[0], LocalTime.MIN)
        println("两周前是 ${CalendarUtil.dateStr(sinceDateTime.toLocalDate())}")

        val projectIds = getField("projectId", sinceDateTime)

        val commits = projectIds.parallelStream().map { projectId ->
            val commitIds = branches.parallelStream().flatMap { branch ->
                getCommitIds(projectId, branch, sinceDateTime).stream()
            }.distinct().collect(Collectors.toList())
            val cs = commitIds.parallelStream().map { commitId ->
                val detail = getObj(url_commits_detail.format(projectId, commitId), CommitDetail::class.java)
                val stats = detail.stats
                val commit = GitSummary.Commit(detail.id, projectId, "",
                        detail.getTime(),
                        stats.additions, stats.deletions, stats.total)
                commit
            }.collect(Collectors.toList())
            cs.stream()
        }.flatMap { it }.collect(Collectors.toList())

        var days = arrayListOf<GitSummary.Day>()

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
        gitSummary.days = days

        //统计今日
        if (epochDayDataMap.containsKey(toDayEpochDay)) {
            val day = epochDayDataMap[toDayEpochDay]!!
            gitSummary.today = day.total
        }

        //统计本周
        val weeks1 = CalendarUtil.getWeekDays(0).filter { !CalendarUtil.isOffDay(it) }
        val gitDays1: List<GitSummary.Day> = weeks1.map {
            epochDayDataMap[it.toEpochDay()]
                    ?: GitSummary.Day(LocalDate.ofEpochDay(it.toEpochDay()), it.toEpochDay(), 0)
        }
        gitSummary.week = gitDays1.stream().mapToInt { it.total }.sum()
        val weekMsg1 = gitDays1.stream().map { "${day2zh(it.date)}(${it.total})" }.collect(Collectors.joining(" "))

        //统计上周
        val weeks2 = CalendarUtil.getWeekDays(-1).filter { !CalendarUtil.isOffDay(it) }
        val gitDays2: List<GitSummary.Day> = weeks2.map {
            epochDayDataMap[it.toEpochDay()]
                    ?: GitSummary.Day(LocalDate.ofEpochDay(it.toEpochDay()), it.toEpochDay(), 0)
        }
        var weekSum2 = gitDays2.stream().mapToInt { it.total }.sum()
        val weekMsg2 = gitDays2.stream().map { "${day2zh(it.date)}(${it.total})" }.collect(Collectors.joining(" "))

        val avg1 = gitSummary.week / weeks1.size
        val avg2 = weekSum2 / weeks2.size
        val messages = "" +
                "-上周：${avg2}=${weekSum2}/${weeks2.size} 行\n  ${weekMsg2}\n" +
                "-今日：${gitSummary.today} 行 \n" +
                "-本周：${avg1}=${gitSummary.week}/${weeks1.size} 行\n  ${weekMsg1}\n" +
                "\n" +
                "最低代码量标准：java>60${if (avg1 >= 60) "(√)" else "(×)"} 前端>70${if (avg1 >= 70) "(√)" else "(×)"}\n" +
                "本次耗时${System.currentTimeMillis() - start}ms\n"
        gitSummary.messages = messages


        return gitSummary
    }

    private fun day2zh(date: LocalDate): String {
        //计算相差日，转成 昨天、今天、明天、xx号
        return "${date.dayOfMonth}"
    }

    private fun parseTime(date: String): LocalDateTime {
        val str = date.replace("T", " ").substring(0, 19)
        return LocalDateTime.parse(str, Constant.FORMATTER)
    }

    private fun <T> getObj(url: String, clazz: Class<T>): T {
        val html = getHtml(settings.origin + url)
        if (html.startsWith("{\n    \"message")) {
            RuntimeException(html)
        }
        return JSONUtil.toBean(html, clazz)
    }

    private fun <T> getList(url: String, clazz: Class<T>): List<T> {
        val html = getHtml(settings.origin + url)
        if (html.startsWith("{\n    \"message")) {
            RuntimeException(html)
        }
        return json2List(html, clazz)
    }

    private fun <T> json2List(json: String, clazz: Class<T>): List<T> {
        return JSONUtil.toList(JSONUtil.parseArray(json), clazz)
    }


}