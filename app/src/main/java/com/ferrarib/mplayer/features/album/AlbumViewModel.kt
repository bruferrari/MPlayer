package com.ferrarib.mplayer.features.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferrarib.mplayer.core.navigation.AppDestinations
import com.ferrarib.mplayer.data.repository.AlbumRepository
import com.ferrarib.mplayer.data.repository.RecentlyPlayedRepository
import com.ferrarib.mplayer.domain.model.Album
import com.ferrarib.mplayer.domain.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AlbumUiState {
    data object Loading : AlbumUiState
    data class Success(val album: Album) : AlbumUiState
    data class Error(val message: String) : AlbumUiState
}

@HiltViewModel
class AlbumViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val albumRepository: AlbumRepository,
    private val recentlyPlayed: RecentlyPlayedRepository,
) : ViewModel() {

    private val collectionId: Long = checkNotNull(savedStateHandle[AppDestinations.ARG_COLLECTION_ID])

    private val _uiState = MutableStateFlow<AlbumUiState>(AlbumUiState.Loading)
    val uiState: StateFlow<AlbumUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    fun onTrackClick(song: Song) {
        recentlyPlayed.add(song)
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = AlbumUiState.Loading
            _uiState.value = try {
                AlbumUiState.Success(albumRepository.getAlbum(collectionId))
            } catch (e: Exception) {
                AlbumUiState.Error(e.localizedMessage ?: "Something went wrong")
            }
        }
    }
}
