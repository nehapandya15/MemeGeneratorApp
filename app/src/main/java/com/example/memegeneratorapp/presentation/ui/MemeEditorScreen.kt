package com.example.memegeneratorapp.presentation.ui

import android.graphics.ImageDecoder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.example.memegeneratorapp.presentation.viewmodel.MemeViewModel

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MemeEditorScreen(viewModel: MemeViewModel) {
    val context = LocalContext.current
    var showTextDialog by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
            viewModel.onImageSelected(uri, bitmap.asImageBitmap())
        }
    }

    MemeEditorContent(
        imageBitmap = viewModel.imageBitmap,
        memeTexts = viewModel.memeTexts,
        onTextPositionChanged = viewModel::onTextPositionChanged,
        onAddText = viewModel::addMemeText,
        onSelectImage = { launcher.launch("image/*") },
        onShowTextDialog = { showTextDialog = true },
        showTextDialog = showTextDialog,
        onDismissTextDialog = { showTextDialog = false },
        onSaveClicked = { viewModel.onSaveClicked(context, viewModel.imageBitmap ?: return@MemeEditorContent) },
        onShareClicked = { viewModel.onShareClicked(context, viewModel.imageBitmap ?: return@MemeEditorContent) },
        isSaving = viewModel.isSaving,
        saveResult = viewModel.saveResult,
        onSaveResultConsumed = { viewModel.resetSaveResult() }
    )
}