package com.example.memegeneratorapp.presentation.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.example.memegeneratorapp.domain.model.MemeText
import kotlin.math.roundToInt

@Composable
fun DraggableMemeText(
    memeText: MemeText,
    imageOffset: Offset,
    imageSize: Size,
    onPositionChange: (Offset) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val measured = textMeasurer.measure(
        text = AnnotatedString(memeText.text),
        style = memeText.style.copy(fontSize = memeText.fontSize.sp)
    )
    val textWidth = measured.size.width.toFloat()
    val textHeight = measured.size.height.toFloat()

    val initialOffset = remember {
        Offset(
            (memeText.offset.x - textWidth / 2f),
            (memeText.offset.y - textHeight / 2f)
        )
    }

    var offset by remember { mutableStateOf(initialOffset) }

    LaunchedEffect(Unit) {
        onPositionChange(offset)
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newOffset = offset + dragAmount

                    val clampedX = newOffset.x.coerceIn(imageOffset.x, imageOffset.x + imageSize.width - textWidth)
                    val clampedY = newOffset.y.coerceIn(imageOffset.y, imageOffset.y + imageSize.height - textHeight)

                    offset = Offset(clampedX, clampedY)
                    onPositionChange(offset)
                }
            }
    ) {
        Text(
            text = memeText.text,
            color = memeText.color,
            fontSize = memeText.fontSize.sp,
            style = memeText.style,
            textAlign = memeText.alignment
        )
    }
}
