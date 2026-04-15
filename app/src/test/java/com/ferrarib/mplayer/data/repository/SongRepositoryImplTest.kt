package com.ferrarib.mplayer.data.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.ferrarib.mplayer.core.network.ItunesApi
import com.ferrarib.mplayer.features.songs.SongsPagingSource
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class SongRepositoryImplTest {

    private val server = MockWebServer()
    private lateinit var api: ItunesApi

    @Before
    fun setUp() {
        server.start()
        val json = Json { ignoreUnknownKeys = true }
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ItunesApi::class.java)
    }

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `GIVEN successful search response WHEN pager refreshes THEN returns mapped domain Songs`() = kotlinx.coroutines.test.runTest {
        server.enqueue(
            MockResponse().setBody(
                """
                {
                  "resultCount": 1,
                  "results": [{
                    "trackId": 42,
                    "trackName": "Perfect",
                    "artistName": "Ed Sheeran",
                    "collectionId": 99,
                    "collectionName": "Divide",
                    "artworkUrl100": "https://example.com/100x100bb.jpg",
                    "previewUrl": "https://example.com/preview.m4a",
                    "trackTimeMillis": 263000
                  }]
                }
                """.trimIndent()
            ).setResponseCode(200)
        )
        val pager = TestPager(
            config = PagingConfig(pageSize = 25, initialLoadSize = 25),
            pagingSource = SongsPagingSource(api, "perfect")
        )
        val result = pager.refresh() as PagingSource.LoadResult.Page
        assertEquals(1, result.data.size)
        assertEquals("Perfect", result.data.first().trackName)
        assertEquals("https://example.com/600x600bb.jpg", result.data.first().artworkUrl)
    }

    @Test
    fun `GIVEN empty search response WHEN pager refreshes THEN returns empty page`() = kotlinx.coroutines.test.runTest {
        server.enqueue(
            MockResponse().setBody("""{"resultCount":0,"results":[]}""").setResponseCode(200)
        )
        val pager = TestPager(
            config = PagingConfig(pageSize = 25, initialLoadSize = 25),
            pagingSource = SongsPagingSource(api, "xyz")
        )
        val result = pager.refresh() as PagingSource.LoadResult.Page
        assertEquals(0, result.data.size)
    }
}
