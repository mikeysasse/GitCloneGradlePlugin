package com.github.mikeysasse.gitclone

/**
 * Defines the configuration for the [GitClonePlugin].
 */
open class GitCloneConfiguration {
    /**
     * The directory in which to download all
     * the [remotes]. Can be overridden by defining
     * [GitRemote][directory] on a specific [GitRemote].
     * Default directory is /project/libs.
     */
    var directory: String = "/libs/"

    /**
     * The [GitRemote]s.
     */
    var remotes = mutableListOf<GitRemote>()

    /**
     * Create a [GitRemote].
     */
    fun git(unit: GitRemote.Builder.() -> Unit) {
        val builder = GitRemote.Builder()
        unit(builder)
        remotes.add(builder.create())
    }

    fun path(remote: GitRemote): String {
        val name = remote.name
        return when {
            remote.path != null -> "{$remote.path}$name"
            else -> "$directory$name" // TODO make sure this starts and ends with a forward slash
        }
    }
}