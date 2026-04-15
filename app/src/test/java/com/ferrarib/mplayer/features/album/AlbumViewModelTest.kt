package com.ferrarib.mplayer.features.album

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.ferrarib.mplayer.core.navigation.AppDestinations
import com.ferrarib.mplayer.data.repository.AlbumRepository
import com.ferrarib.mplayer.data.repository.RecentlyPlayedRepository
import com.ferrarib.mplayer.domain.model.Album
import com.ferrarib.mplayer.domain.model.Song
import com.ferrarib.mplayer.features.player.PlaybackController
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val album = Album(
        collectionId = 100L,
        name = "A Night at the Opera",
        artist = "Queen",
        artworkUrl = "",
        tracks = listOf(
            Song(1L, "Bohemian Rhapsody", "Queen", 100L, "A Night at the Opera", "", null, 354000L),
            Song(2L, "You're My Best Friend", "Queen", 100L, "A Night at the Opera", "", null, 172000L),
        )
    )

    private fun buildViewModel(
        collectionId: Long = 100L,
        albumRepo: AlbumRepository = FakeAlbumRepository { album },
        recentlyPlayed: RecentlyPlayedRepository = FakeRecentlyPlayedRepository(),
        playback: PlaybackController = mockk(relaxed = true),
    ): AlbumViewModel {
        val handle = SavedStateHandle(mapOf(AppDestinations.ARG_COLLECTION_ID to collectionId))
        return AlbumViewModel(handle, albumRepo, recentlyPlayed, playback)
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN successful album load WHEN uiState collected THEN emits Loading then Success`() = runTest(dispatcher) {
        buildViewModel().uiState.test {
            assertEquals(AlbumUiState.Loading, awaitItem())
            val success = awaitItem() as AlbumUiState.Success
            assertEquals(album, success.album)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN repository throws WHEN uiState collected THEN emits Loading then Error`() = runTest(dispatcher) {
        val repo = FakeAlbumRepository { throw RuntimeException("network error") }

        buildViewModel(albumRepo = repo).uiState.test {
            assertEquals(AlbumUiState.Loading, awaitItem())
            val error = awaitItem() as AlbumUiState.Error
            assertTrue(error.message.isNotBlank())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN error state WHEN retry called THEN reloads and emits Success`() = runTest(dispatcher) {
        var callCount = 0
        val repo = FakeAlbumRepository {
            if (callCount++ == 0) throw RuntimeException("network error") else album
        }

        val vm = buildViewModel(albumRepo = repo)
        vm.uiState.test {
            assertEquals(AlbumUiState.Loading, awaitItem())
            assertTrue(awaitItem() is AlbumUiState.Error)

            vm.retry()

            assertEquals(AlbumUiState.Loading, awaitItem())
            val success = awaitItem() as AlbumUiState.Success
            assertEquals(album, success.album)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN album loaded WHEN onTrackClick called THEN adds to recently played and starts playback at correct index`() =
        runTest(dispatcher) {
            val recentlyPlayed = FakeRecentlyPlayedRepository()
            val playback: PlaybackController = mockk(relaxed = true)
            val vm = buildViewModel(recentlyPlayed = recentlyPlayed, playback = playback)

            dispatcher.scheduler.advanceUntilIdle()

            val track = album.tracks[1]
            vm.onTrackClick(track)

            assertEquals(listOf(track), recentlyPlayed.added)
            verify { playback.playQueue(album.tracks, 1) }
        }

    @Test
    fun `GIVEN error state WHEN onTrackClick called THEN does not start playback`() = runTest(dispatcher) {
        val playback: PlaybackController = mockk(relaxed = true)
        val repo = FakeAlbumRepository { throw RuntimeException("error") }
        val vm = buildViewModel(albumRepo = repo, playback = playback)

        dispatcher.scheduler.advanceUntilIdle()

        vm.onTrackClick(album.tracks[0])

        verify(exactly = 0) { playback.playQueue(any(), any()) }
    }
}

private class FakeAlbumRepository(private val block: suspend () -> Album) : AlbumRepository {
    override suspend fun getAlbum(collectionId: Long): Album = block()
}

private class FakeRecentlyPlayedRepository : RecentlyPlayedRepository {
    val added = mutableListOf<Song>()
    override fun observe(): Flow<List<Song>> = MutableStateFlow(emptyList())
    override fun add(song: Song) { added.add(song) }
    override val current: List<Song> get() = added
}
