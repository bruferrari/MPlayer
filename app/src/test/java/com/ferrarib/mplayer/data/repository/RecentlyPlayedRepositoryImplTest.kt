package com.ferrarib.mplayer.data.repository

import app.cash.turbine.test
import com.ferrarib.mplayer.data.cache.RecentlyPlayedDataSource
import com.ferrarib.mplayer.data.cache.RecentlyPlayedFile
import com.ferrarib.mplayer.domain.model.Song
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class RecentlyPlayedRepositoryImplTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private fun song(id: Long) = Song(
        trackId = id, trackName = "Track $id", artistName = "Artist",
        collectionId = 1L, collectionName = "Album",
        artworkUrl = "https://example.com/art.jpg",
        previewUrl = null, trackTimeMillis = null,
    )

    private fun repo(initial: List<Song> = emptyList()): RecentlyPlayedRepositoryImpl {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val file = File(tempFolder.root, "recently_played.json")
        val dataSource = RecentlyPlayedDataSource(file, moshi)
        if (initial.isNotEmpty()) dataSource.write(initial)
        return RecentlyPlayedRepositoryImpl(dataSource)
    }

    @Test
    fun `observe emits persisted list after init`() = runTest {
        val initial = listOf(song(1), song(2))
        val repository = repo(initial)
        repository.observe().test {
            val list = awaitItem()
            assertEquals(2, list.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `add moves existing song to front without duplicating`() = runTest {
        val repository = repo(listOf(song(1), song(2), song(3)))
        repository.observe().test { awaitItem(); cancelAndIgnoreRemainingEvents() }

        repository.add(song(2))

        repository.observe().test {
            val list = awaitItem()
            assertEquals(listOf(2L, 1L, 3L), list.map { it.trackId })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `add new song prepends it`() = runTest {
        val repository = repo(listOf(song(1), song(2)))
        repository.observe().test { awaitItem(); cancelAndIgnoreRemainingEvents() }

        repository.add(song(99))

        repository.observe().test {
            val list = awaitItem()
            assertEquals(99L, list.first().trackId)
            assertEquals(3, list.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `caps at 10 entries`() = runTest {
        val repository = repo((1L..10L).map { song(it) })
        repository.observe().test { awaitItem(); cancelAndIgnoreRemainingEvents() }

        repository.add(song(11))

        repository.observe().test {
            val list = awaitItem()
            assertEquals(10, list.size)
            assertEquals(11L, list.first().trackId)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
