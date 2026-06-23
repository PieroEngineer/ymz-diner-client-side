package com.example.yummiziapp.core.designsystem.theme

import androidx.compose.runtime.Composable
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
fun YummiziImage(
    model: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
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
        contentScale = contentScale
        // Future global rules (like placeholder = painterResource(...) or error fallback states)
        // can be easily assigned right here down the road!
    )
}