package com.github.senordingdong.sprykerideaplugin.services

import com.github.senordingdong.sprykerideaplugin.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
