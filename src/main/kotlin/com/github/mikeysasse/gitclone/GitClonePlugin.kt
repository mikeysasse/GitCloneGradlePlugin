package com.github.mikeysasse.gitclone

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.create
import java.io.File

class GitClonePlugin : Plugin<Settings> {

    override fun apply(target: Settings) {
        val extension = target.extensions.create<GitCloneConfiguration>("git-download")
        target.gradle.beforeProject {
            val remotes = extension.remotes.map { it.resolve() }.toList()
                val credentials = loadCredentials(File("${target.rootProject.projectDir}/secret.yaml"))

                val duplicates = remotes.groupingBy { it.name }.eachCount().filter { it.value > 1 }
                check (duplicates.isEmpty()) {
                    val names = duplicates.map { it.key }.toList()
                    "Repositories cannot have the same name: $names"
                }

                remotes.forEach {
                    val download = GitClone(
                        project = project,
                        remote = it,
                        configuration = extension,
                        credentials = credentials
                    )
                    download.clone()
                }
        }
    }

    private fun loadCredentials(file: File): GitCloneCredentials {
        check (file.exists()) {
            "You need to create secret.yaml file that contains the password to your git account. Use secret.example.yaml as a template."
        }

        return ObjectMapper(YAMLFactory()).readValue(file, GitCloneCredentials::class.java)
    }
}