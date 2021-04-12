package io.github.mikeysasse.gitclone

import java.lang.IllegalStateException

/**
 * Contains information about a remote git repository.
 */
data class GitRemote(
    /**
     * The url of the remote repository.
     */
    val url: String,
    /**
     * The name the local repository will be given
     * on the disk. If null the name will be extracted
     * from the remote repository url.
     */
    val name: String? = null,
    /**
     * A path to contain the downloaded repository.
     * If not set it will default to [GitDownloadConfiguration][directory]
     * or to projects/libs/ if that isn't set.
     */
    val path: String? = null,

    /**
     * The commit at which to clone the remote repository.
     * If [commit] and [branch] are not set it defaults to the 'master' branch.
     */
    val commit: String? = null,

    /**
     * The branch at which to clone the remote repository.
     * If [branch] and [commit] are not set it defaults to the 'master' branch.
     */
    val branch: String? = null
) {

    /**
     * Resolves missing values.
     * Missing [name] is extracted from [url].
     * Missing [branch] and [commit] sets to 'master' [branch].
     */
    fun resolve(): GitRemote {
        var name = this.name
        var branch = this.branch

        if (commit == null && branch == null) {
            branch = "master"
        }

        if (name == null) {
            val regex = "\\/([a-zA-Z0-9-_]+)(?:.git)?\$".toRegex()
            val matches = regex.find(url)
            if (matches == null || matches.groupValues.size < 2) {
                throw IllegalStateException("No repository name could be extracted from $url")
            }
            name = matches.groupValues[1]
        }

        return GitRemote(
            url = url,
            name = name,
            path = path,
            commit = commit,
            branch = branch
        )
    }

    class Builder {
        var url: String? = null
        var name: String? = null
        var path: String? = null
        var commit: String? = null
        var branch: String? = null

        fun create(): GitRemote {
            return GitRemote(
                url = url ?: throw IllegalStateException("URL cannot be empty."),
                name = name,
                path = path,
                commit = commit,
                branch = branch
            )
        }
    }
}