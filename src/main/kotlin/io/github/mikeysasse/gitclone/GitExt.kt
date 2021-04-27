package io.github.mikeysasse.gitclone

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.lang.IllegalArgumentException

fun Git.local(): GitBranchMetadata {
    return GitBranchMetadata(getLocalBranch(), getLocalHead())
}

fun Git.getLocalBranch(): String {
    return repository.branch
}

fun Git.getLocalHead(): String {
    return repository.resolve(Constants.HEAD).name
}

fun Git.getRemoteHead(branch: String, credentials: GitCloneCredentials?): String {
    return getRemoteHeads(credentials).singleOrNull { it.branch == branch }?.commit
        ?: throw IllegalArgumentException("No remote branch '$branch' found.")
}

fun Git.getRemoteHeads(credentials: GitCloneCredentials?): List<GitBranchMetadata> {
    val ls = lsRemote().setHeads(true)
    credentials?.let {
        ls.setCredentialsProvider(UsernamePasswordCredentialsProvider(credentials.username, credentials.password))
    }
    return ls.call().map {
        val objectId = it.objectId.toString()
        val branch = it.name.substring(it.name.lastIndexOf("/") + 1)
        val commit = objectId.substring(objectId.indexOf("[") + 1, objectId.lastIndexOf("]"))
        GitBranchMetadata(branch, commit)
    }
}