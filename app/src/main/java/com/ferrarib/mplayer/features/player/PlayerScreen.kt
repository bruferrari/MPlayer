package com.ferrarib.mplayer.features.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ferrarib.mplayer.features.player.components.ArtworkHero
import com.ferrarib.mplayer.features.player.components.PlaybackControls
import com.ferrarib.mplayer.features.player.components.RecentlyPlayedList

@Composable
fun PlayerScreen(
    windowSizeClass: WindowSizeClass,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        if (isExpanded) {
            ExpandedLayout(
                uiState = uiState,
                onBack = onBack,
                onPlayPause = viewModel::onPlayPauseClick,
                onPrev = viewModel::onPrevClick,
                onNext = viewModel::onNextClick,
                onRowClick = viewModel::onRowClick,
            )
        } else {
            CompactLayout(
                uiState = uiState,
                onBack = onBack,
                onPlayPause = viewModel::onPlayPauseClick,
                onPrev = viewModel::onPrevClick,
                onNext = viewModel::onNextClick,
            )
        }
    }
}

@Composable
private fun ExpandedLayout(
    uiState: PlayerUiState,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onRowClick: (com.ferrarib.mplayer.domain.model.Song) -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Left pane — artwork + metadata + controls
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 32.dp, vertical = 16.dp),
        ) {
            TopBar(title = "Now playing", onBack = onBack)
            Spacer(modifier = Modifier.height(24.dp))
            ArtworkHero(
                artworkUrl = uiState.currentSong?.artworkUrl ?: "",
                contentDescription = uiState.currentSong?.trackName,
                modifier = Modifier.fillMaxWidth(0.85f).align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(24.dp))
            SongMetadata(
                trackName = uiState.currentSong?.trackName ?: "",
                artistName = uiState.currentSong?.artistName ?: "",
            )
            Spacer(modifier = Modifier.height(24.dp))
            PlaybackControls(
                state = uiState.playback,
                onPlayPause = onPlayPause,
                onPrev = onPrev,
                onNext = onNext,
            )
        }

        // Divider
        Spacer(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .padding(vertical = 32.dp)
        )

        // Right pane — recently played list
        RecentlyPlayedList(
            songs = uiState.recentlyPlayed,
            currentTrackId = uiState.playback.currentTrackId,
            onSongClick = onRowClick,
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(top = 72.dp), // align with list content below top bar
        )
    }
}

@Composable
private fun CompactLayout(
    uiState: PlayerUiState,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        TopBar(title = "Now playing", onBack = onBack)
        Spacer(modifier = Modifier.height(32.dp))
        ArtworkHero(
            artworkUrl = uiState.currentSong?.artworkUrl ?: "",
            contentDescription = uiState.currentSong?.trackName,
        )
        Spacer(modifier = Modifier.height(32.dp))
        SongMetadata(
            trackName = uiState.currentSong?.trackName ?: "",
            artistName = uiState.currentSong?.artistName ?: "",
        )
        Spacer(modifier = Modifier.height(32.dp))
        PlaybackControls(
            state = uiState.playback,
            onPlayPause = onPlayPause,
            onPrev = onPrev,
            onNext = onNext,
        )
    }
}

@Composable
private fun TopBar(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun SongMetadata(trackName: String, artistName: String) {
    Text(
        text = trackName,
        style = MaterialTheme.typography.headlineMedium,
        color = Color.White,
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = artistName,
        style = MaterialTheme.typography.bodyLarge,
        color = Color(0xFF737373),
    )
}
