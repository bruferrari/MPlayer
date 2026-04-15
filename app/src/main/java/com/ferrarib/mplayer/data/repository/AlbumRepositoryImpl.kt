package com.ferrarib.mplayer.data.repository

import com.ferrarib.mplayer.core.network.ItunesApi
import com.ferrarib.mplayer.data.itunes.toAlbum
import com.ferrarib.mplayer.domain.model.Album
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    private val api: ItunesApi
) : AlbumRepository {

    override suspend fun getAlbum(collectionId: Long): Album =
        api.lookup(collectionId).toAlbum()
            ?: throw Exception("Album $collectionId not found")
}
