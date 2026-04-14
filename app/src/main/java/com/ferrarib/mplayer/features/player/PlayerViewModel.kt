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
        val currentId = state.currentTrackId ?: trackId
        PlayerUiState(
            currentSong = songs.firstOrNull { it.trackId == currentId },
            recentlyPlayed = songs,
            playback = state,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerUiState())

    init {
        // Wait until the song is available in recently played, then start playback.
        viewModelScope.launch {
            val list = recentlyPlayed.observe().first { songs ->
                songs.any { it.trackId == trackId }
            }
            val song = list.first { it.trackId == trackId }
            playback.playSong(song)
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
        playback.playSong(song)
    }

    fun onPrevClick() {
        val songs = uiState.value.recentlyPlayed
        val currentId = uiState.value.playback.currentTrackId ?: return
        val idx = songs.indexOfFirst { it.trackId == currentId }
        songs.getOrNull(idx + 1)?.let { playback.playSong(it) }
    }

    fun onNextClick() {
        val songs = uiState.value.recentlyPlayed
        val currentId = uiState.value.playback.currentTrackId ?: return
        val idx = songs.indexOfFirst { it.trackId == currentId }
        if (idx > 0) songs.getOrNull(idx - 1)?.let { playback.playSong(it) }
    }
}
