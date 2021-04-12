package io.github.mikeysasse.gitclone

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
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

fun Git.remote(): GitBranchMetadata {
    return GitBranchMetadata(
        getLocalBranch(),
        getRemoteHead(getLocalBranch())
    )
}

fun Git.getRemoteHead(branch: String): String {
    return getRemoteHeads().singleOrNull { it.branch == branch }?.commit
        ?: throw IllegalArgumentException("No remote branch '$branch' found.")
}

fun Git.getRemoteHeads(): List<GitBranchMetadata> {
    val ls = lsRemote().setHeads(true).call()
    return ls.map {
        val objectId = it.objectId.toString()
        val branch = it.name.substring(it.name.lastIndexOf("/") + 1)
        val commit = objectId.substring(objectId.indexOf("[") + 1, objectId.lastIndexOf("]"))
        GitBranchMetadata(branch, commit)
    }
}