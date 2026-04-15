package com.ferrarib.mplayer.core.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.ferrarib.mplayer.data.cache.RecentlyPlayedFile
import com.ferrarib.mplayer.features.player.PlaybackController
import com.ferrarib.mplayer.features.player.PlaybackControllerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {

    @Binds
    @Singleton
    abstract fun bindPlaybackController(impl: PlaybackControllerImpl): PlaybackController

    companion object {

        @Provides
        @Singleton
        fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer =
            ExoPlayer.Builder(context).build()

        @Provides
        @Singleton
        @RecentlyPlayedFile
        fun provideRecentlyPlayedFile(@ApplicationContext context: Context): File =
            File(context.filesDir, "recently_played.json")
    }
}
