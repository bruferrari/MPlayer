package com.ferrarib.mplayer.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.SubcomposeAsyncImage
import com.ferrarib.mplayer.R

@Composable
fun ArtworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        loading = { ArtworkPlaceholder() },
        error = { ArtworkPlaceholder() },
    )
}

@Composable
private fun ArtworkPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_songs_list),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.fillMaxSize(0.4f),
        )
    }
}
