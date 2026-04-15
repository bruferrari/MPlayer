package com.ferrarib.mplayer.data.repository

import com.ferrarib.mplayer.core.network.ItunesApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AlbumRepositoryImplTest {

    private val server = MockWebServer()
    private lateinit var repo: AlbumRepositoryImpl

    @Before
    fun setUp() {
        server.start()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ItunesApi::class.java)
        repo = AlbumRepositoryImpl(api)
    }

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `GIVEN successful lookup response WHEN getAlbum called THEN returns mapped Album`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """
                {
                  "resultCount": 3,
                  "results": [
                    {
                      "wrapperType": "collection",
                      "collectionId": 100,
                      "collectionName": "A Night at the Opera",
                      "artistName": "Queen",
                      "artworkUrl100": "https://example.com/100x100bb.jpg"
                    },
                    {
                      "wrapperType": "track",
                      "trackId": 1,
                      "trackName": "Bohemian Rhapsody",
                      "artistName": "Queen",
                      "collectionId": 100,
                      "collectionName": "A Night at the Opera",
                      "artworkUrl100": "https://example.com/100x100bb.jpg",
                      "trackNumber": 1,
                      "previewUrl": "https://example.com/preview.m4a",
                      "trackTimeMillis": 354000
                    },
                    {
                      "wrapperType": "track",
                      "trackId": 2,
                      "trackName": "You're My Best Friend",
                      "artistName": "Queen",
                      "collectionId": 100,
                      "collectionName": "A Night at the Opera",
                      "artworkUrl100": "https://example.com/100x100bb.jpg",
                      "trackNumber": 2,
                      "previewUrl": "https://example.com/preview.m4a",
                      "trackTimeMillis": 172000
                    }
                  ]
                }
                """.trimIndent()
            ).setResponseCode(200)
        )

        val album = repo.getAlbum(100L)

        assertEquals(100L, album.collectionId)
        assertEquals("A Night at the Opera", album.name)
        assertEquals("Queen", album.artist)
        assertEquals("https://example.com/600x600bb.jpg", album.artworkUrl)
        assertEquals(2, album.tracks.size)
        assertEquals("Bohemian Rhapsody", album.tracks[0].trackName)
        assertEquals("You're My Best Friend", album.tracks[1].trackName)

        val request = server.takeRequest()
        assertEquals("/lookup?id=100&entity=song", request.path)
    }

    @Test
    fun `GIVEN empty lookup response WHEN getAlbum called THEN throws exception`() = runTest {
        server.enqueue(
            MockResponse().setBody("""{"resultCount":0,"results":[]}""").setResponseCode(200)
        )
        assertThrows(Exception::class.java) {
            kotlinx.coroutines.runBlocking { repo.getAlbum(999L) }
        }
    }
}
