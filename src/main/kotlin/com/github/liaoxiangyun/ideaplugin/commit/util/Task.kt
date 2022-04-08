package com.github.liaoxiangyun.ideaplugin.commit.util

class Task {

    var id = ""
    var type = ""

    var name = ""
    var desc: String? = ""

    var storyID: String? = ""
    var storyTitle: String? = ""
    var storyStatus = ""
    override fun toString(): String {
        return "Task(id='$id', type='$type', name='$name', desc=$desc, storyID=$storyID, storyTitle=$storyTitle, storyStatus='$storyStatus')"
    }


}