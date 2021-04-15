package io.github.mikeysasse.gitclone

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.eclipse.jgit.api.errors.TransportException
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
        val dir = File("${projectDir}${configuration.path(remote)}")
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

        try {
            val cloned = git.setURI(remote.url)
                .setDirectory(dir)
                .call()

            remote.commit?.let {
                cloned.checkout().setName(it)
                println("$dir is in a detached head.")
            }
            println("${remote.url} cloned to ${dir.relativeTo(projectDir)}.")
        } catch (e: InvalidRemoteException) {
            notAccessible()
        } catch (e: TransportException) {
            if (e.message != null && e.message!!.contains("Authentication is required")) {
                notAccessible()
                return
            }
            e.printStackTrace()
        }
    }

    private fun notAccessible() {
        System.err.println("Not accessible or repository doesn't exist: ${remote.url}")
    }
}