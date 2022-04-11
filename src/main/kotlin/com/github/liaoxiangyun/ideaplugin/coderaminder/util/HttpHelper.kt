package com.github.liaoxiangyun.ideaplugin.coderaminder.util

import cn.hutool.json.JSONUtil
import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import com.github.liaoxiangyun.ideaplugin.coderaminder.model.GitSummary
import com.github.liaoxiangyun.ideaplugin.coderaminder.model.gitlab.CommitDetail
import com.github.liaoxiangyun.ideaplugin.coderaminder.model.gitlab.Event
import com.github.liaoxiangyun.ideaplugin.coderaminder.model.gitlab.Project
import com.github.liaoxiangyun.ideaplugin.coderaminder.settings.CodeSettingsState
import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.gitlab.api.GitlabAPI
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.stream.Collectors


class HttpHelper {

    private var settings: CodeSettingsState = CodeSettingsState.instance
    private val gson: Gson = Gson()
    private var origin = "http://gitlab.szewec.com/"
    private var branches = arrayListOf<String>()
    private var branch = ""

    //第一步，生成私钥
    private var token = ""

    //第二步，获取当前用户可见的所有项目（即使用户不是成员）
    //接口地址：gitlab的地址/api/v4/projects/?private_token=xxx
    private var url_projects = "/api/v4/projects?pagination=keyset&per_page=2&order_by=created_at&sort=desc"

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
    private val url_commits_detail = "/api/v4/projects/{项目id}/repository/commits/{commitsId}"

    private var url_events = "/api/v4/users/{用户id}/events?per_page=100&page={page}"

    companion object {
        val httpClient: OkHttpClient = OkHttpClient.Builder()
                .build()
    }

