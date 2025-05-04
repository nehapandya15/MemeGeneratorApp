package com.example.memegeneratorapp.presentation.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.text.style.TextAlign
import com.example.memegeneratorapp.domain.model.MemeText
import kotlin.math.min

fun createMemeBitmap(
    imageBitmap: androidx.compose.ui.graphics.ImageBitmap,
    memeTexts: List<MemeText>,
    context: Context
): Bitmap {
    val width = imageBitmap.width
    val height = imageBitmap.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Draw the original image
    val nativeBitmap = imageBitmap.asAndroidBitmap()
    canvas.drawBitmap(nativeBitmap, 0f, 0f, null)

    val screenWidth = context.resources.displayMetrics.widthPixels.toFloat()
    val screenHeight = context.resources.displayMetrics.heightPixels.toFloat()
    val density = context.resources.displayMetrics.density

    val scale = min(screenWidth / width.toFloat(), screenHeight / height.toFloat())
    val offsetX = (screenWidth - width * scale) / 2f
    val offsetY = (screenHeight - height * scale) / 2f

    memeTexts.forEach { memeText ->
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = memeText.color.hashCode()
            textSize = memeText.fontSize * density / scale
            isFakeBoldText = memeText.style?.fontWeight?.weight ?: 400 > 500
            textSkewX =
                if (memeText.style?.fontStyle == androidx.compose.ui.text.font.FontStyle.Italic) -0.25f else 0f
            isUnderlineText =
                memeText.style?.textDecoration == androidx.compose.ui.text.style.TextDecoration.Underline
            textAlign = when (memeText.alignment) {
                TextAlign.Left -> Paint.Align.LEFT
                TextAlign.Center -> Paint.Align.CENTER
                TextAlign.Right -> Paint.Align.RIGHT
                else -> Paint.Align.LEFT
            }
        }

        // Convert screen offset to original bitmap space
        val xRaw = (memeText.offset.x - offsetX) / scale
        val yRaw = (memeText.offset.y - offsetY) / scale

        // Measure text bounds to get the top of the text
        val textBounds = Rect()
        paint.getTextBounds(memeText.text, 0, memeText.text.length, textBounds)
        val textHeight = textBounds.height().toFloat()
        val textWidth = textBounds.width().toFloat()
        val textTop = -textBounds.top.toFloat() // Distance from baseline to the top of the text
        val textBottom =
            -textBounds.bottom.toFloat() // Distance from baseline to the top of the text

        // Calculate the x coordinate based on alignment
        val x = when (memeText.alignment) {
            TextAlign.Left -> xRaw
            TextAlign.Center -> xRaw + textWidth / 2f
            TextAlign.Right -> xRaw
            else -> xRaw
        }

        // Position the top of the text at yRaw
        val y = (yRaw + textTop / 2f - textBottom / 2f + textHeight / 2f) - 1.5f

        canvas.drawText(memeText.text, x, y, paint)
    }
    return bitmap
}


fun saveBitmapToPictures(context: Context, bitmap: Bitmap): Uri? {
    val filename = "meme_${System.currentTimeMillis()}.png"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Memes")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        resolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }

        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)
    }

    return uri
}

fun shareImageUri(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Meme"))
}
