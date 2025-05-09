package com.example.memegeneratorapp.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memegeneratorapp.domain.model.MemeText
import com.example.memegeneratorapp.domain.repository.MemeRepository
import com.example.memegeneratorapp.domain.usecase.SaveMemeUseCase
import com.example.memegeneratorapp.presentation.utils.shareImageUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MemeViewModel @Inject constructor(
    private val saveMemeUseCase: SaveMemeUseCase
) : ViewModel() {

    var selectedImageUri by mutableStateOf<Uri?>(null)
    var imageBitmap by mutableStateOf<ImageBitmap?>(null)
//    var memeTexts by mutableStateOf(listOf<MemeText>())

    // MutableStateFlows to track UI states
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> get() = _isSaving

    private val _saveResult = MutableStateFlow<Boolean?>(null) // Nullable for initial empty state
    val saveResult: StateFlow<Boolean?> get() = _saveResult

    private val _memeTexts = MutableStateFlow<List<MemeText>>(emptyList())
    val memeTexts: StateFlow<List<MemeText>> get() = _memeTexts

    // Actions to trigger UI changes
    fun onTextPositionChanged(index: Int, offset: Offset) {
        _memeTexts.value = _memeTexts.value.toMutableList().apply {
            this[index] = this[index].copy(offset = offset)
        }
    }

    fun addMemeText(newText: MemeText) {
        _memeTexts.value = _memeTexts.value + newText
    }

    fun onSaveClicked(context: Context, bitmap: ImageBitmap) {
        _isSaving.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Create the meme bitmap with the text overlays
                val uri = saveMemeUseCase(imageBitmap!!,  _memeTexts.value, context)
                withContext(Dispatchers.Main) {
                    _saveResult.value = uri != null
                }
            } catch (e: Exception) {
                // In case of failure, handle gracefully
                withContext(Dispatchers.Main) {
                    _saveResult.value = false
                }
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun onShareClicked(context: Context, bitmap: ImageBitmap) {
        _isSaving.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Create the meme bitmap with the text overlays and share
                val uri = saveMemeUseCase(imageBitmap!!,  _memeTexts.value, context)
                withContext(Dispatchers.Main) {
                    if (uri != null) {
                        shareImageUri(context, uri)
                    }
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun resetSaveResult() {
        _saveResult.value = null
    }

    fun onImageSelected(uri: Uri, bitmap: ImageBitmap) {
        selectedImageUri = uri
        imageBitmap = bitmap
    }

}

