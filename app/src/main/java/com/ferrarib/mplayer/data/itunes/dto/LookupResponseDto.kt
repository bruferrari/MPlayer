package com.ferrarib.mplayer.data.itunes.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LookupResponseDto(
    @Json(name = "resultCount") val resultCount: Int,
    @Json(name = "results") val results: List<LookupItemDto>
)

@JsonClass(generateAdapter = true)
data class LookupItemDto(
    @Json(name = "wrapperType") val wrapperType: String?,
    @Json(name = "collectionId") val collectionId: Long?,
    @Json(name = "collectionName") val collectionName: String?,
    @Json(name = "artistName") val artistName: String?,
    @Json(name = "artworkUrl100") val artworkUrl100: String?,
    @Json(name = "trackId") val trackId: Long?,
    @Json(name = "trackName") val trackName: String?,
    @Json(name = "trackNumber") val trackNumber: Int?,
    @Json(name = "previewUrl") val previewUrl: String?,
    @Json(name = "trackTimeMillis") val trackTimeMillis: Long?
)