    init {
        origin = settings.origin
        token = settings.token
        branches = settings.branches.split("|").map { it.trim() } as ArrayList<String>
        branch = if (branches.isNotEmpty()) {
            branches[0].trim()
        } else {
            "develop"
        }
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


    private fun getUserId(): Int {
        if (settings.gitlabUser == null) {
            settings.gitlabUser = GitlabAPI.connect(settings.origin, settings.token).user
        }
        return settings.gitlabUser.id
    }

    fun getSummary2(): GitSummary {

        val gitSummary = GitSummary()
        val now = LocalDateTime.now()
        val toDayEpochDay = now.toLocalDate().toEpochDay()
        val sinceDateTime = now.plusDays(-14)
        println("两周前是 ${CalendarUtil.dateStr(sinceDateTime.toLocalDate())}")

        var commitMap = mutableMapOf<String, Event.Data.Commit>()

        var list: List<Event>?
        var page = 0
        val replace = url_events.replace("{用户id}", "${getUserId()}")
        do {
            ++page
            list = getList(replace.replace("{page}", page.toString()), Event::class.java)
            val createdAt = parseTime(list.last().created_at)
            println("#遍历用户事件 page=$page 本页最早时间=${CalendarUtil.dateStr(createdAt.toLocalDate())}")
            //过滤 actionName=pushed to
            for (e in list.filter { it.action_name == "pushed to" && it.data?.ref?.endsWith(branch) == true }) {
                val projectId = e.project_id
                e.data.let { d ->
                    val path_with_namespace = d?.project?.path_with_namespace ?: ""
                    d?.commits?.forEach {
                        if (((it.author.email == d.user_email) && !it.message.startsWith("Merge branch"))) {
                            it.project_id = projectId
                            it.path_with_namespace = path_with_namespace
                            commitMap[it.id] = it
                        }
                    }
                }
            }
        } while (createdAt > sinceDateTime)
        println("commitMap.size = ${commitMap.size}")

        val values = commitMap.values
        val commits = values.parallelStream().map {
            val url = url_commits_detail.replace("{项目id}", it.project_id)
                    .replace("{commitsId}", it.id)
            val detail = getObj(url, CommitDetail::class.java)
            val stats = detail.stats
            val commit = GitSummary.Commit(detail.id, it.project_id, it.path_with_namespace,
                    detail.getTime(),
                    stats.additions, stats.deletions, stats.total)
            commit
        }.collect(Collectors.toList())
        var days = arrayListOf<GitSummary.Day>()

        println("commits =============================\n${JSONUtil.toJsonStr(commits)}\n")

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
                "法定休息日不统计\n"
        gitSummary.messages = messages

        return gitSummary
    }

    private fun day2zh(date: LocalDate): String {
        val now = LocalDate.now()
        //计算相差日，转成 昨天、今天、明天、xx号
        return "${date.dayOfMonth}"
    }

    private fun parseTime(date: String): LocalDateTime {
        val str = date.replace("T", " ").substring(0, 19)
        return LocalDateTime.parse(str, Constant.FORMATTER)
    }

    private fun <T> getObj(url: String, clazz: Class<T>): T {
        val html = getHtml(origin + url)
        if (html.startsWith("{\n    \"message")) {
            RuntimeException(html)
        }
        return JSONUtil.toBean(html, clazz)
    }

    private fun <T> getList(url: String, clazz: Class<T>): List<T> {
        val html = getHtml(origin + url)
        if (html.startsWith("{\n    \"message")) {
            RuntimeException(html)
        }
        return json2List(html, clazz)
    }

    private fun <T> json2List(json: String, clazz: Class<T>): List<T> {
        return JSONUtil.toList(JSONUtil.parseArray(json), clazz)
    }


    open fun test() {
        val s = "[{\n" +
                "        \"id\":219,\n" +
                "        \"description\":\"\",\n" +
                "        \"name\":\"share-5.2.3.8\",\n" +
                "        \"name_with_namespace\":\"develop / share-5.2.3.8\",\n" +
                "        \"path\":\"share-5.2.3.8\",\n" +
                "        \"path_with_namespace\":\"develop/share-5.2.3.8\",\n" +
                "        \"created_at\":\"2019-07-10T19:59:29.855+08:00\",\n" +
                "        \"default_branch\":\"master\",\n" +
                "        \"tag_list\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"ssh_url_to_repo\":\"git@127.0.0.1:develop/share-5.2.3.8.git\",\n" +
                "        \"http_url_to_repo\":\"http://127.0.0.1/develop/share-5.2.3.8.git\",\n" +
                "        \"web_url\":\"http://127.0.0.1/develop/share-5.2.3.8\",\n" +
                "        \"readme_url\":\"http://127.0.0.1/develop/share-5.2.3.8/blob/master/README.md\",\n" +
                "        \"avatar_url\":null,\n" +
                "        \"star_count\":0,\n" +
                "        \"forks_count\":0,\n" +
                "        \"last_activity_at\":\"2019-07-11T02:53:44.831+08:00\",\n" +
                "        \"_links\":{\n" +
                "            \"self\":\"http://127.0.0.1/api/v4/projects/219\",\n" +
                "            \"issues\":\"http://127.0.0.1/api/v4/projects/219/issues\",\n" +
                "            \"merge_requests\":\"http://127.0.0.1/api/v4/projects/219/merge_requests\",\n" +
                "            \"repo_branches\":\"http://127.0.0.1/api/v4/projects/219/repository/branches\",\n" +
                "            \"labels\":\"http://127.0.0.1/api/v4/projects/219/labels\",\n" +
                "            \"events\":\"http://127.0.0.1/api/v4/projects/219/events\",\n" +
                "            \"members\":\"http://127.0.0.1/api/v4/projects/219/members\"\n" +
                "        },\n" +
                "        \"archived\":false,\n" +
                "        \"visibility\":\"private\",\n" +
                "        \"resolve_outdated_diff_discussions\":false,\n" +
                "        \"container_registry_enabled\":true,\n" +
                "        \"issues_enabled\":true,\n" +
                "        \"merge_requests_enabled\":true,\n" +
                "        \"wiki_enabled\":true,\n" +
                "        \"jobs_enabled\":true,\n" +
                "        \"snippets_enabled\":true,\n" +
                "        \"shared_runners_enabled\":true,\n" +
                "        \"lfs_enabled\":true,\n" +
                "        \"creator_id\":14,\n" +
                "        \"namespace\":{\n" +
                "            \"id\":17,\n" +
                "            \"name\":\"develop\",\n" +
                "            \"path\":\"develop\",\n" +
                "            \"kind\":\"group\",\n" +
                "            \"full_path\":\"develop\",\n" +
                "            \"parent_id\":null\n" +
                "        },\n" +
                "        \"import_status\":\"none\",\n" +
                "        \"open_issues_count\":0,\n" +
                "        \"public_jobs\":true,\n" +
                "        \"ci_config_path\":null,\n" +
                "        \"shared_with_groups\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"only_allow_merge_if_pipeline_succeeds\":false,\n" +
                "        \"request_access_enabled\":false,\n" +
                "        \"only_allow_merge_if_all_discussions_are_resolved\":false,\n" +
                "        \"printing_merge_request_link_enabled\":true,\n" +
                "        \"merge_method\":\"merge\",\n" +
                "        \"permissions\":{\n" +
                "            \"project_access\":null,\n" +
                "            \"group_access\":{\n" +
                "                \"access_level\":40,\n" +
                "                \"notification_level\":3\n" +
                "            }\n" +
                "        }\n" +
                "    }]\n"
        val jsonToList = json2List(s, Project::class.java)
        println("jsonToList = ${jsonToList}")


    }


}