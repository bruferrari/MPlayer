package com.ferrarib.mplayer.features.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ferrarib.mplayer.domain.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackControllerImpl @Inject constructor(
    private val player: ExoPlayer,
) : PlaybackController {

    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state

    private var queue: List<Song> = emptyList()
    private var currentIndex: Int = -1

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.update { it.copy(isPlaying = isPlaying) }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> _state.update { it.copy(durationMs = player.duration.coerceAtLeast(0L)) }
                Player.STATE_ENDED -> if (_state.value.isRepeatOne) {
                    player.seekTo(0)
                    player.play()
                }
            }
        }
    }

    init {
        player.addListener(listener)
    }

    override fun playQueue(songs: List<Song>, startIndex: Int) {
        if (songs.isEmpty() || startIndex !in songs.indices) return
        queue = songs
        currentIndex = startIndex
        _state.update { it.copy(queue = songs) }
        playSong(songs[startIndex])
    }

    override fun playQueueIndex(index: Int) {
        if (index !in queue.indices) return
        currentIndex = index
        playSong(queue[index])
    }

    override fun next() {
        if (currentIndex < queue.lastIndex) {
            currentIndex++
            playSong(queue[currentIndex])
        }
    }

    override fun prev() {
        if (currentIndex > 0) {
            currentIndex--
            playSong(queue[currentIndex])
        }
    }

    override fun playSong(song: Song) {
        val url = song.previewUrl ?: return
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.playWhenReady = true
        _state.update { it.copy(currentTrackId = song.trackId, currentSong = song, positionMs = 0L, durationMs = 0L) }
    }

    override fun toggle() {
        if (player.isPlaying) player.pause() else player.play()
    }

    override fun tickPosition() {
        _state.update { it.copy(positionMs = player.currentPosition.coerceAtLeast(0L)) }
    }

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        _state.update { it.copy(positionMs = positionMs) }
    }

    override fun toggleRepeat() {
        _state.update { it.copy(isRepeatOne = !it.isRepeatOne) }
    }

    fun release() {
        player.removeListener(listener)
        player.release()
    }
}
