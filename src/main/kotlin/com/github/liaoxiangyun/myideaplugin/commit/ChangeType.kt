package com.github.liaoxiangyun.myideaplugin.commit

/**
 * From https://github.com/commitizen/conventional-commit-types
 *
 * @author Damien Arrachequesne
 */
enum class ChangeType(private val title: String, private val description: String, private val lineNames: List<String>) {
    /**
     * 开发新功能
     */
    TYPE1("开发新功能", "开发新功能", arrayListOf("任务描述", "任务ID", "需求ID", "修改描述", "影响范围", "责任人", "检视人")),

    /**
     * 修改Bug
     */
    TYPE2("修改Bug", "修改Bug", arrayListOf("Bug描述", "BugID", "问题原因", "修改描述", "影响范围", "责任人", "检视人")),

    /**
     * 代码重构
     */
    TYPE3("代码重构", "代码重构", arrayListOf("任务ID", "重构对象", "重构原因", "优化描述", "影响范围", "责任人", "检视人")),

    /**
     * 业务重构
     */
    TYPE4("业务重构", "业务重构", arrayListOf("任务ID", "重构对象", "重构原因", "优化描述", "影响范围", "责任人", "检视人")),

    /**
     * 性能优化
     */
    TYPE5("性能优化", "性能优化", arrayListOf("任务ID", "优化对象", "优化原因", "优化描述", "影响范围", "责任人", "检视人")),

    /**
     * 代码格式化
     */
    TYPE6("代码格式化", "代码格式化", arrayListOf("任务ID", "备注")),

    /**
     * 代码迁移
     */
    TYPE7("代码迁移", "代码迁移", arrayListOf("任务ID", "备注")),

    /**
     * 其它
     */
    TYPE8("其它", "其它", arrayListOf("修改描述", "影响范围"));

    fun label(): String {
        return name.toLowerCase()
    }

    fun title(): String {
        return title
    }

    fun description(): String {
        return description
    }

    fun lineNames(): List<String> {
        return lineNames
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