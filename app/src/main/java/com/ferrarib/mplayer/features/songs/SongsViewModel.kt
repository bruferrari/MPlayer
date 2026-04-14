package com.ferrarib.mplayer.features.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ferrarib.mplayer.data.repository.SongRepository
import com.ferrarib.mplayer.domain.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val pagingData: Flow<PagingData<Song>> = _query
        .debounce(DEBOUNCE_MS)
        .flatMapLatest { repository.search(it) }
        .cachedIn(viewModelScope)

    fun onQueryChange(query: String) {
        _query.value = query
    }

    companion object {
        private const val DEBOUNCE_MS = 300L
    }
}
