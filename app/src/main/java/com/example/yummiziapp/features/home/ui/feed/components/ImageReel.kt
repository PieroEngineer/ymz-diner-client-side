package com.example.yummiziapp.features.home.ui.feed.components

import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import coil3.compose.rememberAsyncImagePainter
import com.example.yummiziapp.core.designsystem.theme.AsyncYummiziImage

//@Preview
@Composable
fun ImageReel(
    imageUrls: List<String>,
    reelOn: Boolean = false
) {
    Log.d("MyActivity", "🍲  Links of the food images $imageUrls\n")
    val spacing = YummiziTheme.spacing

    if(reelOn && imageUrls.size>1){
        val painters = imageUrls.map { id ->
            rememberAsyncImagePainter(model = id)
        }
        val displayDurationMillis = 3000
        val slideDurationMillis = 800

        val infiniteTransition = rememberInfiniteTransition(label = "ReelTransition")

        // Single animation value to drive the offset
        val offsetFraction by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = painters.size.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = (displayDurationMillis + slideDurationMillis) * painters.size

                    // For each image, create a "Hold" and a "Slide" period
                    for (i in 0..painters.size) {
                        val startTime = i * (displayDurationMillis + slideDurationMillis)
                        i.toFloat() at startTime // Start holding at this index
                        i.toFloat() at startTime + displayDurationMillis // End holding, start sliding
                    }
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "OffsetFraction"
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds() // Crucial: Don't draw outside the container
                .drawBehind {
                    val imageWidth = size.width
                    val imageHeight = size.height
                    painters.forEachIndexed { index, painter ->
                        // Calculate relative position
                        var xOffset = (index - offsetFraction) * imageWidth

                        // SEAMLESS LOGIC:
                        // If an image moves too far to the left, teleport it to the right
                        // of the current viewing area to close the "white space" gap.
                        val totalWidth = painters.size * imageWidth
                        while (xOffset < -imageWidth) xOffset += totalWidth
                        while (xOffset > totalWidth - imageWidth) xOffset -= totalWidth

                        translate(left = xOffset) {
                            // Coil's painter automatically draws its loading/error
                            // state if the image hasn't arrived yet.
                            with(painter) {
                                draw(size = Size(imageWidth, imageHeight))
                            }
                        }
                    }
                }
        )
    }
    else{
        if (imageUrls.isNotEmpty()){
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                AsyncYummiziImage(
                    model = imageUrls[0],
                    contentDescription = null,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(spacing.mediumCornerRadius)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

}