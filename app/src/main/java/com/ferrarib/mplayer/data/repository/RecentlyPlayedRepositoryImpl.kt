package com.ferrarib.mplayer.data.repository

import com.ferrarib.mplayer.core.di.ApplicationScope
import com.ferrarib.mplayer.data.cache.RecentlyPlayedDataSource
import com.ferrarib.mplayer.domain.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentlyPlayedRepositoryImpl @Inject constructor(
    private val dataSource: RecentlyPlayedDataSource,
    @ApplicationScope private val scope: CoroutineScope,
) : RecentlyPlayedRepository {

    private val state = MutableStateFlow<List<Song>?>(null)

    init {
        scope.launch { state.value = dataSource.read() }
    }

    override fun observe(): Flow<List<Song>> = state.filterNotNull()

    override val current: List<Song> get() = state.value ?: emptyList()

    override fun add(song: Song) {
        val base = state.value ?: emptyList()
        val updated = (listOf(song) + base.filter { it.trackId != song.trackId }).take(MAX_ENTRIES)
        state.value = updated
        scope.launch { dataSource.write(updated) }
    }

    companion object {
        const val MAX_ENTRIES = 10
    }
}
