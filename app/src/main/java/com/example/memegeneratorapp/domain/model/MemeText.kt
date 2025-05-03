package com.example.memegeneratorapp.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign


data class MemeText(val text: String,
                    val fontSize: Float,
                    val color: Color,
                    val style: TextStyle,
                    val offset: Offset,
                    val alignment: TextAlign = TextAlign.Center
)
