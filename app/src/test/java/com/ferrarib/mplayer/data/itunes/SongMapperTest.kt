package com.ferrarib.mplayer.data.itunes

import com.ferrarib.mplayer.data.itunes.dto.SongDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SongMapperTest {

    private val validDto = SongDto(
        trackId = 1,
        trackName = "Perfect",
        artistName = "Ed Sheeran",
        collectionId = 100,
        collectionName = "Divide",
        artworkUrl100 = "https://example.com/artwork/100x100bb.jpg",
        previewUrl = "https://example.com/preview.m4a",
        trackTimeMillis = 263000
    )

    @Test
    fun `maps valid DTO to domain Song`() {
        val song = validDto.toDomain()!!
        assertEquals(1L, song.trackId)
        assertEquals("Perfect", song.trackName)
        assertEquals("Ed Sheeran", song.artistName)
        assertEquals(100L, song.collectionId)
        assertEquals("Divide", song.collectionName)
        assertEquals("https://example.com/artwork/600x600bb.jpg", song.artworkUrl)
        assertEquals("https://example.com/preview.m4a", song.previewUrl)
        assertEquals(263000L, song.trackTimeMillis)
    }

    @Test
    fun `returns null when trackId is missing`() {
        assertNull(validDto.copy(trackId = null).toDomain())
    }

    @Test
    fun `returns null when trackName is missing`() {
        assertNull(validDto.copy(trackName = null).toDomain())
    }

    @Test
    fun `upgrades artwork URL from 100x100bb to 600x600bb`() {
        val url = "https://is1-ssl.mzstatic.com/image/thumb/Music/v4/100x100bb.jpg"
        assertEquals(url.replace("100x100bb", "600x600bb"), url.upgradeArtwork())
    }
}
