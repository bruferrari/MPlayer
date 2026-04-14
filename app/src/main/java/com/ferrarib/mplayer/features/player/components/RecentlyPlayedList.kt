package com.ferrarib.mplayer.features.player.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.ferrarib.mplayer.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ferrarib.mplayer.domain.model.Song

@Composable
fun RecentlyPlayedList(
    songs: List<Song>,
    currentTrackId: Long?,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val currentIndex = songs.indexOfFirst { it.trackId == currentTrackId }.coerceAtLeast(0)

    LaunchedEffect(currentTrackId) {
        if (songs.isNotEmpty()) listState.animateScrollToItem(currentIndex)
    }

    LazyColumn(state = listState, modifier = modifier) {
        items(songs, key = { it.trackId }) { song ->
            val isCurrent = song.trackId == currentTrackId
            RecentlyPlayedRow(
                song = song,
                isCurrent = isCurrent,
                onClick = { onSongClick(song) },
            )
        }
    }
}

@Composable
private fun RecentlyPlayedRow(
    song: Song,
    isCurrent: Boolean,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        AsyncImage(
            model = song.artworkUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.trackName,
                fontSize = 14.sp,
                color = if (isCurrent) Color.White else Color(0xFFCCCCCC),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = song.artistName,
                fontSize = 12.sp,
                color = Color(0xFF737373),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (isCurrent) {
            Icon(
                painter = painterResource(R.drawable.ic_waves),
                contentDescription = "Now playing",
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp),
            )
        }
    }
}
