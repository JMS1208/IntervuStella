package com.capstone.Capstone2Project.utils.extensions

import android.graphics.Bitmap
import android.media.Image
import java.nio.ByteBuffer

fun Image.toBitmap(): Bitmap {
//    val buffer = this.planes[0].buffer
//
//    val bytes = ByteArray(buffer.capacity())
//
//    buffer.get(bytes)
//
//    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)

    val image = this

    val width = image.width
    val height = image.height
    val pixelStride = image.planes[0].pixelStride
    val rowStride = image.planes[0].rowStride
    val bufferSize = rowStride * (height / pixelStride) + pixelStride - 1
    val buffer = ByteBuffer.allocate(bufferSize)
    image.planes[0].buffer[buffer.array()]
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.copyPixelsFromBuffer(buffer)

    return bitmap
}