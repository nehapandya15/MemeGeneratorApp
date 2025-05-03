package com.example.memegeneratorapp.presentation.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.content.FileProvider
import com.example.memegeneratorapp.domain.model.MemeText
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

fun captureComposableAsBitmap(view: View): Bitmap? {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val cachePath = File(context.cacheDir, "images")
    cachePath.mkdirs()
    val file = File(cachePath, "shared_meme.png")
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    outputStream.close()

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

fun createMemeBitmap(
    imageBitmap: ImageBitmap,
    memeTexts: List<MemeText>
): Bitmap {
    val width = imageBitmap.width
    val height = imageBitmap.height
    val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(resultBitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val androidBitmap = imageBitmap.asAndroidBitmap()
    canvas.drawBitmap(androidBitmap, 0f, 0f, null)

    memeTexts.forEach { memeText ->
        paint.color = memeText.color.toArgb()
        paint.textSize = memeText.fontSize
        paint.isFakeBoldText = memeText.style.fontWeight == FontWeight.Bold
        paint.textSkewX = if (memeText.style.fontStyle == FontStyle.Italic) -0.25f else 0f
        paint.isUnderlineText = memeText.style.textDecoration == TextDecoration.Underline
        paint.textAlign = when (memeText.alignment) {
            TextAlign.Center -> Paint.Align.CENTER
            TextAlign.Right -> Paint.Align.RIGHT
            else -> Paint.Align.LEFT
        }

        canvas.drawText(
            memeText.text,
            memeText.offset.x,
            memeText.offset.y,
            paint
        )
    }

    return resultBitmap
}
/*
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
}*/

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
            textSkewX = if (memeText.style?.fontStyle == androidx.compose.ui.text.font.FontStyle.Italic) -0.25f else 0f
            isUnderlineText = memeText.style?.textDecoration == androidx.compose.ui.text.style.TextDecoration.Underline
        }

        // Convert screen offset to original bitmap space
        val xRaw = (memeText.offset.x - offsetX) / scale
        val yRaw = (memeText.offset.y - offsetY) / scale

        // Measure text bounds
        val textBounds = Rect()
        paint.getTextBounds(memeText.text, 0, memeText.text.length, textBounds)
        val textWidth = textBounds.width().toFloat()
        val textHeight = textBounds.height().toFloat()

        // Adjust x based on alignment manually
        val x = when (memeText.alignment) {
            TextAlign.Left -> xRaw
            TextAlign.Center -> (xRaw - textWidth / 8f) + 12f
            TextAlign.Right -> xRaw - textWidth
            else -> xRaw
        }

        // Y baseline correction
        val y = yRaw + textHeight

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

fun convertToSoftwareBitmap(hardwareBitmap: Bitmap): Bitmap {
    // Create a software-rendered copy
    val softwareBitmap = Bitmap.createBitmap(
        hardwareBitmap.width,
        hardwareBitmap.height,
        Bitmap.Config.ARGB_8888 // Software-compatible config
    )
    val canvas = Canvas(softwareBitmap)
    canvas.drawBitmap(hardwareBitmap, 0f, 0f, null)
    return softwareBitmap
}