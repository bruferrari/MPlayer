package com.ferrarib.mplayer.data.repository

import com.ferrarib.mplayer.domain.model.Album

interface AlbumRepository {
    suspend fun getAlbum(collectionId: Long): Album
}
