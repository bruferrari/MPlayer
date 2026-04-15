package com.ferrarib.mplayer.core.di

import com.ferrarib.mplayer.data.repository.AlbumRepository
import com.ferrarib.mplayer.data.repository.AlbumRepositoryImpl
import com.ferrarib.mplayer.data.repository.RecentlyPlayedRepository
import com.ferrarib.mplayer.data.repository.RecentlyPlayedRepositoryImpl
import com.ferrarib.mplayer.data.repository.SongRepository
import com.ferrarib.mplayer.data.repository.SongRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSongRepository(impl: SongRepositoryImpl): SongRepository

    @Binds
    @Singleton
    abstract fun bindRecentlyPlayedRepository(impl: RecentlyPlayedRepositoryImpl): RecentlyPlayedRepository

    @Binds
    @Singleton
    abstract fun bindAlbumRepository(impl: AlbumRepositoryImpl): AlbumRepository
}
