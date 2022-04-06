package com.github.liaoxiangyun.ideaplugin.coderaminder.model.gitlab

/**
 * 提交记录详情
 */
class CommitDetail : CommitRecord() {
    var stats: Stats = Stats(0, 0, 0)
    var status: String = ""


    open class Stats {
        var additions: Int = 0
        var deletions: Int = 0
        var total: Int = 0

        constructor(additions: Int, deletions: Int, total: Int) {
            this.additions = additions
            this.deletions = deletions
            this.total = total
        }

    }


}