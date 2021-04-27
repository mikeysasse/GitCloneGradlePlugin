package io.github.mikeysasse.gitclone

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import java.io.File

class GitClonePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<GitCloneConfiguration>("gitclone")
        target.task("git-clone") {
            doFirst {
                println(extension.remotes.map { it.url }.toList())
                clone(extension, target)
            }
        }
    }

    private fun clone(extension: GitCloneConfiguration, target: Project) {
        val remotes = extension.remotes.map { it.resolve() }.toList()
        val credentials = loadCredentials(File("${target.rootProject.projectDir}/secret.yaml"))

        val duplicates = remotes.groupingBy { it.name }.eachCount().filter { it.value > 1 }
        check (duplicates.isEmpty()) {
            val names = duplicates.map { it.key }.toList()
            "Repositories cannot have the same name: $names"
        }

        remotes.forEach {
            val clone = GitClone(
                    projectDir = target.rootProject.projectDir,
                    remote = it,
                    configuration = extension,
                    credentials = credentials
            )
            clone.clone()
        }
    }

    private fun loadCredentials(file: File): GitCloneCredentials? {
        if (!file.exists()) {
            println(
                "If you're going to be cloning private repositories you need to create secret.yaml " +
                "file that contains the username and password to your git account. Use secret.example.yaml as a template. " +
                "SSH is not supported yet."
            )

            return null
        }

        return ObjectMapper(YAMLFactory()).readValue(file, GitCloneCredentials::class.java)
    }
}