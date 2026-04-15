package com.ferrarib.mplayer.features.songs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.ferrarib.mplayer.R
import com.ferrarib.mplayer.ui.theme.MPlayerTheme
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.flowOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ferrarib.mplayer.domain.model.Song
import com.ferrarib.mplayer.features.songs.components.SongRow

@Composable
@Suppress("UNUSED_PARAMETER")
fun SongsScreen(
    windowSizeClass: WindowSizeClass,
    onSongClick: (Song) -> Unit,
    onViewAlbum: (Song) -> Unit,
    viewModel: SongsViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val songs = viewModel.pagingData.collectAsLazyPagingItems()
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeDrawingPadding()
    ) {
        SongListContent(
            query = query,
            songs = songs,
            recentlyPlayed = recentlyPlayed,
            onQueryChange = viewModel::onQueryChange,
            onSongClick = { song ->
                viewModel.onSongTapped(song)
                onSongClick(song)
            },
            onViewAlbum = onViewAlbum,
        )
    }
}

@Composable
private fun SongListContent(
    query: String,
    songs: LazyPagingItems<Song>,
    recentlyPlayed: List<Song>,
    onQueryChange: (String) -> Unit,
    onSongClick: (Song) -> Unit,
    onViewAlbum: (Song) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.title_songs),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp)
        )

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.hint_search_library),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Box(modifier = Modifier.weight(1f)) {
            when {
                query.isBlank() && recentlyPlayed.isNotEmpty() ->
                    RecentlyPlayedSection(
                        songs = recentlyPlayed,
                        onSongClick = onSongClick,
                        onViewAlbum = onViewAlbum,
                    )
                query.isBlank() -> EmptyPrompt(stringResource(R.string.empty_search_for_songs))
                songs.loadState.refresh is LoadState.Loading -> CenteredProgress()
                songs.loadState.refresh is LoadState.Error -> {
                    val e = (songs.loadState.refresh as LoadState.Error).error
                    ErrorPrompt(e.localizedMessage ?: "") { songs.retry() }
                }
                songs.itemCount == 0 && songs.loadState.refresh is LoadState.NotLoading ->
                    EmptyPrompt(stringResource(R.string.empty_no_results, query))
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
    LazyColumn(contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)) {
        items(count = songs.itemCount, key = songs.itemKey { it.trackId }) { index ->
            val song = songs[index] ?: return@items
            SongRow(
                song = song,
                onClick = { onSongClick(song) },
                onViewAlbum = { onViewAlbum(song) }
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
                    Text(stringResource(R.string.error_retry_message, e.localizedMessage ?: ""))
                }
            }
        }
    }
}

@Composable
private fun RecentlyPlayedSection(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    onViewAlbum: (Song) -> Unit,
) {
    Column {
        Text(
            text = stringResource(R.string.section_recently_played),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        LazyColumn(contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)) {
            items(count = songs.size, key = { songs[it].trackId }) { index ->
                val song = songs[index]
                SongRow(
                    song = song,
                    onClick = { onSongClick(song) },
                    onViewAlbum = { onViewAlbum(song) },
                )
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
            TextButton(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
        }
    }
}

@Composable
private fun CenteredProgress(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

private val previewSongs = listOf(
    Song(1L, "Bohemian Rhapsody", "Queen", 100L, "A Night at the Opera", "", null, 354000L),
    Song(2L, "Don't Stop Me Now", "Queen", 100L, "Jazz", "", null, 209000L),
    Song(3L, "We Will Rock You", "Queen", 100L, "News of the World", "", null, 121000L),
)

@Preview(showBackground = true, backgroundColor = 0xFF000000, name = "Songs - Empty")
@Composable
private fun SongsEmptyPreview() {
    val songs = flowOf(PagingData.empty<Song>()).collectAsLazyPagingItems()
    MPlayerTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            SongListContent(
                query = "",
                songs = songs,
                recentlyPlayed = emptyList(),
                onQueryChange = {},
                onSongClick = {},
                onViewAlbum = {},
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, name = "Songs - Recently Played")
@Composable
private fun SongsRecentlyPlayedPreview() {
    val songs = flowOf(PagingData.empty<Song>()).collectAsLazyPagingItems()
    MPlayerTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            SongListContent(
                query = "",
                songs = songs,
                recentlyPlayed = previewSongs,
                onQueryChange = {},
                onSongClick = {},
                onViewAlbum = {},
            )
        }
    }
}
