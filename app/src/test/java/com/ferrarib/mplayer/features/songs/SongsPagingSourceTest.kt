package com.ferrarib.mplayer.features.songs

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.ferrarib.mplayer.core.network.ItunesApi
import com.ferrarib.mplayer.data.itunes.dto.SearchResponseDto
import com.ferrarib.mplayer.data.itunes.dto.SongDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SongsPagingSourceTest {

    private fun songDto(id: Long) = SongDto(
        trackId = id, trackName = "Song $id", artistName = "Artist",
        collectionId = 1L, collectionName = "Album",
        artworkUrl100 = "https://example.com/100x100bb.jpg",
        previewUrl = null, trackTimeMillis = null
    )

    private fun fakeApi(totalItems: Int): ItunesApi = object : ItunesApi {
        override suspend fun search(term: String, entity: String, limit: Int, offset: Int): SearchResponseDto {
            val slice = (offset until minOf(offset + limit, totalItems))
                .map { songDto(it.toLong()) }
            return SearchResponseDto(resultCount = totalItems, results = slice)
        }
        override suspend fun lookup(id: Long, entity: String) =
            throw UnsupportedOperationException("not used in this test")
    }

    @Test
    fun `GIVEN blank query WHEN pager refreshes THEN returns empty page`() = runTest {
        val pager = TestPager(
            config = PagingConfig(pageSize = 25),
            pagingSource = SongsPagingSource(fakeApi(100), "")
        )
        val result = pager.refresh() as PagingSource.LoadResult.Page
        assertEquals(0, result.data.size)
        assertNull(result.nextKey)
    }

    @Test
    fun `GIVEN 100 items and page size 25 WHEN first page loaded THEN returns 25 items with nextKey`() = runTest {
        val pager = TestPager(
            config = PagingConfig(pageSize = 25, initialLoadSize = 25),
            pagingSource = SongsPagingSource(fakeApi(100), "test")
        )
        val result = pager.refresh() as PagingSource.LoadResult.Page
        assertEquals(25, result.data.size)
        assertEquals(25, result.nextKey)
        assertNull(result.prevKey)
    }

    @Test
    fun `GIVEN 10 items and page size 25 WHEN first page loaded THEN returns 10 items with null nextKey`() = runTest {
        val pager = TestPager(
            config = PagingConfig(pageSize = 25, initialLoadSize = 25),
            pagingSource = SongsPagingSource(fakeApi(10), "test")
        )
        val result = pager.refresh() as PagingSource.LoadResult.Page
        assertEquals(10, result.data.size)
        assertNull(result.nextKey)
    }
}
