package com.github.liaoxiangyun.ideaplugin.commit.util

class MyTodo {

    var title = ""
    var tabID = ""
    var type = ""
    var tasks = arrayListOf<Task>()
    var bugs = arrayListOf<Bug>()
    override fun toString(): String {
        return "MyTodo(title='$title', tabID='$tabID', type='$type', tasks=$tasks, bugs=$bugs)"
    }


}