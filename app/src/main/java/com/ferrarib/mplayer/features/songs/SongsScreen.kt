package com.ferrarib.mplayer.features.songs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ferrarib.mplayer.domain.model.Song
import com.ferrarib.mplayer.features.songs.components.SongRow

@Composable
fun SongsScreen(
    windowSizeClass: WindowSizeClass,
    onSongClick: (Song) -> Unit,
    onViewAlbum: (Song) -> Unit,
    viewModel: SongsViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val songs = viewModel.pagingData.collectAsLazyPagingItems()

    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
        // Two-pane: songs list fills this composable;
        // detail pane is managed by the parent NavHost scaffold.
        SongListContent(
            query = query,
            songs = songs,
            onQueryChange = viewModel::onQueryChange,
            onSongClick = onSongClick,
            onViewAlbum = onViewAlbum
        )
    } else {
        SongListContent(
            query = query,
            songs = songs,
            onQueryChange = viewModel::onQueryChange,
            onSongClick = onSongClick,
            onViewAlbum = onViewAlbum
        )
    }
}

@Composable
private fun SongListContent(
    query: String,
    songs: LazyPagingItems<Song>,
    onQueryChange: (String) -> Unit,
    onSongClick: (Song) -> Unit,
    onViewAlbum: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().safeDrawingPadding()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search your library") },
            leadingIcon = {
                Icon(Icons.Rounded.Search, contentDescription = null)
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        Box(modifier = Modifier.weight(1f)) {
            when {
                query.isBlank() -> EmptyPrompt("Search for songs")
                songs.loadState.refresh is LoadState.Loading -> CenteredProgress()
                songs.loadState.refresh is LoadState.Error -> {
                    val e = (songs.loadState.refresh as LoadState.Error).error
                    ErrorPrompt(e.localizedMessage ?: "Something went wrong") { songs.retry() }
                }
                songs.itemCount == 0 && songs.loadState.refresh is LoadState.NotLoading ->
                    EmptyPrompt("No results for \"$query\"")
                else -> SongsList(songs = songs, onSongClick = onSongClick, onViewAlbum = onViewAlbum)
            }
        }
    }
}

@Composable
private fun SongsList(
    songs: LazyPagingItems<Song>,
    onSongClick: (Song) -> Unit,
    onViewAlbum: (Song) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
        items(count = songs.itemCount, key = songs.itemKey { it.trackId }) { index ->
            val song = songs[index] ?: return@items
            SongRow(
                song = song,
                onClick = { onSongClick(song) },
                onViewAlbum = { onViewAlbum(song) }
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                modifier = Modifier.padding(start = 76.dp)
            )
        }
        if (songs.loadState.append is LoadState.Loading) {
            item { CenteredProgress(modifier = Modifier.padding(16.dp)) }
        }
        if (songs.loadState.append is LoadState.Error) {
            val e = (songs.loadState.append as LoadState.Error).error
            item {
                TextButton(
                    onClick = { songs.retry() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Retry — ${e.localizedMessage}")
                }
            }
        }
    }
}

@Composable
private fun EmptyPrompt(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorPrompt(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = onRetry) { Text("Retry") }
        }
    }
}

@Composable
private fun CenteredProgress(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
