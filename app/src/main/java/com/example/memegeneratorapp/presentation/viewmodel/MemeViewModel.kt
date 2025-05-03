package com.example.memegeneratorapp.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.example.memegeneratorapp.domain.model.MemeText
import com.example.memegeneratorapp.domain.usecase.SaveMemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MemeViewModel @Inject constructor(private val saveMemeUseCase: SaveMemeUseCase) : ViewModel() {


    var selectedImageUri by mutableStateOf<Uri?>(null)
    var imageBitmap by mutableStateOf<ImageBitmap?>(null)

    var imageScale by mutableStateOf(1f)
    var imageOffset by mutableStateOf(Offset(0f, 0f))

    var memeTexts by mutableStateOf(listOf<MemeText>())

    fun onImageSelected(uri: Uri, bitmap: ImageBitmap) {
        selectedImageUri = uri
        imageBitmap = bitmap
        imageOffset = Offset(0f, 0f)
        imageScale = 1f
    }

    fun addText(text: MemeText) {
        memeTexts = memeTexts + text
    }

    fun updateText(index: Int, newText: MemeText) {
        memeTexts = memeTexts.toMutableList().apply { this[index] = newText }
    }

    fun updateTextPosition(index: Int, newOffset: Offset) {
        memeTexts = memeTexts.mapIndexed { i, text ->
            if (i == index) text.copy(offset = newOffset) else text
        }
    }
}

