package com.ferrarib.mplayer.domain.model

data class Album(
    val collectionId: Long,
    val name: String,
    val artist: String,
    val artworkUrl: String,
    val tracks: List<Song>
)
