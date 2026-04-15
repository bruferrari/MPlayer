package com.ferrarib.mplayer.features.player.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ferrarib.mplayer.core.ui.ArtworkImage

@Composable
fun ArtworkHero(
    artworkUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    ArtworkImage(
        url = artworkUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp)),
    )
}
