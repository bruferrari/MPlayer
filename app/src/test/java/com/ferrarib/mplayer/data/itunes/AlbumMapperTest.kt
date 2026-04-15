package com.ferrarib.mplayer.data.itunes

import com.ferrarib.mplayer.data.itunes.dto.LookupItemDto
import com.ferrarib.mplayer.data.itunes.dto.LookupResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlbumMapperTest {

    private val collectionItem = LookupItemDto(
        wrapperType = "collection",
        collectionId = 100L,
        collectionName = "A Night at the Opera",
        artistName = "Queen",
        artworkUrl100 = "https://example.com/100x100bb.jpg",
        trackId = null,
        trackName = null,
        trackNumber = null,
        previewUrl = null,
        trackTimeMillis = null,
    )

    private fun trackItem(id: Long, name: String, number: Int) = LookupItemDto(
        wrapperType = "track",
        collectionId = 100L,
        collectionName = "A Night at the Opera",
        artistName = "Queen",
        artworkUrl100 = "https://example.com/100x100bb.jpg",
        trackId = id,
        trackName = name,
        trackNumber = number,
        previewUrl = "https://example.com/preview.m4a",
        trackTimeMillis = 354000L,
    )

    @Test
    fun `GIVEN collection and track items WHEN toAlbum called THEN returns mapped Album`() {
        val response = LookupResponseDto(
            resultCount = 3,
            results = listOf(
                collectionItem,
                trackItem(1L, "Bohemian Rhapsody", 1),
                trackItem(2L, "You're My Best Friend", 2),
            )
        )
        val album = response.toAlbum()!!
        assertEquals(100L, album.collectionId)
        assertEquals("A Night at the Opera", album.name)
        assertEquals("Queen", album.artist)
        assertEquals("https://example.com/600x600bb.jpg", album.artworkUrl)
        assertEquals(2, album.tracks.size)
        assertEquals("Bohemian Rhapsody", album.tracks[0].trackName)
        assertEquals("You're My Best Friend", album.tracks[1].trackName)
    }

    @Test
    fun `GIVEN tracks out of order WHEN toAlbum called THEN sorts by trackNumber`() {
        val response = LookupResponseDto(
            resultCount = 3,
            results = listOf(
                collectionItem,
                trackItem(2L, "You're My Best Friend", 2),
                trackItem(1L, "Bohemian Rhapsody", 1),
            )
        )
        val album = response.toAlbum()!!
        assertEquals("Bohemian Rhapsody", album.tracks[0].trackName)
        assertEquals("You're My Best Friend", album.tracks[1].trackName)
    }

    @Test
    fun `GIVEN no collection item WHEN toAlbum called THEN returns null`() {
        val response = LookupResponseDto(
            resultCount = 1,
            results = listOf(trackItem(1L, "Bohemian Rhapsody", 1))
        )
        assertNull(response.toAlbum())
    }

    @Test
    fun `GIVEN tracks with missing required fields WHEN toAlbum called THEN skips invalid tracks`() {
        val trackMissingId = trackItem(1L, "Bohemian Rhapsody", 1).copy(trackId = null)
        val trackMissingName = trackItem(2L, "You're My Best Friend", 2).copy(trackName = null)
        val response = LookupResponseDto(
            resultCount = 3,
            results = listOf(collectionItem, trackMissingId, trackMissingName)
        )
        val album = response.toAlbum()!!
        assertEquals(0, album.tracks.size)
    }

    @Test
    fun `GIVEN collection missing collectionId WHEN toAlbum called THEN returns null`() {
        val response = LookupResponseDto(
            resultCount = 1,
            results = listOf(collectionItem.copy(collectionId = null))
        )
        assertNull(response.toAlbum())
    }

    @Test
    fun `GIVEN collection missing name WHEN toAlbum called THEN returns null`() {
        val response = LookupResponseDto(
            resultCount = 1,
            results = listOf(collectionItem.copy(collectionName = null))
        )
        assertNull(response.toAlbum())
    }

    @Test
    fun `GIVEN artwork URL with 100x100bb WHEN toAlbum called THEN upgrades to 600x600bb`() {
        val response = LookupResponseDto(
            resultCount = 1,
            results = listOf(collectionItem)
        )
        val album = response.toAlbum()!!
        assertEquals("https://example.com/600x600bb.jpg", album.artworkUrl)
    }

    @Test
    fun `GIVEN empty results WHEN toAlbum called THEN returns null`() {
        val response = LookupResponseDto(resultCount = 0, results = emptyList())
        assertNull(response.toAlbum())
    }
}
