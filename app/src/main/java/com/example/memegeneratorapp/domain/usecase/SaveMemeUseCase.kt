package com.example.memegeneratorapp.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.memegeneratorapp.domain.repository.MemeRepository

class SaveMemeUseCase(private val repository: MemeRepository) {
    suspend operator fun invoke(bitmap: Bitmap): Uri = repository.saveMeme(bitmap)
}