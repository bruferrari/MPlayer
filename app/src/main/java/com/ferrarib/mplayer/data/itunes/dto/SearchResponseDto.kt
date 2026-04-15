package com.ferrarib.mplayer.data.itunes.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    val resultCount: Int,
    val results: List<SongDto>,
)
