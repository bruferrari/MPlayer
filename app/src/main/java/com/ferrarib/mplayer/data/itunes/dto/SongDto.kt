package com.ferrarib.mplayer.data.itunes.dto

import kotlinx.serialization.Serializable

@Serializable
data class SongDto(
    val trackId: Long? = null,
    val trackName: String? = null,
    val artistName: String? = null,
    val collectionId: Long? = null,
    val collectionName: String? = null,
    val artworkUrl100: String? = null,
    val previewUrl: String? = null,
    val trackTimeMillis: Long? = null,
)
