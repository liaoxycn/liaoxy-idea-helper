package com.github.liaoxiangyun.ideaplugin.coderaminder.model.gitlab

class Event {
    var project_id = ""
    var action_name = ""
    var target = ""
    var created_at = ""
    var data: Data? = null

    open class Data {
        var event_name = ""
        var ref = ""
        var user_id = ""
        var user_email = ""
        var commits: List<Commit> = arrayListOf()
        var project: Project? = null

        open class Project {
            var name = ""
            var description = ""
            var path_with_namespace = ""
        }

        open class Commit {
            var id = ""
            var project_id = ""
            var path_with_namespace = ""
            var message = ""
            var timestamp = ""
            var author: Author = Author()
            var added: List<String> = arrayListOf()
            var modified: List<String> = arrayListOf()
            var removed: List<String> = arrayListOf()
        }
    }
}