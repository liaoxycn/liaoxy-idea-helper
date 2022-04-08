package com.github.liaoxiangyun.ideaplugin.coderaminder.model.gitlab

import cn.hutool.json.JSONUtil
import com.github.liaoxiangyun.ideaplugin.coderaminder.common.Constant
import java.time.LocalDateTime

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

    open fun getTime(): LocalDateTime {
        var str = ""
        if (this.created_at?.length > 19) {
            str = this.created_at.replace("T", " ").substring(0, 19)
        } else if (this.committer_date?.length > 19) {
            str = this.committer_date.replace("T", " ").substring(0, 19)
        } else if (this.authored_date?.length > 19) {
            str = this.authored_date.replace("T", " ").substring(0, 19)
        } else {
            println(JSONUtil.toJsonStr(this))
        }
        return LocalDateTime.parse(str, Constant.FORMATTER)
    }

}