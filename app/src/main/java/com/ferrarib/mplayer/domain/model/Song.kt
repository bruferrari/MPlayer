package com.ferrarib.mplayer.domain.model

data class Song(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val collectionId: Long,
    val collectionName: String,
    val artworkUrl: String,
    val previewUrl: String?,
    val trackTimeMillis: Long?
)
