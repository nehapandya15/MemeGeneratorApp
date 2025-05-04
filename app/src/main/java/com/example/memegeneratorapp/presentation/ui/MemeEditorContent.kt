package com.example.memegeneratorapp.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.memegeneratorapp.domain.model.MemeText
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MemeEditorContent(
    imageBitmap: ImageBitmap?,
    memeTexts: StateFlow<List<MemeText>>,
    onTextPositionChanged: (Int, Offset) -> Unit,
    onAddText: (MemeText) -> Unit,
    onSelectImage: () -> Unit,
    onShowTextDialog: () -> Unit,
    onDismissTextDialog: () -> Unit,
    showTextDialog: Boolean,
    onSaveClicked: () -> Unit,
    onShareClicked: () -> Unit,
    isSaving: StateFlow<Boolean>,
    saveResult: StateFlow<Boolean?>,
    onSaveResultConsumed: () -> Unit
) {
    val context = LocalContext.current
    val memeTextList by memeTexts.collectAsState()
    val saving by isSaving.collectAsState()
    val saveStatus by saveResult.collectAsState()

    var scaledWidth by remember { mutableStateOf(0f) }
    var scaledHeight by remember { mutableStateOf(0f) }
    var drawOffsetX by remember { mutableStateOf(0f) }
    var drawOffsetY by remember { mutableStateOf(0f) }

    // Show save result toast
    saveStatus?.let { success ->
        LaunchedEffect(success) {
            Toast.makeText(
                context,
                if (success) "Meme saved!" else "Failed to save meme.",
                Toast.LENGTH_SHORT
            ).show()
            onSaveResultConsumed()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Draw image
        Canvas(modifier = Modifier.fillMaxSize()) {
            imageBitmap?.let { bitmap ->
                val canvasSize = size
                val scale =
                    minOf(canvasSize.width / bitmap.width, canvasSize.height / bitmap.height)
                scaledWidth = bitmap.width * scale
                scaledHeight = bitmap.height * scale
                drawOffsetX = (canvasSize.width - scaledWidth) / 2f
                drawOffsetY = (canvasSize.height - scaledHeight) / 2f

                drawImage(
                    image = bitmap,
                    dstOffset = IntOffset(drawOffsetX.toInt(), drawOffsetY.toInt()),
                    dstSize = IntSize(scaledWidth.toInt(), scaledHeight.toInt())
                )
            }
        }

        val imageOffset = Offset(drawOffsetX, drawOffsetY)
        val imageSize = Size(scaledWidth, scaledHeight)

        // Render draggable texts
        memeTextList.forEachIndexed { index, memeText ->
            DraggableMemeText(
                memeText = memeText,
                imageOffset = imageOffset,
                imageSize = imageSize,
                onPositionChange = { newOffset -> onTextPositionChanged(index, newOffset) }
            )
        }

        // Editor action buttons
        EditorActions(
            onSelectImage = onSelectImage,
            onAddText = onShowTextDialog,
            onSave = onSaveClicked,
            onShare = onShareClicked
        )

        // Loading indicator while saving
        if (saving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Add text dialog
        if (showTextDialog) {
            MemeTextDialog(
                onAdd = {
                    onAddText(it)
                    onDismissTextDialog()
                },
                onDismiss = onDismissTextDialog
            )
        }
    }
}
