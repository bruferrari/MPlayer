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

data class PlaybackState(
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val currentTrackId: Long? = null,
)

@Singleton
class PlaybackController @Inject constructor(
    private val player: ExoPlayer,
) {
    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.update { it.copy(isPlaying = isPlaying) }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                _state.update { it.copy(durationMs = player.duration.coerceAtLeast(0L)) }
            }
        }
    }

    init {
        player.addListener(listener)
    }

    fun playSong(song: Song) {
        val url = song.previewUrl ?: return
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.playWhenReady = true
        _state.update { it.copy(currentTrackId = song.trackId, positionMs = 0L, durationMs = 0L) }
    }

    fun toggle() {
        if (player.isPlaying) player.pause() else player.play()
    }

    fun tickPosition() {
        _state.update { it.copy(positionMs = player.currentPosition.coerceAtLeast(0L)) }
    }

    fun release() {
        player.removeListener(listener)
        player.release()
    }
}
