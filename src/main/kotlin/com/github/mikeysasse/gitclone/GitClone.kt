package com.github.mikeysasse.gitclone

import org.eclipse.jgit.api.Git
import org.gradle.api.Project
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

class GitClone(
    val project: Project,
    val remote: GitRemote,
    val configuration: GitCloneConfiguration,
    val credentials: GitCloneCredentials
) {

    fun clone() {
        val dir = File("${project.projectDir}${configuration.path(remote)}")
        checkWorkingDirectory(dir)
        cloneRepository(dir)
        println("${remote.url} cloned to $dir.")
    }

    private fun checkWorkingDirectory(dir: File) {
        if (dir.exists()) {
            val git = Git.open(dir)
            val status = git.status().call()
            if (!status.isClean) {
                println("Local repository $dir is not clean and cannot be replaced.")
                return
            } else {
                if (!dir.deleteRecursively()) {
                    println("Unable to delete directory $dir")
                    return
                }
            }
        }
    }

    private fun cloneRepository(dir: File) {
        Git.cloneRepository()
            .setCredentialsProvider(UsernamePasswordCredentialsProvider(credentials.username, credentials.password))
            .setURI(remote.url)
            .setDirectory(dir)
            .call()
    }
}