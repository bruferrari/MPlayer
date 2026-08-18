package com.ferrarib.mplayer.features.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ferrarib.mplayer.data.repository.RecentlyPlayedRepository
import com.ferrarib.mplayer.data.repository.SongRepository
import com.ferrarib.mplayer.domain.model.Song
import com.ferrarib.mplayer.features.player.PlaybackController
import com.ferrarib.mplayer.features.player.PlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val repository: SongRepository,
    private val recentlyPlayedRepository: RecentlyPlayedRepository,
    private val playbackController: PlaybackController,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val pagingData: Flow<PagingData<Song>> = _query
        .debounce(DEBOUNCE_MS)
        .flatMapLatest { repository.search(it) }
        .cachedIn(viewModelScope)

    val recentlyPlayed: StateFlow<List<Song>> = recentlyPlayedRepository.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val playback: StateFlow<PlaybackState> = playbackController.state

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun onSongTapped(song: Song) {
        recentlyPlayedRepository.add(song)
        val queue = recentlyPlayedRepository.current
        playbackController.playQueue(queue, 0)
    }

    fun onMiniPlayPause() = playbackController.toggle()
    fun onMiniNext() = playbackController.next()
    fun onMiniPrev() = playbackController.prev()
    fun tickPosition() = playbackController.tickPosition()

    companion object {
        private const val DEBOUNCE_MS = 300L
    }
}
