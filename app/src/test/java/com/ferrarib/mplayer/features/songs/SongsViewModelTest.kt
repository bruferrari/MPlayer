package com.ferrarib.mplayer.features.songs

import androidx.paging.PagingData
import app.cash.turbine.test
import com.ferrarib.mplayer.data.repository.RecentlyPlayedRepository
import com.ferrarib.mplayer.data.repository.SongRepository
import com.ferrarib.mplayer.domain.model.Song
import com.ferrarib.mplayer.features.player.FakePlaybackController
import com.ferrarib.mplayer.features.player.PlaybackState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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
class SongsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val song1 = Song(1L, "Bohemian Rhapsody", "Queen", 100L, "A Night at the Opera", "", null, 354000L)
    private val song2 = Song(2L, "Perfect", "Ed Sheeran", 200L, "Divide", "", null, 263000L)

    private lateinit var playback: FakePlaybackController

    private fun buildViewModel(
        songRepo: SongRepository = FakeSongRepository(),
        recentlyPlayed: FakeRecentlyPlayedRepository = FakeRecentlyPlayedRepository(),
    ) = SongsViewModel(songRepo, recentlyPlayed, playback)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        playback = FakePlaybackController()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN new ViewModel WHEN query accessed THEN value is empty string`() = runTest(dispatcher) {
        assertEquals("", buildViewModel().query.value)
    }

    @Test
    fun `GIVEN any state WHEN onQueryChange called THEN query updates immediately`() = runTest(dispatcher) {
        val vm = buildViewModel()
        vm.onQueryChange("queen")
        assertEquals("queen", vm.query.value)
    }

    @Test
    fun `GIVEN repository with songs WHEN recentlyPlayed collected THEN emits repository list`() = runTest(dispatcher) {
        val repo = FakeRecentlyPlayedRepository(MutableStateFlow(listOf(song1, song2)))
        buildViewModel(recentlyPlayed = repo).recentlyPlayed.test {
            // stateIn emits its initial empty value first.
            assertEquals(emptyList<Song>(), awaitItem())
            assertEquals(listOf(song1, song2), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN reactive repository WHEN repository emits new list THEN recentlyPlayed updates`() = runTest(dispatcher) {
        val flow = MutableStateFlow(listOf(song1))
        val repo = FakeRecentlyPlayedRepository(flow)
        buildViewModel(recentlyPlayed = repo).recentlyPlayed.test {
            assertEquals(emptyList<Song>(), awaitItem())
            assertEquals(listOf(song1), awaitItem())
            flow.value = listOf(song2, song1)
            assertEquals(listOf(song2, song1), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN any state WHEN onSongTapped called THEN adds song to recently played`() = runTest(dispatcher) {
        val repo = FakeRecentlyPlayedRepository()
        buildViewModel(recentlyPlayed = repo).onSongTapped(song1)
        assertEquals(listOf(song1), repo.added)
    }

    @Test
    fun `GIVEN recently played songs WHEN onSongTapped called THEN starts playback with full queue`() = runTest(dispatcher) {
        val repo = FakeRecentlyPlayedRepository(MutableStateFlow(listOf(song1, song2)))
        buildViewModel(recentlyPlayed = repo).onSongTapped(song1)
        assertEquals(listOf(song1, song2), playback.lastQueue)
        assertEquals(0, playback.lastQueueStartIndex)
    }

    @Test
    fun `GIVEN playback state WHEN ViewModel created THEN playback exposes current state immediately`() = runTest(dispatcher) {
        val expected = PlaybackState(
            isPlaying = true,
            currentSong = song1,
            currentTrackId = song1.trackId,
        )
        playback.setState(expected)

        assertEquals(expected, buildViewModel().playback.value)
    }

    @Test
    fun `GIVEN any state WHEN onMiniPlayPause called THEN toggles playback`() = runTest(dispatcher) {
        buildViewModel().onMiniPlayPause()

        assertTrue(playback.toggleCalled)
    }

    @Test
    fun `GIVEN any state WHEN onMiniNext called THEN advances playback`() = runTest(dispatcher) {
        buildViewModel().onMiniNext()

        assertTrue(playback.nextCalled)
    }

    @Test
    fun `GIVEN any state WHEN onMiniPrev called THEN rewinds playback`() = runTest(dispatcher) {
        buildViewModel().onMiniPrev()

        assertTrue(playback.prevCalled)
    }

    @Test
    fun `GIVEN any state WHEN tickPosition called THEN updates playback position`() = runTest(dispatcher) {
        buildViewModel().tickPosition()

        assertTrue(playback.tickCalled)
    }
}

private class FakeSongRepository : SongRepository {
    override fun search(query: String): Flow<PagingData<Song>> = flowOf(PagingData.empty())
}

private class FakeRecentlyPlayedRepository(
    private val flow: MutableStateFlow<List<Song>> = MutableStateFlow(emptyList()),
) : RecentlyPlayedRepository {
    val added = mutableListOf<Song>()
    override fun observe(): Flow<List<Song>> = flow
    override fun add(song: Song) {
        added.add(song)
        flow.value = listOf(song) + flow.value.filter { it.trackId != song.trackId }
    }
    override val current: List<Song> get() = flow.value
}
