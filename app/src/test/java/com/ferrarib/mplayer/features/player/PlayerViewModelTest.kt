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
    fun `GIVEN idle playback WHEN playback state changes THEN uiState reflects new state`() = runTest(dispatcher) {
        val vm = buildViewModel()

        vm.uiState.test {
            assertEquals(null, awaitItem().currentSong)

            playback.setState(PlaybackState(currentSong = song1, currentTrackId = song1.trackId))
            assertEquals(song1, awaitItem().currentSong)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN song in recently played WHEN startInitialPlaybackIfNeeded called THEN plays the song`() = runTest(dispatcher) {
        buildViewModel(trackId = song1.trackId).startInitialPlaybackIfNeeded()
        assertEquals(song1, playback.lastPlayedSong)
    }

    @Test
    fun `GIVEN song already playing WHEN startInitialPlaybackIfNeeded called THEN does not restart playback`() = runTest(dispatcher) {
        playback.setState(PlaybackState(currentTrackId = song1.trackId))
        buildViewModel(trackId = song1.trackId).startInitialPlaybackIfNeeded()
        assertNull(playback.lastPlayedSong)
    }

    @Test
    fun `GIVEN any state WHEN tickPosition called THEN delegates to playback`() = runTest(dispatcher) {
        buildViewModel().tickPosition()
        assertTrue(playback.tickCalled)
    }

    @Test
    fun `GIVEN any state WHEN onPrevClick called THEN delegates to playback`() = runTest(dispatcher) {
        buildViewModel().onPrevClick()
        assertTrue(playback.prevCalled)
    }

    @Test
    fun `GIVEN any state WHEN onNextClick called THEN delegates to playback`() = runTest(dispatcher) {
        buildViewModel().onNextClick()
        assertTrue(playback.nextCalled)
    }

    @Test
    fun `GIVEN any state WHEN onPlayPauseClick called THEN toggles playback`() = runTest(dispatcher) {
        buildViewModel().onPlayPauseClick()
        assertTrue(playback.toggleCalled)
    }

    @Test
    fun `GIVEN any state WHEN onRepeatClick called THEN toggles repeat`() = runTest(dispatcher) {
        buildViewModel().onRepeatClick()
        assertTrue(playback.repeatToggled)
    }

    @Test
    fun `GIVEN queue with 3 songs WHEN onRowClick with song2 THEN plays at index 1`() = runTest(dispatcher) {
        playback.setState(PlaybackState(queue = listOf(song1, song2, song3)))
        buildViewModel().onRowClick(song2)
        assertEquals(1, playback.lastQueueIndex)
    }

    @Test
    fun `GIVEN song not in queue WHEN onRowClick called THEN does not play any index`() = runTest(dispatcher) {
        playback.setState(PlaybackState(queue = listOf(song1)))
        buildViewModel().onRowClick(song3)
        assertNull(playback.lastQueueIndex)
    }

    @Test
    fun `GIVEN duration 200000 WHEN onSeek with 0_25 THEN seeks to 50000`() = runTest(dispatcher) {
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
