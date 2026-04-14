package com.ferrarib.mplayer.data.cache

import com.ferrarib.mplayer.domain.model.Song
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class RecentlyPlayedDataSourceTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var dataSource: RecentlyPlayedDataSource

    @Before
    fun setUp() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        dataSource = RecentlyPlayedDataSource(File(tempFolder.root, "recently_played.json"), moshi)
    }

    private fun song(id: Long) = Song(
        trackId = id, trackName = "Track $id", artistName = "Artist",
        collectionId = 1L, collectionName = "Album",
        artworkUrl = "https://example.com/art.jpg",
        previewUrl = null, trackTimeMillis = null,
    )

    @Test
    fun `returns empty list when file does not exist`() {
        assertEquals(emptyList<Song>(), dataSource.read())
    }

    @Test
    fun `writes and reads back correctly`() {
        val songs = (1L..5L).map { song(it) }
        dataSource.write(songs)
        assertEquals(songs, dataSource.read())
    }

    @Test
    fun `overwrites on second write`() {
        dataSource.write((1L..5L).map { song(it) })
        val second = listOf(song(99L))
        dataSource.write(second)
        assertEquals(second, dataSource.read())
    }
}
