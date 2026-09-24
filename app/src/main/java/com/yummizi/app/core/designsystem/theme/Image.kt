package com.yummizi.app.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

/**
 * Global enterprise wrapper for asynchronous image loading.
 * Completely isolates the feature layers from knowing about Coil's underlying APIs.
 */
@Composable
fun AsyncYummiziImage(
    model: Any?,    // "String" and "File" are supported
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
) {
    // Construct the isolated request model attached cleanly to the local lifecycle context
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(model)
        .crossfade(true) // Enforces premium visual transitions globally
        .build()

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        alignment = alignment
    )
}