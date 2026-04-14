package com.ferrarib.mplayer.features.songs

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ferrarib.mplayer.core.network.ItunesApi
import com.ferrarib.mplayer.data.itunes.toDomain
import com.ferrarib.mplayer.domain.model.Song

class SongsPagingSource(
    private val api: ItunesApi,
    private val query: String
) : PagingSource<Int, Song>() {

    override fun getRefreshKey(state: PagingState<Int, Song>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(ItunesApi.PAGE_SIZE)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(ItunesApi.PAGE_SIZE)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        if (query.isBlank()) return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)

        val offset = params.key ?: 0
        return try {
            val response = api.search(term = query, offset = offset, limit = params.loadSize)
            val songs = response.results.mapNotNull { it.toDomain() }
            LoadResult.Page(
                data = songs,
                prevKey = if (offset == 0) null else offset - params.loadSize,
                nextKey = if (songs.size < params.loadSize) null else offset + params.loadSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
