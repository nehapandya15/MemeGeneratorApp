package com.example.memegeneratorapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.memegeneratorapp.domain.repository.MemeRepository

class MemeRepositoryImpl(private val context: Context
): MemeRepository {
    override suspend fun saveMeme(bitmap: Bitmap): Uri {
        // Save image to cache or MediaStore and return URI
        return TODO("Provide the return value")
    }
}