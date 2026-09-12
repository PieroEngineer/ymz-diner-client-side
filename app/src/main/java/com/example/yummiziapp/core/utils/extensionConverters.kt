package com.example.yummiziapp.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import java.io.File
import java.io.FileOutputStream

/**
 * Converts an image from a [Uri] to WebP format and saves it to a [File].
 * Uses [ImageDecoder] for high-performance decoding on API 28+ and [BitmapFactory] as fallback.
 *
 * @param context The application context.
 * @param targetFile The file where the WebP image will be saved.
 * @param quality Compression quality (0-100). Default is 80.
 */
fun Uri.convertToWebp(
    context: Context,
    targetFile: File,
    quality: Int = 80
) {
    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, this)
        ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            // Use software allocator to ensure compatibility with Bitmap.compress
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            
            // Limit resolution to a reasonable size for a payment receipt (e.g., max 2048px)
            val maxSide = 2048
            if (info.size.width > maxSide || info.size.height > maxSide) {
                val ratio = info.size.width.toFloat() / info.size.height.toFloat()
                if (ratio > 1) {
                    decoder.setTargetSize(maxSide, (maxSide / ratio).toInt())
                } else {
                    decoder.setTargetSize((maxSide * ratio).toInt(), maxSide)
                }
            }
        }
    } else {
        context.contentResolver.openInputStream(this).use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        } ?: throw IllegalStateException("Could not decode bitmap from Uri: $this")
    }

    FileOutputStream(targetFile).use { out ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, quality, out)
        } else {
            @Suppress("DEPRECATION")
            bitmap.compress(Bitmap.CompressFormat.WEBP, quality, out)
        }
    }
    bitmap.recycle()
}
