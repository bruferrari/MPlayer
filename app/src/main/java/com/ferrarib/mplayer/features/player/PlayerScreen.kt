package com.ferrarib.mplayer.features.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ferrarib.mplayer.ui.theme.MPlayerTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ferrarib.mplayer.R
import com.ferrarib.mplayer.core.ui.DevicePosture
import com.ferrarib.mplayer.domain.model.Song
import com.ferrarib.mplayer.features.player.components.ArtworkHero
import com.ferrarib.mplayer.features.player.components.PlaybackControls
import com.ferrarib.mplayer.features.player.components.RecentlyPlayedList

@Composable
fun PlayerScreen(
    windowSizeClass: WindowSizeClass,
    devicePosture: DevicePosture,
    onBack: () -> Unit,
    onViewAlbum: (collectionId: Long) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) { viewModel.startInitialPlaybackIfNeeded() }
    LaunchedEffect(viewModel) {
        while (coroutineContext.isActive) {
            delay(500L)
            viewModel.tickPosition()
        }
    }

    val widthClass = windowSizeClass.widthSizeClass
    val isExpanded = widthClass == WindowWidthSizeClass.Expanded
    val isPhone = widthClass == WindowWidthSizeClass.Compact
    val isTableTop = devicePosture is DevicePosture.TableTop
    val collectionId = uiState.currentSong?.collectionId ?: 0L

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeDrawingPadding()
    ) {
        when {
            isTableTop -> TableTopLayout(
                uiState = uiState,
                onBack = onBack,
                onViewAlbum = { onViewAlbum(collectionId) },
                onPlayPause = viewModel::onPlayPauseClick,
                onPrev = viewModel::onPrevClick,
                onNext = viewModel::onNextClick,
                onRepeat = viewModel::onRepeatClick,
                onSeek = viewModel::onSeek,
            )
            isExpanded -> ExpandedLayout(
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
            else -> CompactLayout(
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

// Tabletop posture (Galaxy Fold / Pixel Fold half-opened, horizontal hinge):
// artwork lives on the upper half (the "screen"), metadata + controls live on
// the lower half (the "keyboard"), mirroring how a laptop is held.
@Composable
private fun TableTopLayout(
    uiState: PlayerUiState,
    onBack: () -> Unit,
    onViewAlbum: () -> Unit,
    onPlayPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onRepeat: () -> Unit,
    onSeek: (Float) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            TopBar(onBack = onBack, onViewAlbum = onViewAlbum)
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                ArtworkHero(
                    artworkUrl = uiState.currentSong?.artworkUrl ?: "",
                    contentDescription = uiState.currentSong?.trackName,
                    modifier = Modifier.fillMaxWidth(0.4f),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
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
                onRepeat = onRepeat,
                onSeek = onSeek,
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
                    modifier = Modifier.fillMaxWidth(0.4f),
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

        RightPane(
            modifier = Modifier.weight(0.30f),
            uiState = uiState,
            onRowClick = onRowClick,
        )
    }
}

@Composable
private fun RightPane(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState,
    onRowClick: (Song) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(top = 16.dp, end = 20.dp)
            .background(Color(0x26FFFFFF), RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 20.dp),
            contentAlignment = Alignment.TopStart,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_songs_list),
                contentDescription = stringResource(R.string.cd_queue),
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }

        RecentlyPlayedList(
            songs = uiState.queue,
            currentTrackId = uiState.playback.currentTrackId,
            onSongClick = onRowClick,
            modifier = Modifier.fillMaxSize(),
        )
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
                contentDescription = stringResource(R.string.cd_back),
                tint = Color.White,
            )
        }
        Text(
            text = stringResource(R.string.title_now_playing),
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
                    contentDescription = stringResource(R.string.cd_more_options),
                    tint = Color.White,
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_view_album)) },
                    onClick = {
                        menuExpanded = false
                        onViewAlbum()
                    }
                )
            }
        }
    }
}

private val previewSong = Song(
    trackId = 1L,
    trackName = "Bohemian Rhapsody",
    artistName = "Queen",
    collectionId = 100L,
    collectionName = "A Night at the Opera",
    artworkUrl = "",
    previewUrl = null,
    trackTimeMillis = 354000L,
)

private val previewPlayerUiState = PlayerUiState(
    currentSong = previewSong,
    queue = listOf(
        previewSong,
        previewSong.copy(trackId = 2L, trackName = "Don't Stop Me Now"),
        previewSong.copy(trackId = 3L, trackName = "We Will Rock You"),
    ),
    playback = PlaybackState(
        isPlaying = true,
        positionMs = 120000L,
        durationMs = 354000L,
        currentTrackId = 1L,
        currentSong = previewSong,
    ),
)

@Preview(showBackground = true, backgroundColor = 0xFF000000, name = "Player - Phone")
@Composable
private fun PlayerCompactPreview() {
    MPlayerTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            CompactLayout(
                uiState = previewPlayerUiState,
                isPhone = true,
                onBack = {},
                onViewAlbum = {},
                onPlayPause = {},
                onPrev = {},
                onNext = {},
                onRepeat = {},
                onSeek = {},
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 1280,
    heightDp = 800,
    name = "Player - Tablet",
)
@Composable
private fun PlayerExpandedPreview() {
    MPlayerTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            ExpandedLayout(
                uiState = previewPlayerUiState,
                onBack = {},
                onViewAlbum = {},
                onPlayPause = {},
                onPrev = {},
                onNext = {},
                onRepeat = {},
                onSeek = {},
                onRowClick = {},
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 840,
    heightDp = 1100,
    name = "Player - Foldable TableTop",
)
@Composable
private fun PlayerTableTopPreview() {
    MPlayerTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            TableTopLayout(
                uiState = previewPlayerUiState,
                onBack = {},
                onViewAlbum = {},
                onPlayPause = {},
                onPrev = {},
                onNext = {},
                onRepeat = {},
                onSeek = {},
            )
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
