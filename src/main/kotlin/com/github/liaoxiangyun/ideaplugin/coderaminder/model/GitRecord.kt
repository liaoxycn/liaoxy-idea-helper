package com.github.liaoxiangyun.ideaplugin.coderaminder.model

import java.util.*

// <div class="event-block event-item" >
class GitRecord {
    var datetime: Date? = null
    var authorName: String? = null
    var projectName: String? = null
    var branch: String? = null

    constructor()
    constructor(datetime: Date?, authorName: String?, projectName: String?, branch: String?) {
        this.datetime = datetime
        this.authorName = authorName
        this.projectName = projectName
        this.branch = branch
    }

}