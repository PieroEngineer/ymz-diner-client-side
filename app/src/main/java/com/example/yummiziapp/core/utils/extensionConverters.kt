package com.example.yummiziapp.core.utils

/// Analyze if their  importations are heavy

///**
// * Chooses the optimal WebP compression format based on Android SDK version.
// * Android 11 (API 30+) introduced explicit WEBP_LOSSY / WEBP_LOSSLESS constants.
// */
//fun getWebpCompressFormat(): Bitmap.CompressFormat {
//    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//        Bitmap.CompressFormat.WEBP_LOSSY
//    } else {
//        @Suppress("DEPRECATION")
//        Bitmap.CompressFormat.WEBP
//    }
//}
//
///**
// * Safely decodes an InputStream into a Bitmap while downsampling
// * to avoid OutOfMemory Errors on ultra-high-resolution photos.
// */
//fun decodeBitmapWithSampling(
//    inputStream: InputStream,
//    maxWidth: Int,
//    maxHeight: Int
//): Bitmap {
//    val bytes = inputStream.readBytes()
//
//    // First pass: Read dimensions only (zero memory allocation)
//    val options = BitmapFactory.Options().apply {
//        inJustDecodeBounds = true
//    }
//    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
//
//    // Calculate sample size factor
//    options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
//    options.inJustDecodeBounds = false
//
//    // Second pass: Decode downsampled bitmap into RAM
//    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
//        ?: throw IllegalStateException("Bitmap decoding failed.")
//}
//
//fun calculateInSampleSize(
//    options: BitmapFactory.Options,
//    reqWidth: Int,
//    reqHeight: Int
//): Int {
//    val (height: Int, width: Int) = options.outHeight to options.outWidth
//    var inSampleSize = 1
//
//    if (height > reqHeight || width > reqWidth) {
//        val halfHeight: Int = height / 2
//        val halfWidth: Int = width / 2
//
//        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
//            inSampleSize *= 2
//        }
//    }
//    return inSampleSize
//}
