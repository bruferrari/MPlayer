package com.ferrarib.mplayer.features.player.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ArtworkHero(
    artworkUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp)),
        model = artworkUrl,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop
    )
}
