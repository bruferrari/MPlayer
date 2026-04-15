package com.ferrarib.mplayer.data.cache

import com.ferrarib.mplayer.domain.model.Song
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentlyPlayedDataSource @Inject constructor(
    @RecentlyPlayedFile private val file: File,
    private val json: Json,
) {
    fun read(): List<Song> = try {
        if (!file.exists()) emptyList()
        else json.decodeFromString<List<Song>>(file.readText())
    } catch (_: Exception) {
        emptyList()
    }

    fun write(songs: List<Song>) {
        try {
            file.writeText(json.encodeToString(songs))
        } catch (_: Exception) { /* non-fatal */ }
    }
}
