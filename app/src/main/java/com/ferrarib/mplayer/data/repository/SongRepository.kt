package com.ferrarib.mplayer.data.repository

import androidx.paging.PagingData
import com.ferrarib.mplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun search(query: String): Flow<PagingData<Song>>
}
