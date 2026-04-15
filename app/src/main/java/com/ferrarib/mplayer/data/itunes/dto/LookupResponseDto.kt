package com.ferrarib.mplayer.data.itunes.dto

import kotlinx.serialization.Serializable

@Serializable
data class LookupResponseDto(
    val resultCount: Int,
    val results: List<LookupItemDto>,
)

@Serializable
data class LookupItemDto(
    val wrapperType: String? = null,
    val collectionId: Long? = null,
    val collectionName: String? = null,
    val artistName: String? = null,
    val artworkUrl100: String? = null,
    val trackId: Long? = null,
    val trackName: String? = null,
    val trackNumber: Int? = null,
    val previewUrl: String? = null,
    val trackTimeMillis: Long? = null,
)
