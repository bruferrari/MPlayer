package com.ferrarib.mplayer.core.navigation

object AppDestinations {
    const val SPLASH = "splash"
    const val SONGS = "songs"

    const val PLAYER_ROUTE = "player/{trackId}"
    const val ALBUM_ROUTE = "album/{collectionId}"
    const val ARG_TRACK_ID = "trackId"
    const val ARG_COLLECTION_ID = "collectionId"

    fun player(trackId: Long) = "player/$trackId"
    fun album(collectionId: Long) = "album/$collectionId"
}
