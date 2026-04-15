package com.ferrarib.mplayer.features.player

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.ferrarib.mplayer.core.navigation.AppDestinations
import com.ferrarib.mplayer.data.repository.RecentlyPlayedRepository
import com.ferrarib.mplayer.domain.model.Song
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val song1 = Song(1L, "Bohemian Rhapsody", "Queen", 100L, "A Night at the Opera", "", "https://example.com/1.m4a", 354000L)
    private val song2 = Song(2L, "You're My Best Friend", "Queen", 100L, "A Night at the Opera", "", "https://example.com/2.m4a", 172000L)
    private val song3 = Song(3L, "Don't Stop Me Now", "Queen", 100L, "A Night at the Opera", "", "https://example.com/3.m4a", 210000L)

    private val playback = FakePlaybackController()

    private fun buildViewModel(
        trackId: Long = song1.trackId,
        recentlyPlayed: RecentlyPlayedRepository = FakeRecentlyPlayedRepository(listOf(song1, song2, song3)),
    ): PlayerViewModel {
        val handle = SavedStateHandle(mapOf(AppDestinations.ARG_TRACK_ID to trackId))
        return PlayerViewModel(handle, playback, recentlyPlayed)
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
    fun `uiState reflects playback state changes`() = runTest(dispatcher) {
        val vm = buildViewModel()

        vm.uiState.test {
            assertEquals(null, awaitItem().currentSong)

            playback.setState(PlaybackState(currentSong = song1, currentTrackId = song1.trackId))
            assertEquals(song1, awaitItem().currentSong)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startInitialPlaybackIfNeeded plays the song from recently played`() = runTest(dispatcher) {
        buildViewModel(trackId = song1.trackId).startInitialPlaybackIfNeeded()
        assertEquals(song1, playback.lastPlayedSong)
    }

    @Test
    fun `startInitialPlaybackIfNeeded skips when song already playing`() = runTest(dispatcher) {
        playback.setState(PlaybackState(currentTrackId = song1.trackId))
        buildViewModel(trackId = song1.trackId).startInitialPlaybackIfNeeded()
        assertNull(playback.lastPlayedSong)
    }

    @Test
    fun `tickPosition delegates to playback`() = runTest(dispatcher) {
        buildViewModel().tickPosition()
        assertTrue(playback.tickCalled)
    }

    @Test
    fun `onPrevClick delegates to playback`() = runTest(dispatcher) {
        buildViewModel().onPrevClick()
        assertTrue(playback.prevCalled)
    }

    @Test
    fun `onNextClick delegates to playback`() = runTest(dispatcher) {
        buildViewModel().onNextClick()
        assertTrue(playback.nextCalled)
    }

    @Test
    fun `onPlayPauseClick toggles playback`() = runTest(dispatcher) {
        buildViewModel().onPlayPauseClick()
        assertTrue(playback.toggleCalled)
    }

    @Test
    fun `onRepeatClick toggles repeat`() = runTest(dispatcher) {
        buildViewModel().onRepeatClick()
        assertTrue(playback.repeatToggled)
    }

    @Test
    fun `onRowClick plays correct queue index`() = runTest(dispatcher) {
        playback.setState(PlaybackState(queue = listOf(song1, song2, song3)))
        buildViewModel().onRowClick(song2)
        assertEquals(1, playback.lastQueueIndex)
    }

    @Test
    fun `onRowClick does nothing when song not in queue`() = runTest(dispatcher) {
        playback.setState(PlaybackState(queue = listOf(song1)))
        buildViewModel().onRowClick(song3)
        assertNull(playback.lastQueueIndex)
    }

    @Test
    fun `onSeek seeks to fraction of duration`() = runTest(dispatcher) {
        playback.setState(PlaybackState(durationMs = 200_000L))
        buildViewModel().onSeek(0.25f)
        assertEquals(50_000L, playback.lastSeekTo)
    }
}

private class FakeRecentlyPlayedRepository(
    private val songs: List<Song> = emptyList(),
) : RecentlyPlayedRepository {
    override fun observe(): Flow<List<Song>> = MutableStateFlow(songs)
    override fun add(song: Song) {}
    override val current: List<Song> get() = songs
}
