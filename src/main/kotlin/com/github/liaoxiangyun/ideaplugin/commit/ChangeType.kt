package com.github.liaoxiangyun.ideaplugin.commit

import com.github.liaoxiangyun.ideaplugin.commit.model.CommitLine
import com.github.liaoxiangyun.ideaplugin.commit.settings.AppSettingsState

/**
 * From https://github.com/commitizen/conventional-commit-types
 *
 * @author Damien Arrachequesne
 */
enum class ChangeType(private val title: String, private val description: String, private val commitLines: List<CommitLine>) {
    /**
     * 开发新功能
     */
    TYPE1("开发新功能", "开发新功能", arrayListOf(
            CommitLine("任务描述", true, null),
            CommitLine("任务ID", true, null),
            CommitLine("需求ID", true, null),
            CommitLine("修改描述", true, null),
            CommitLine("影响范围", true, null),
            CommitLine("责任人", false, null, AppSettingsState.getConfig()?.responsiblePerson),
            CommitLine("检视人", false, null, AppSettingsState.getConfig()?.inspector))),

    /**
     * 修改Bug
     */
    TYPE2("修改Bug", "修改Bug", arrayListOf(
            CommitLine("Bug描述", true, null),
            CommitLine("BugID", true, null),
            CommitLine("问题原因", true, null),
            CommitLine("修改描述", true, null),
            CommitLine("影响范围", true, null),
            CommitLine("责任人", false, null, AppSettingsState.getConfig()?.responsiblePerson),
            CommitLine("检视人", false, null, AppSettingsState.getConfig()?.inspector))),

    /**
     * 代码重构
     */
    TYPE3("代码重构", "代码重构", arrayListOf(
            CommitLine("任务ID", true, null),
            CommitLine("重构对象", true, null),
            CommitLine("重构原因", true, null),
            CommitLine("优化描述", true, null),
            CommitLine("影响范围", true, null),
            CommitLine("责任人", false, null, AppSettingsState.getConfig()?.responsiblePerson),
            CommitLine("检视人", false, null, AppSettingsState.getConfig()?.inspector))),

    /**
     * 业务重构
     */
    TYPE4("业务重构", "业务重构", arrayListOf(
            CommitLine("任务ID", true, null),
            CommitLine("重构对象", true, null),
            CommitLine("重构原因", true, null),
            CommitLine("优化描述", true, null),
            CommitLine("影响范围", true, null),
            CommitLine("责任人", false, null, AppSettingsState.getConfig()?.responsiblePerson),
            CommitLine("检视人", false, null, AppSettingsState.getConfig()?.inspector))),

    /**
     * 性能优化
     */
    TYPE5("性能优化", "性能优化", arrayListOf(
            CommitLine("任务ID", true, null),
            CommitLine("优化对象", true, null),
            CommitLine("优化原因", true, null),
            CommitLine("优化描述", true, null),
            CommitLine("影响范围", true, null),
            CommitLine("责任人", false, null, AppSettingsState.getConfig()?.responsiblePerson),
            CommitLine("检视人", false, null, AppSettingsState.getConfig()?.inspector))),

    /**
     * 代码格式化
     */
    TYPE6("代码格式化", "代码格式化", arrayListOf(
            CommitLine("任务ID", true, null, "aaaaa", "aaaaa"),
            CommitLine("备注", true, null))),

    /**
     * 代码迁移
     */
    TYPE7("代码迁移", "代码迁移", arrayListOf(
            CommitLine("任务ID", true, null, "bbbbb", "bbbbb"),
            CommitLine("备注", true, null, "CI统计时默认排除。"))),

    /**
     * 第三方开源代码引入
     */
    TYPE9("第三方开源代码引入", "第三方开源代码引入", arrayListOf(
            CommitLine("任务ID", true, null, "ccccc", "ccccc"),
            CommitLine("备注", true, null, "CI统计时默认排除。"))),

    /**
     * 第三方开源代码引入
     */
    TYPE10("resource目录下的脚本和资源文件初始化或修改", "resource目录下的脚本和资源文件初始化或修改", arrayListOf(
            CommitLine("任务ID", true, null, "ddddd", "ddddd"),
            CommitLine("备注", true, null, "CI统计时默认排除。"))),

    /**
     * 其它
     */
    TYPE8("其它", "其它", arrayListOf(
            CommitLine("修改描述", true, null),
            CommitLine("影响范围", true, null))),

    ;

    fun label(): String {
        return name.toLowerCase()
    }

    fun title(): String {
        return title
    }

    fun description(): String {
        return description
    }


    fun commitLines(): List<CommitLine> {
        return commitLines
    }


    companion object {
        open val TYPE_NAME: String = "修改类型"
        open fun titleOf(title: String): ChangeType? {
            for (value in values()) {
                if (value.title == title) {
                    return value
                }
            }
            return null
        }
    }

    override fun toString(): String {
        return String.format("%s - %s", label(), description)
    }

}