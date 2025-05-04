package com.example.memegeneratorapp.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditorActions(
    onSelectImage: () -> Unit,
    onAddText: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FloatingActionButton(onClick = onSelectImage) {
            Icon(Icons.Default.Face, contentDescription = "Select Image")
        }

        FloatingActionButton(onClick = onAddText) {
            Icon(Icons.Default.Add, contentDescription = "Add Text")
        }

        FloatingActionButton(onClick = onSave) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Save Meme")
        }

        FloatingActionButton(onClick = onShare) {
            Icon(Icons.Default.Share, contentDescription = "Share Meme")
        }
    }
}
