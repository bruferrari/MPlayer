package com.ferrarib.mplayer.features.songs.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferrarib.mplayer.R
import com.ferrarib.mplayer.core.ui.ArtworkImage
import com.ferrarib.mplayer.domain.model.Song
import com.ferrarib.mplayer.features.player.PlaybackState
import com.ferrarib.mplayer.ui.theme.MPlayerTheme

private val TrackActiveColor = Color.White
private val TrackInactiveColor = Color(0xFF3A3A3A)

@Composable
fun MiniPlayerBottomBar(
    state: PlaybackState,
    onTap: () -> Unit,
    onPlayPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val song = state.currentSong ?: return
    Column(modifier = modifier.fillMaxWidth().background(Color.Black)) {
        HorizontalDivider(color = Color(0xFF1A1A1A))
        MiniProgressBar(
            positionMs = state.positionMs,
            durationMs = state.durationMs,
            modifier = Modifier.fillMaxWidth().height(2.dp),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onTap)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            MiniArtwork(song = song, size = 48)
            Spacer(modifier = Modifier.width(12.dp))
            MiniMetadata(
                trackName = song.trackName,
                artistName = song.artistName,
                modifier = Modifier.weight(1f),
            )
            MiniControls(
                isPlaying = state.isPlaying,
                onPlayPause = onPlayPause,
                onPrev = onPrev,
                onNext = onNext,
                playButtonSize = 48,
                playIconSize = 24,
                navButtonSize = 48,
                navIconSize = 22,
            )
        }
    }
}

@Composable
fun MiniPlayerSidePanel(
    state: PlaybackState,
    onTap: () -> Unit,
    onPlayPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val song = state.currentSong ?: return
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(Color(0x26FFFFFF), RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onTap)
            .padding(16.dp),
    ) {
        ArtworkImage(
            url = song.artworkUrl,
            contentDescription = stringResource(R.string.cd_artwork_for, song.trackName),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp)),
        )
        Spacer(modifier = Modifier.height(16.dp))
        MiniMetadata(
            trackName = song.trackName,
            artistName = song.artistName,
            modifier = Modifier.fillMaxWidth(),
            centered = true,
        )
        Spacer(modifier = Modifier.height(12.dp))
        MiniProgressBar(
            positionMs = state.positionMs,
            durationMs = state.durationMs,
            modifier = Modifier.fillMaxWidth().height(3.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        MiniControls(
            isPlaying = state.isPlaying,
            onPlayPause = onPlayPause,
            onPrev = onPrev,
            onNext = onNext,
            playButtonSize = 56,
            playIconSize = 28,
            navButtonSize = 48,
            navIconSize = 24,
        )
    }
}

@Composable
private fun MiniArtwork(song: Song, size: Int) {
    ArtworkImage(
        url = song.artworkUrl,
        contentDescription = stringResource(R.string.cd_artwork_for, song.trackName),
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(12.dp)),
    )
}

@Composable
private fun MiniMetadata(
    trackName: String,
    artistName: String,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
) {
    val textAlign = if (centered) androidx.compose.ui.text.style.TextAlign.Center else androidx.compose.ui.text.style.TextAlign.Start
    Column(modifier = modifier) {
        Text(
            text = trackName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign,
        )
        Text(
            text = artistName,
            fontSize = 14.sp,
            color = Color(0xFF737373),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign,
        )
    }
}

@Composable
private fun MiniControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    playButtonSize: Int,
    playIconSize: Int,
    navButtonSize: Int,
    navIconSize: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        IconButton(onClick = onPrev, modifier = Modifier.size(navButtonSize.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_backward),
                contentDescription = stringResource(R.string.cd_previous),
                tint = Color.White,
                modifier = Modifier.size(navIconSize.dp),
            )
        }
        IconButton(
            onClick = onPlayPause,
            modifier = Modifier
                .size(playButtonSize.dp)
                .background(Color(0xFF3A3A3A), CircleShape),
        ) {
            Icon(
                painter = painterResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = stringResource(if (isPlaying) R.string.cd_pause else R.string.cd_play),
                tint = Color.White,
                modifier = Modifier.size(playIconSize.dp),
            )
        }
        IconButton(onClick = onNext, modifier = Modifier.size(navButtonSize.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_forward),
                contentDescription = stringResource(R.string.cd_next),
                tint = Color.White,
                modifier = Modifier.size(navIconSize.dp),
            )
        }
    }
}

@Composable
private fun MiniProgressBar(
    positionMs: Long,
    durationMs: Long,
    modifier: Modifier = Modifier,
) {
    val fraction = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val r = CornerRadius(h / 2)
        drawRoundRect(color = TrackInactiveColor, size = Size(w, h), cornerRadius = r)
        drawRoundRect(color = TrackActiveColor, size = Size(w * fraction, h), cornerRadius = r)
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

private val previewState = PlaybackState(
    isPlaying = true,
    currentSong = previewSong,
    currentTrackId = 1L,
    positionMs = 90_000L,
    durationMs = 354_000L,
)

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 412, name = "MiniPlayer - Phone Bottom Bar")
@Composable
private fun MiniPlayerBottomBarPreview() {
    MPlayerTheme {
        MiniPlayerBottomBar(
            state = previewState,
            onTap = {},
            onPlayPause = {},
            onPrev = {},
            onNext = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 280, heightDp = 800, name = "MiniPlayer - Tablet Side Panel")
@Composable
private fun MiniPlayerSidePanelPreview() {
    MPlayerTheme {
        MiniPlayerSidePanel(
            state = previewState,
            onTap = {},
            onPlayPause = {},
            onPrev = {},
            onNext = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
