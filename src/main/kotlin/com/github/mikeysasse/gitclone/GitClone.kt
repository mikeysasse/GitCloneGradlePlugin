package com.github.mikeysasse.gitclone

import org.eclipse.jgit.api.Git
import org.gradle.api.Project
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

class GitClone(
    val projectDir: File,
    val remote: GitRemote,
    val configuration: GitCloneConfiguration,
    val credentials: GitCloneCredentials?
) {

    private lateinit var git: Git

    fun clone() {
        val dir = File("${project.projectDir}${configuration.path(remote)}")
        if (!dir.exists()) {
            cloneRepository(dir)
            return
        }

        git = Git.open(dir)

        if (!isUpdateRequired()) {
            println("$dir: UP-TO-DATE")
            return
        }

        val status = git.status().call()
        if (!status.isClean) {
            error("Repository needs an update but it isn't clean: $dir")
        }

        // TODO instead of deleting lets instead pull?
        if (!dir.deleteRecursively()) {
            println("Unable to delete directory $dir")
            return
        }

        cloneRepository(dir)
    }

    private fun isUpdateRequired(): Boolean {
        val local = git.local()

        if (remote.branch != null) {
            when {
                local.branch != remote.branch -> return true
                local.commit != git.getRemoteHead(local.branch) -> return true
            }
        }

        if (remote.commit != null && local.commit != remote.commit) {
            return true
        }

        return false
    }

    private fun cloneRepository(dir: File) {
        val git = Git.cloneRepository()

        credentials?.let {
            git.setCredentialsProvider(UsernamePasswordCredentialsProvider(credentials.username, credentials.password))
        }

        remote.branch?.let { git.setBranch(it) }

        val cloned = git.setURI(remote.url)
            .setDirectory(dir)
            .call()

        remote.commit?.let {
            cloned.checkout().setName(it)
            println("$dir is in a detached head.")
        }
        println("${remote.url} cloned to $dir.")
    }
}