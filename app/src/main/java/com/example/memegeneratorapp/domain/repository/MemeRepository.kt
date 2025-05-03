package com.example.memegeneratorapp.domain.repository

import android.graphics.Bitmap
import android.net.Uri

interface MemeRepository {
    suspend fun saveMeme(bitmap: Bitmap): Uri
}