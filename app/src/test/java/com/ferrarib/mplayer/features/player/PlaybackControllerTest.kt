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
    fun `isPlaying becomes true when listener fires onIsPlayingChanged(true)`() {
        val controller = createController()
        listenerSlot.captured.onIsPlayingChanged(true)
        assertTrue(controller.state.value.isPlaying)
    }

    @Test
    fun `isPlaying becomes false when listener fires onIsPlayingChanged(false)`() {
        val controller = createController()
        listenerSlot.captured.onIsPlayingChanged(true)
        listenerSlot.captured.onIsPlayingChanged(false)
        assertFalse(controller.state.value.isPlaying)
    }

    @Test
    fun `durationMs updated when STATE_READY fires`() {
        every { player.duration } returns 240000L
        val controller = createController()
        listenerSlot.captured.onPlaybackStateChanged(Player.STATE_READY)
        assertEquals(240000L, controller.state.value.durationMs)
    }

    @Test
    fun `STATE_ENDED with repeatOne seeks to 0 and resumes`() {
        val controller = createController()
        controller.toggleRepeat()
        listenerSlot.captured.onPlaybackStateChanged(Player.STATE_ENDED)
        verify { player.seekTo(0) }
        verify { player.play() }
    }

    @Test
    fun `STATE_ENDED without repeatOne does nothing`() {
        val controller = createController()
        listenerSlot.captured.onPlaybackStateChanged(Player.STATE_ENDED)
        verify(exactly = 0) { player.play() }
    }

    // --- playSong ---

    @Test
    fun `playSong updates currentTrackId and currentSong`() {
        val song = song(id = 42L)
        val controller = createController()
        controller.playSong(song)
        assertEquals(42L, controller.state.value.currentTrackId)
        assertEquals(song, controller.state.value.currentSong)
    }

    @Test
    fun `playSong resets positionMs to 0`() {
        val controller = createController()
        controller.playSong(song())
        assertEquals(0L, controller.state.value.positionMs)
    }

    @Test
    fun `playSong with null previewUrl does not update state`() {
        val controller = createController()
        controller.playSong(song(previewUrl = null))
        assertNull(controller.state.value.currentTrackId)
    }

    // --- playQueue ---

    @Test
    fun `playQueue sets queue and starts playback at given index`() {
        val songs = listOf(song(1L), song(2L), song(3L))
        val controller = createController()
        controller.playQueue(songs, 1)
        assertEquals(songs, controller.state.value.queue)
        assertEquals(2L, controller.state.value.currentTrackId)
    }

    @Test
    fun `playQueue with empty list does nothing`() {
        val controller = createController()
        controller.playQueue(emptyList(), 0)
        assertNull(controller.state.value.currentTrackId)
    }

    @Test
    fun `playQueue with out-of-range index does nothing`() {
        val controller = createController()
        controller.playQueue(listOf(song()), 5)
        assertNull(controller.state.value.currentTrackId)
    }

    // --- toggle ---

    @Test
    fun `toggle calls pause when player is playing`() {
        every { player.isPlaying } returns true
        val controller = createController()
        controller.toggle()
        verify { player.pause() }
    }

    @Test
    fun `toggle calls play when player is not playing`() {
        every { player.isPlaying } returns false
        val controller = createController()
        controller.toggle()
        verify { player.play() }
    }

    // --- tickPosition / seekTo ---

    @Test
    fun `tickPosition updates positionMs from player`() {
        every { player.currentPosition } returns 45000L
        val controller = createController()
        controller.tickPosition()
        assertEquals(45000L, controller.state.value.positionMs)
    }

    @Test
    fun `seekTo updates positionMs and delegates to player`() {
        val controller = createController()
        controller.seekTo(30000L)
        assertEquals(30000L, controller.state.value.positionMs)
        verify { player.seekTo(30000L) }
    }

    // --- toggleRepeat ---

    @Test
    fun `toggleRepeat flips isRepeatOne`() {
        val controller = createController()
        assertFalse(controller.state.value.isRepeatOne)
        controller.toggleRepeat()
        assertTrue(controller.state.value.isRepeatOne)
        controller.toggleRepeat()
        assertFalse(controller.state.value.isRepeatOne)
    }
}
