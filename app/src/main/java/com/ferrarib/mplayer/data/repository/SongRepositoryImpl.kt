package com.ferrarib.mplayer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ferrarib.mplayer.core.network.ItunesApi
import com.ferrarib.mplayer.domain.model.Song
import com.ferrarib.mplayer.features.songs.SongsPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val api: ItunesApi
) : SongRepository {

    override fun search(query: String): Flow<PagingData<Song>> =
        Pager(
            config = PagingConfig(
                pageSize = ItunesApi.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SongsPagingSource(api, query) }
        ).flow
}
