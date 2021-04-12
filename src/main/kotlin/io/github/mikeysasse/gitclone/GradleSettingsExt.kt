package io.github.mikeysasse.gitclone

import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.configure

fun Settings.gitclone(unit: GitCloneConfiguration.() -> Unit) {
    configure<GitCloneConfiguration> {
        unit(this)
    }
}