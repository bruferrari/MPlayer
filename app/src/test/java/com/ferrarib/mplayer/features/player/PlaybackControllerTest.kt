package com.ferrarib.mplayer.features.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ferrarib.mplayer.domain.model.Song
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.Runs
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PlaybackControllerTest {

    private val player: ExoPlayer = mockk(relaxed = true)
    private val listenerSlot = slot<Player.Listener>()

    @Before
    fun setUp() {
        mockkStatic(MediaItem::class)
        every { MediaItem.fromUri(any<String>()) } returns mockk()
        every { player.addListener(capture(listenerSlot)) } just Runs
    }

    @After
    fun tearDown() {
        unmockkStatic(MediaItem::class)
    }

    private fun createController() = PlaybackControllerImpl(player)

    private fun song(
        id: Long = 1L,
        previewUrl: String? = "https://example.com/preview.m4a",
    ) = Song(id, "Track $id", "Artist", 100L, "Album", "", previewUrl, 180000L)

    // --- Listener-driven state changes ---

    @Test
    fun `GIVEN idle player WHEN onIsPlayingChanged true THEN isPlaying is true`() {
        val controller = createController()
        listenerSlot.captured.onIsPlayingChanged(true)
        assertTrue(controller.state.value.isPlaying)
    }

    @Test
    fun `GIVEN playing player WHEN onIsPlayingChanged false THEN isPlaying is false`() {
        val controller = createController()
        listenerSlot.captured.onIsPlayingChanged(true)
        listenerSlot.captured.onIsPlayingChanged(false)
        assertFalse(controller.state.value.isPlaying)
    }

    @Test
    fun `GIVEN player duration 240000 WHEN STATE_READY fires THEN durationMs is 240000`() {
        every { player.duration } returns 240000L
        val controller = createController()
        listenerSlot.captured.onPlaybackStateChanged(Player.STATE_READY)
        assertEquals(240000L, controller.state.value.durationMs)
    }

    @Test
    fun `GIVEN repeatOne enabled WHEN STATE_ENDED fires THEN seeks to 0 and resumes`() {
        val controller = createController()
        controller.toggleRepeat()
        listenerSlot.captured.onPlaybackStateChanged(Player.STATE_ENDED)
        verify { player.seekTo(0) }
        verify { player.play() }
    }

    @Test
    fun `GIVEN repeatOne disabled WHEN STATE_ENDED fires THEN does not call play`() {
        val controller = createController()
        listenerSlot.captured.onPlaybackStateChanged(Player.STATE_ENDED)
        verify(exactly = 0) { player.play() }
    }

    // --- playSong ---

    @Test
    fun `GIVEN valid song WHEN playSong called THEN updates currentTrackId and currentSong`() {
        val song = song(id = 42L)
        val controller = createController()
        controller.playSong(song)
        assertEquals(42L, controller.state.value.currentTrackId)
        assertEquals(song, controller.state.value.currentSong)
    }

    @Test
    fun `GIVEN any song WHEN playSong called THEN resets positionMs to 0`() {
        val controller = createController()
        controller.playSong(song())
        assertEquals(0L, controller.state.value.positionMs)
    }

    @Test
    fun `GIVEN song with null previewUrl WHEN playSong called THEN does not update state`() {
        val controller = createController()
        controller.playSong(song(previewUrl = null))
        assertNull(controller.state.value.currentTrackId)
    }

    // --- playQueue ---

    @Test
    fun `GIVEN valid songs list and index 1 WHEN playQueue called THEN sets queue and starts playback at that index`() {
        val songs = listOf(song(1L), song(2L), song(3L))
        val controller = createController()
        controller.playQueue(songs, 1)
        assertEquals(songs, controller.state.value.queue)
        assertEquals(2L, controller.state.value.currentTrackId)
    }

    @Test
    fun `GIVEN empty songs list WHEN playQueue called THEN does nothing`() {
        val controller = createController()
        controller.playQueue(emptyList(), 0)
        assertNull(controller.state.value.currentTrackId)
    }

    @Test
    fun `GIVEN out-of-range index WHEN playQueue called THEN does nothing`() {
        val controller = createController()
        controller.playQueue(listOf(song()), 5)
        assertNull(controller.state.value.currentTrackId)
    }

    // --- toggle ---

    @Test
    fun `GIVEN player is playing WHEN toggle called THEN calls pause`() {
        every { player.isPlaying } returns true
        val controller = createController()
        controller.toggle()
        verify { player.pause() }
    }

    @Test
    fun `GIVEN player is not playing WHEN toggle called THEN calls play`() {
        every { player.isPlaying } returns false
        val controller = createController()
        controller.toggle()
        verify { player.play() }
    }

    // --- tickPosition / seekTo ---

    @Test
    fun `GIVEN player at position 45000 WHEN tickPosition called THEN updates positionMs`() {
        every { player.currentPosition } returns 45000L
        val controller = createController()
        controller.tickPosition()
        assertEquals(45000L, controller.state.value.positionMs)
    }

    @Test
    fun `GIVEN position 30000 WHEN seekTo called THEN updates positionMs and delegates to player`() {
        val controller = createController()
        controller.seekTo(30000L)
        assertEquals(30000L, controller.state.value.positionMs)
        verify { player.seekTo(30000L) }
    }

    // --- toggleRepeat ---

    @Test
    fun `GIVEN isRepeatOne is false WHEN toggleRepeat called THEN isRepeatOne flips`() {
        val controller = createController()
        assertFalse(controller.state.value.isRepeatOne)
        controller.toggleRepeat()
        assertTrue(controller.state.value.isRepeatOne)
        controller.toggleRepeat()
        assertFalse(controller.state.value.isRepeatOne)
    }
}
