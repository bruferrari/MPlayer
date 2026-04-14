package com.ferrarib.mplayer.data.cache

import com.ferrarib.mplayer.domain.model.Song
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentlyPlayedDataSource @Inject constructor(
    @RecentlyPlayedFile private val file: File,
    private val moshi: Moshi,
) {
    private val adapter by lazy {
        val type = Types.newParameterizedType(List::class.java, Song::class.java)
        moshi.adapter<List<Song>>(type)
    }

    fun read(): List<Song> = try {
        if (!file.exists()) emptyList()
        else file.source().buffer().use { adapter.fromJson(it) } ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }

    fun write(songs: List<Song>) {
        try {
            file.sink().buffer().use { adapter.toJson(it, songs) }
        } catch (_: Exception) { /* non-fatal */ }
    }

}
