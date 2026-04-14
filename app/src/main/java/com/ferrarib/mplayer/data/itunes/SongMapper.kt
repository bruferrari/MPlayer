package com.ferrarib.mplayer.data.itunes

import com.ferrarib.mplayer.data.itunes.dto.SongDto
import com.ferrarib.mplayer.domain.model.Song

internal fun SongDto.toDomain(): Song? {
    val id = trackId ?: return null
    val name = trackName ?: return null
    val artist = artistName ?: return null
    val colId = collectionId ?: return null
    val colName = collectionName ?: return null
    return Song(
        trackId = id,
        trackName = name,
        artistName = artist,
        collectionId = colId,
        collectionName = colName,
        artworkUrl = artworkUrl100?.upgradeArtwork() ?: "",
        previewUrl = previewUrl,
        trackTimeMillis = trackTimeMillis
    )
}

/** Replaces iTunes thumbnail (100x100bb) with a high-res version (600x600bb). */
internal fun String.upgradeArtwork(): String =
    replace("100x100bb", "600x600bb")
