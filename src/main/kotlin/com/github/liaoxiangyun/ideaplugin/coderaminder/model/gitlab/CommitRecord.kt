package com.github.liaoxiangyun.ideaplugin.coderaminder.model.gitlab

/**
 * 提交记录
 */
open class CommitRecord {
    var id: String = ""
    var short_id: String = ""
    var title: String = ""
    var message: String = ""
    var created_at: String = ""
    var parent_ids = arrayListOf<String>()

    //作者
    var author_name: String = ""
    var author_email: String = ""
    var authored_date: String = ""

    //提交人信息
    var committer_name: String = ""
    var committer_email: String = ""
    var committer_date: String = ""


    /**
     * 合并的代码不统计
     */
    open fun isMerge(): Boolean {
        if (parent_ids.size > 0) {
            return true
        }
        return false
    }

}