package com.example.memegeneratorapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.memegeneratorapp.domain.repository.MemeRepository
import com.example.memegeneratorapp.presentation.utils.saveBitmapToPictures
import com.example.memegeneratorapp.presentation.utils.shareImageUri
import javax.inject.Inject

class MemeRepositoryImpl @Inject constructor() : MemeRepository {
    override suspend fun saveToPictures(bitmap: Bitmap, context: Context): Uri? {
        return saveBitmapToPictures(context, bitmap)
    }

    override suspend fun shareMeme(uri: Uri, context: Context) {
        shareImageUri(context, uri)
    }
}