package com.github.liaoxiangyun.ideaplugin.commit

import com.github.liaoxiangyun.ideaplugin.commit.model.CommitLine
import com.github.liaoxiangyun.ideaplugin.commit.util.Util

/**
 * @author Damien Arrachequesne <damien.arrachequesne></damien.arrachequesne>@gmail.com>
 */
class CommitMessage {
    var changeType: ChangeType? = null
        private set

    var map: MutableMap<String, Any> = mutableMapOf()

    private constructor() {

    }

    constructor(changeType: ChangeType?, map: MutableMap<String, Any>) {
        this.changeType = changeType
        this.map = map
    }

    open fun getVerify(): String {
        if (changeType == null) {
            return "格式错误"
        }
        for (commitLine in changeType!!.commitLines()) {
            val value = this.map[commitLine.lineName]
            if (commitLine.require) {
                if (Util.isEmpty(value)) {
                    return commitLine.lineName + "不能为空"
                }
            }
            if (value != null && (value is String) && commitLine.regex != CommitLine.REGEX) {
                val matcher = commitLine.regex.matcher(value)
                if (!matcher.matches()) {
                    return "格式错误"
                }
            }
        }
        return ""
    }

    override fun toString(): String {
        val builder = StringBuilder()
        try {
            builder.append("【修改类型】：")
            builder.append(changeType!!.title())
            builder.append(System.lineSeparator())
            for (commitLine in this.changeType!!.commitLines()) {
                val value = this.map[commitLine.lineName]
                if (!commitLine.require && Util.isEmpty(value)) {
                    continue
                }
                builder.append("【${commitLine.lineName}】：$value")
                builder.append(System.lineSeparator())
            }
        } catch (e: Exception) {
        }
        return builder.toString()
    }

    companion object {
        fun parse(message: String): CommitMessage {
            val commitMessage = CommitMessage()
            try {
                val split = message.trim().split("\n【").map { if (it.startsWith("【")) it else "【$it" }
                for (index in split.withIndex()) {
                    val split1 = index.value.split("：")
                    val key = split1[0]?.trim().trim { it == '【' || it == '】' }
                    val value = split1[1]?.trim()
                    if (!Util.isEmpty(key)) {
                        commitMessage.map[key] = value
                        if (ChangeType.TYPE_NAME == key) {
                            commitMessage.changeType = ChangeType.titleOf(value)!!
                        }
                    } else {
                        continue
                    }
                }
            } catch (e: RuntimeException) {
                println("CommitMessage parse error!!  \n" + e.message)
            }
            return commitMessage
        }
    }
}