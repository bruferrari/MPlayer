package com.ferrarib.mplayer.features.player

import com.ferrarib.mplayer.domain.model.Song
import kotlinx.coroutines.flow.StateFlow

data class PlaybackState(
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val currentTrackId: Long? = null,
    val currentSong: Song? = null,
    val isRepeatOne: Boolean = false,
    val queue: List<Song> = emptyList(),
)

interface PlaybackController {
    val state: StateFlow<PlaybackState>
    fun playSong(song: Song)
    fun playQueue(songs: List<Song>, startIndex: Int)
    fun playQueueIndex(index: Int)
    fun next()
    fun prev()
    fun toggle()
    fun toggleRepeat()
    fun seekTo(positionMs: Long)
    fun tickPosition()
}
