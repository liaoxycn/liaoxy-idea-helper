package com.github.liaoxiangyun.ideaplugin.coderaminder.model

import java.time.LocalDate
import java.time.LocalDateTime

// <div class="event-block event-item" >
class GitSummary {
    /**
     * 今日代码量
     */
    var today: Int = 0

    /**
     * 本周代码量
     */
    var week: Int = 0

    var days = arrayListOf<Day>()
    var commits = arrayListOf<Commit>()

    var messages: String = ""

    class Day {
        var date: LocalDate
        var epochDay: Long
        var total: Int = 0

        constructor(date: LocalDate, epochDay: Long, total: Int) {
            this.date = date
            this.epochDay = epochDay
            this.total = total
        }
    }

    class Commit {
        var id: String = ""
        var projectId: String = ""
        var projectName: String = ""
        var createAt: LocalDateTime
        var date: LocalDate
        var epochDay: Long
        var add: Int = 0
        var del: Int = 0
        var total: Int = 0

        constructor(id: String, projectId: String, projectName: String, createAt: LocalDateTime, add: Int, del: Int, total: Int) {
            this.id = id
            this.projectId = projectId
            this.projectName = projectName
            this.createAt = createAt
            this.add = add
            this.del = del
            this.total = total
            val localDate = createAt.toLocalDate()
            this.date = localDate
            epochDay = localDate.toEpochDay()
        }
    }

}