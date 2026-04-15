package com.ferrarib.mplayer.data.repository

import com.ferrarib.mplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface RecentlyPlayedRepository {
    fun observe(): Flow<List<Song>>
    fun add(song: Song)
    val current: List<Song>
}
