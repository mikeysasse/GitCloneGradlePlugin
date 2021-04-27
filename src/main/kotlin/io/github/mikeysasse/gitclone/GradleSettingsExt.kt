package io.github.mikeysasse.gitclone

import org.gradle.api.Project
import org.gradle.kotlin.dsl.KotlinBuildScript
import org.gradle.kotlin.dsl.configure

fun Project.gitclone(unit: GitCloneConfiguration.() -> Unit) {
    configure<GitCloneConfiguration> {
        unit(this)
    }
}

fun KotlinBuildScript.gitclone(unit: GitCloneConfiguration.() -> Unit) {
    configure<GitCloneConfiguration> {
        unit(this)
    }
}

