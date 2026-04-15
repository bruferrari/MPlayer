package com.ferrarib.mplayer.data.itunes

import com.ferrarib.mplayer.data.itunes.dto.LookupItemDto
import com.ferrarib.mplayer.data.itunes.dto.LookupResponseDto
import com.ferrarib.mplayer.domain.model.Album
import com.ferrarib.mplayer.domain.model.Song

internal fun LookupResponseDto.toAlbum(): Album? {
    val collection = results.firstOrNull { it.wrapperType == "collection" } ?: return null
    val colId = collection.collectionId ?: return null
    val name = collection.collectionName ?: return null
    val artist = collection.artistName ?: return null
    val artwork = collection.artworkUrl100?.upgradeArtwork() ?: ""

    val tracks = results
        .filter { it.wrapperType == "track" }
        .sortedBy { it.trackNumber ?: Int.MAX_VALUE }
        .mapNotNull { it.toTrack() }

    return Album(
        collectionId = colId,
        name = name,
        artist = artist,
        artworkUrl = artwork,
        tracks = tracks
    )
}

private fun LookupItemDto.toTrack(): Song? {
    val id = trackId ?: return null
    val track = trackName ?: return null
    val artist = artistName ?: return null
    val colId = collectionId ?: return null
    val colName = collectionName ?: return null
    return Song(
        trackId = id,
        trackName = track,
        artistName = artist,
        collectionId = colId,
        collectionName = colName,
        artworkUrl = artworkUrl100?.upgradeArtwork() ?: "",
        previewUrl = previewUrl,
        trackTimeMillis = trackTimeMillis
    )
}
