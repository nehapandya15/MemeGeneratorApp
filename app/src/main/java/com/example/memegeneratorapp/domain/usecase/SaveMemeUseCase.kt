package com.example.memegeneratorapp.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import com.example.memegeneratorapp.domain.model.MemeText
import com.example.memegeneratorapp.domain.repository.MemeRepository
import com.example.memegeneratorapp.presentation.utils.createMemeBitmap
import javax.inject.Inject

class SaveMemeUseCase @Inject constructor(
    private val memeRepository: MemeRepository
) {
    suspend operator fun invoke(
        imageBitmap: ImageBitmap,
        memeTexts: List<MemeText>,
        context: Context
    ): Uri? {
        val bitmap = createMemeBitmap(imageBitmap, memeTexts, context)
        return memeRepository.saveToPictures(bitmap, context)
    }
}