package com.github.liaoxiangyun.ideaplugin.commit

import com.github.liaoxiangyun.ideaplugin.commit.model.CommitLine
import org.apache.commons.lang.StringUtils

/**
 * @author Damien Arrachequesne <damien.arrachequesne></damien.arrachequesne>@gmail.com>
 */
class CommitMessage {
    var changeType: ChangeType? = null
        private set
    var lines: ArrayList<Any>? = null

    private constructor() {
        lines = arrayListOf()
    }

    constructor(changeType: ChangeType?, lines: ArrayList<Any>) {
        this.changeType = changeType
        this.lines = lines
    }

    open fun getVerify(): String {
        if (changeType == null) {
            return "格式错误"
        }
        if (changeType?.lineNames()?.size !== lines?.size) {
            return "格式错误"
        }
        for ((index, commitLine) in changeType!!.lineNames().withIndex()) {
            val value = lines?.get(index)
            if (commitLine.require) {
                if (value == null || (value is String && value.trim() == "")) {
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
            for ((line, lineName) in this.changeType!!.lineNames().withIndex()) {
                val value = this.lines!![line]
                builder.append("【${lineName.lineName}】：$value")
                builder.append(System.lineSeparator())
            }
        } catch (e: Exception) {
        }
        return builder.toString()
    }

    private fun formatClosedIssue(closedIssue: String): String {
        val trimmed = closedIssue.trim { it <= ' ' }
        return (if (StringUtils.isNumeric(trimmed)) "#" else "") + trimmed
    }

    companion object {
        fun parse(message: String): CommitMessage {
            val commitMessage = CommitMessage()
            try {
                val split = message.trim().split("\n【").map { if (it.startsWith("【")) it else "【$it" }
                var line = 0
                var arr = arrayListOf<Any>()
                for (index in split.withIndex()) {
                    if (index.index === 0) {
                        val split1 = index.value.split("：")
                        val name = ChangeType.TYPE_NAME
                        if ("【$name】" == split1[0].trim()) {
                            commitMessage.changeType = ChangeType.titleOf(split1[1].trim())!!
                        } else {
                            break
                        }
                    } else {
                        val split1 = index.value.split("：")
                        val name = commitMessage.changeType!!.lineNames()[line].lineName
                        if ("【$name】" == split1[0].trim()) {
                            arr.add(split1[1])
                            line++
                        }
                    }
                }
                commitMessage.lines = arr
            } catch (e: RuntimeException) {
            }
            return commitMessage
        }
    }
}