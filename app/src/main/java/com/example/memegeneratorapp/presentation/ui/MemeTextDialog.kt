package com.example.memegeneratorapp.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.memegeneratorapp.domain.model.MemeText

@Composable
fun MemeTextDialog(
    onAdd: (MemeText) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var fontSize by remember { mutableStateOf(64f) }
    var selectedColor by remember { mutableStateOf(Color.White) }
    var isBold by remember { mutableStateOf(true) }
    var isItalic by remember { mutableStateOf(false) }
    var isUnderlined by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Meme Text") },
        text = {
            Column {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Text") }
                )

                Spacer(Modifier.height(8.dp))

                Text("Font Size: ${fontSize.toInt()}")
                Slider(value = fontSize, onValueChange = { fontSize = it }, valueRange = 16f..128f)

                Spacer(Modifier.height(8.dp))

                Text("Color")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(
                        listOf(
                            Color.White, Color.Black, Color.Red, Color.Blue, Color.Green,
                            Color.Yellow, Color.Magenta, Color.Cyan, Color.Gray, Color.LightGray, Color.DarkGray,
                            Color(0xFFFFA500)
                        )
                    ) { color ->
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(color, CircleShape)
                                .border(2.dp, if (selectedColor == color) Color.Gray else Color.Transparent, CircleShape)
                                .clickable { selectedColor = color }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    SwitchWithLabel("Bold", isBold) { isBold = it }
                    SwitchWithLabel("Italic", isItalic) { isItalic = it }
                    SwitchWithLabel("Underline", isUnderlined) { isUnderlined = it }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (inputText.isNotBlank()) {
                    val canvasWidth = context.resources.displayMetrics.widthPixels
                    val canvasHeight = context.resources.displayMetrics.heightPixels
                    onAdd(
                        MemeText(
                            text = inputText,
                            fontSize = fontSize,
                            color = selectedColor,
                            style = TextStyle(
                                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                                fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                                textDecoration = if (isUnderlined) TextDecoration.Underline else TextDecoration.None
                            ),
                            offset = Offset(canvasWidth / 2f, canvasHeight / 2f),
                            alignment = TextAlign.Center
                        )
                    )
                    inputText = ""
                    onDismiss()
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SwitchWithLabel(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
