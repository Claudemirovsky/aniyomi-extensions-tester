package eu.kanade.tachiyomi.animesource.model

import java.io.Serializable

interface SAnime : Serializable {

    var url: String

    var title: String

    var artist: String?

    var author: String?

    var description: String?

    var genre: String?

    var status: Int

    var thumbnail_url: String?

    var update_strategy: UpdateStrategy

    var is_thumbnail_loading: Boolean

    var initialized: Boolean

    fun copyFrom(other: SAnime) {
        title = other.title
        is_thumbnail_loading = other.is_thumbnail_loading

        if (other.author != null) {
            author = other.author
        }

        if (other.artist != null) {
            artist = other.artist
        }

        if (other.description != null) {
            description = other.description
        }

        if (other.genre != null) {
            genre = other.genre
        }

        if (other.thumbnail_url != null) {
            thumbnail_url = other.thumbnail_url
        }

        status = other.status

        if (!initialized) {
            initialized = other.initialized
        }
    }

    companion object {
        const val UNKNOWN = 0
        const val ONGOING = 1
        const val COMPLETED = 2
        const val LICENSED = 3
        const val PUBLISHING_FINISHED = 4
        const val CANCELLED = 5
        const val ON_HIATUS = 6

        fun create(): SAnime {
            return SAnimeImpl()
        }

        // Useful for pretty-printing
        fun getStatus(status: Int): String {
            return when (status) {
                ONGOING -> "ONGOING"
                COMPLETED -> "COMPLETED"
                LICENSED -> "LICENSED"
                PUBLISHING_FINISHED -> "PUBLISHING_FINISHED"
                CANCELLED -> "CANCELLED"
                ON_HIATUS -> "ON_HIATUS"
                else -> "UNKNOWN"
            }
        }
    }
}
