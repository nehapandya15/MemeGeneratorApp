package com.example.memegeneratorapp.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.memegeneratorapp.R

@Composable
fun EditorActions(
    onSelectImage: () -> Unit,
    onAddText: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit,
    isImageSelected: Boolean
) {
    val disabledAlpha = 0.4f
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FloatingActionButton(onClick = onSelectImage) {
            Icon(
                painter = painterResource(id = R.drawable.image),
                contentDescription = "Select Image",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }

        FloatingActionButton(
            onClick = {
                if (isImageSelected) onAddText()
            },
            containerColor = if (isImageSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                alpha = disabledAlpha
            ),
            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
        ) {
            Icon(
                Icons.Default.Add, contentDescription = "Add Text",
                tint = if (isImageSelected) Color.White else Color.White.copy(alpha = disabledAlpha)
            )
        }

        FloatingActionButton(
            onClick = {
                if (isImageSelected) onSave()
            },
            containerColor = if (isImageSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                alpha = disabledAlpha
            ),
            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.download),
                contentDescription = "Save Meme",
                modifier = Modifier.size(24.dp),
                tint = if (isImageSelected) Color.White else Color.White.copy(alpha = disabledAlpha)
            )
        }

        FloatingActionButton(
            onClick = {
                if (isImageSelected) onShare()
            },
            containerColor = if (isImageSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                alpha = disabledAlpha
            ),
            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
        ) {
            Icon(
                Icons.Default.Share,
                contentDescription = "Share Meme",
                tint = if (isImageSelected) Color.White else Color.White.copy(alpha = disabledAlpha)
            )
        }
    }
}
