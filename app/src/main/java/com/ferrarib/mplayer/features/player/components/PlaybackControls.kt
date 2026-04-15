package com.ferrarib.mplayer.features.player.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferrarib.mplayer.R
import com.ferrarib.mplayer.features.player.PlaybackState

private val TrackActiveColor = Color.White
private val TrackInactiveColor = Color(0xFF3A3A3A)
private val TrackHeight = 4.dp
private val ThumbSize = 18.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackControls(
    state: PlaybackState,
    onPlayPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onRepeat: () -> Unit,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dragProgress by remember { mutableStateOf<Float?>(null) }

    val trackedProgress = if (state.durationMs > 0) {
        (state.positionMs.toFloat() / state.durationMs).coerceIn(0f, 1f)
    } else 0f

    val displayProgress = dragProgress ?: trackedProgress
    val displayPositionMs = (displayProgress * state.durationMs).toLong()

    Column(modifier = modifier.fillMaxWidth()) {
        Slider(
            value = displayProgress,
            onValueChange = { dragProgress = it },
            onValueChangeFinished = {
                dragProgress?.let { onSeek(it) }
                dragProgress = null
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = TrackActiveColor,
                inactiveTrackColor = TrackInactiveColor,
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(ThumbSize)
                        .background(Color.White, CircleShape),
                )
            },
            track = { sliderState ->
                val fraction = (sliderState.value - sliderState.valueRange.start) /
                    (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(TrackHeight),
                ) {
                    val w = size.width
                    val h = size.height
                    val r = CornerRadius(h / 2)
                    drawRoundRect(color = TrackInactiveColor, size = Size(w, h), cornerRadius = r)
                    drawRoundRect(color = TrackActiveColor, size = Size(w * fraction, h), cornerRadius = r)
                }
            },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = displayPositionMs.toMmSs(),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "-${(state.durationMs - displayPositionMs).coerceAtLeast(0L).toMmSs()}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFF3A3A3A), CircleShape),
            ) {
                Icon(
                    painter = painterResource(if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                    contentDescription = if (state.isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp),
                )
            }
            IconButton(onClick = onPrev, modifier = Modifier.size(48.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_backward),
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }
            IconButton(onClick = onNext, modifier = Modifier.size(48.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_forward),
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onRepeat, modifier = Modifier.size(48.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_repeat),
                    contentDescription = "Repeat",
                    tint = if (state.isRepeatOne) Color.White else Color(0xFF737373),
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PlaybackControlsPreview() {
    PlaybackControls(
        state = PlaybackState(),
        onPlayPause = {},
        onPrev = {},
        onNext = {},
        onRepeat = {},
        onSeek = {},
    )
}

private fun Long.toMmSs(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
