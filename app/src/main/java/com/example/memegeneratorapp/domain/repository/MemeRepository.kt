package com.example.memegeneratorapp.domain.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri

interface MemeRepository {
    suspend fun saveToPictures(bitmap: Bitmap, context: Context): Uri?
    suspend fun shareMeme(uri: Uri, context: Context)
}