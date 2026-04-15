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
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ferrarib.mplayer.R
import com.ferrarib.mplayer.domain.model.Song
import com.ferrarib.mplayer.features.player.components.ArtworkHero
import com.ferrarib.mplayer.features.player.components.PlaybackControls
import com.ferrarib.mplayer.features.player.components.RecentlyPlayedList

@Composable
fun PlayerScreen(
    windowSizeClass: WindowSizeClass,
    onBack: () -> Unit,
    onViewAlbum: (collectionId: Long) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val widthClass = windowSizeClass.widthSizeClass
    val isExpanded = widthClass == WindowWidthSizeClass.Expanded
    val isPhone = widthClass == WindowWidthSizeClass.Compact
    val collectionId = uiState.currentSong?.collectionId ?: 0L

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        if (isExpanded) {
            ExpandedLayout(
                uiState = uiState,
                onBack = onBack,
                onViewAlbum = { onViewAlbum(collectionId) },
                onPlayPause = viewModel::onPlayPauseClick,
                onPrev = viewModel::onPrevClick,
                onNext = viewModel::onNextClick,
                onRepeat = viewModel::onRepeatClick,
                onSeek = viewModel::onSeek,
                onRowClick = viewModel::onRowClick,
            )
        } else {
            CompactLayout(
                uiState = uiState,
                isPhone = isPhone,
                onBack = onBack,
                onViewAlbum = { onViewAlbum(collectionId) },
                onPlayPause = viewModel::onPlayPauseClick,
                onPrev = viewModel::onPrevClick,
                onNext = viewModel::onNextClick,
                onRepeat = viewModel::onRepeatClick,
                onSeek = viewModel::onSeek,
            )
        }
    }
}

@Composable
private fun ExpandedLayout(
    uiState: PlayerUiState,
    onBack: () -> Unit,
    onViewAlbum: () -> Unit,
    onPlayPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onRepeat: () -> Unit,
    onSeek: (Float) -> Unit,
    onRowClick: (Song) -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Left pane — artwork + metadata + controls
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            TopBar(onBack = onBack, onViewAlbum = onViewAlbum)
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                ArtworkHero(
                    artworkUrl = uiState.currentSong?.artworkUrl ?: "",
                    contentDescription = uiState.currentSong?.trackName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 200.dp),
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            SongMetadata(
                trackName = uiState.currentSong?.trackName ?: "",
                artistName = uiState.currentSong?.artistName ?: "",
            )
            Spacer(modifier = Modifier.height(16.dp))
            PlaybackControls(
                state = uiState.playback,
                onPlayPause = onPlayPause,
                onPrev = onPrev,
                onNext = onNext,
                onRepeat = onRepeat,
                onSeek = onSeek,
            )
        }

        // Right pane — recently played list with header icon
        Column(
            modifier = Modifier
                .weight(0.55f)
                .fillMaxHeight()
                .padding(top = 16.dp, end = 8.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp, bottom = 8.dp),
                contentAlignment = Alignment.TopEnd,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_songs_list),
                    contentDescription = "Queue",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
            RecentlyPlayedList(
                songs = uiState.recentlyPlayed,
                currentTrackId = uiState.playback.currentTrackId,
                onSongClick = onRowClick,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun CompactLayout(
    uiState: PlayerUiState,
    isPhone: Boolean,
    onBack: () -> Unit,
    onViewAlbum: () -> Unit,
    onPlayPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onRepeat: () -> Unit,
    onSeek: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        TopBar(onBack = onBack, onViewAlbum = onViewAlbum)

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = if (isPhone) {
                Modifier.weight(1f).fillMaxWidth()
            } else {
                Modifier.weight(1f).padding(horizontal = 100.dp).fillMaxWidth()
            },
            contentAlignment = Alignment.Center,
        ) {
            ArtworkHero(
                artworkUrl = uiState.currentSong?.artworkUrl ?: "",
                contentDescription = uiState.currentSong?.trackName,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SongMetadata(
            trackName = uiState.currentSong?.trackName ?: "",
            artistName = uiState.currentSong?.artistName ?: "",
        )

        Spacer(modifier = Modifier.height(20.dp))

        PlaybackControls(
            state = uiState.playback,
            onPlayPause = onPlayPause,
            onPrev = onPrev,
            onNext = onNext,
            onRepeat = onRepeat,
            onSeek = onSeek,
        )
    }
}

@Composable
private fun TopBar(onBack: () -> Unit, onViewAlbum: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
            )
        }
        Text(
            text = "Now playing",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
        )
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "More options",
                    tint = Color.White,
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text("View album") },
                    onClick = {
                        menuExpanded = false
                        onViewAlbum()
                    }
                )
            }
        }
    }
}

@Composable
private fun SongMetadata(trackName: String, artistName: String) {
    Text(
        text = trackName,
        style = MaterialTheme.typography.headlineLarge,
        color = Color.White,
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = artistName,
        style = MaterialTheme.typography.bodyLarge,
        color = Color(0xFF737373),
    )
}
