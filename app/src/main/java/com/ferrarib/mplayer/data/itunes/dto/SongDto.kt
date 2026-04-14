package com.ferrarib.mplayer.data.itunes.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SongDto(
    @Json(name = "trackId") val trackId: Long?,
    @Json(name = "trackName") val trackName: String?,
    @Json(name = "artistName") val artistName: String?,
    @Json(name = "collectionId") val collectionId: Long?,
    @Json(name = "collectionName") val collectionName: String?,
    @Json(name = "artworkUrl100") val artworkUrl100: String?,
    @Json(name = "previewUrl") val previewUrl: String?,
    @Json(name = "trackTimeMillis") val trackTimeMillis: Long?
)
