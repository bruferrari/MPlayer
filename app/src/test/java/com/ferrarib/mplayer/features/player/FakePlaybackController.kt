package com.ferrarib.mplayer.features.player

import com.ferrarib.mplayer.domain.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakePlaybackController : PlaybackController {

    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state

    var lastPlayedSong: Song? = null
    var lastQueue: List<Song>? = null
    var lastQueueStartIndex: Int? = null
    var lastQueueIndex: Int? = null
    var toggleCalled = false
    var prevCalled = false
    var nextCalled = false
    var repeatToggled = false
    var lastSeekTo: Long? = null
    var tickCalled = false

    fun setState(state: PlaybackState) {
        _state.value = state
    }

    override fun playSong(song: Song) {
        lastPlayedSong = song
        _state.value = _state.value.copy(currentTrackId = song.trackId, currentSong = song)
    }

    override fun playQueue(songs: List<Song>, startIndex: Int) {
        lastQueue = songs
        lastQueueStartIndex = startIndex
    }

    override fun playQueueIndex(index: Int) {
        lastQueueIndex = index
    }

    override fun next() { nextCalled = true }
    override fun prev() { prevCalled = true }
    override fun toggle() { toggleCalled = true }
    override fun toggleRepeat() { repeatToggled = true }
    override fun seekTo(positionMs: Long) { lastSeekTo = positionMs }
    override fun tickPosition() { tickCalled = true }
}
