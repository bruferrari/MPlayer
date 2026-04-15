package com.ferrarib.mplayer.features.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferrarib.mplayer.core.navigation.AppDestinations
import com.ferrarib.mplayer.data.repository.RecentlyPlayedRepository
import com.ferrarib.mplayer.domain.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val currentSong: Song? = null,
    val recentlyPlayed: List<Song> = emptyList(),
    val playback: PlaybackState = PlaybackState(),
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playback: PlaybackController,
    private val recentlyPlayed: RecentlyPlayedRepository,
) : ViewModel() {

    private val trackId: Long =
        savedStateHandle.get<Long>(AppDestinations.ARG_TRACK_ID) ?: 0L

    val uiState: StateFlow<PlayerUiState> = combine(
        recentlyPlayed.observe(),
        playback.state,
    ) { songs, state ->
        PlayerUiState(
            currentSong = state.currentSong,
            recentlyPlayed = songs,
            playback = state,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerUiState())

    init {
        // Wait until the song is available in recently played, then start playback
        // if not already started by the calling ViewModel (songs/album flow).
        viewModelScope.launch {
            val list = recentlyPlayed.observe().first { songs ->
                songs.any { it.trackId == trackId }
            }
            val song = list.first { it.trackId == trackId }
            if (playback.state.value.currentTrackId != trackId) {
                playback.playSong(song)
            }
        }

        // Poll playback position every 500 ms.
        viewModelScope.launch {
            while (true) {
                delay(500L)
                playback.tickPosition()
            }
        }
    }

    fun onPlayPauseClick() = playback.toggle()

    fun onRowClick(song: Song) {
        recentlyPlayed.add(song)
        val queue = recentlyPlayed.current
        playback.playQueue(queue, 0)
    }

    fun onPrevClick() = playback.prev()

    fun onNextClick() = playback.next()

    fun onRepeatClick() = playback.toggleRepeat()

    fun onSeek(fraction: Float) {
        val positionMs = (fraction * playback.state.value.durationMs).toLong()
        playback.seekTo(positionMs)
    }
}
