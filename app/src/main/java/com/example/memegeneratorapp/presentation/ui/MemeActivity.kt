package com.example.memegeneratorapp.presentation.ui

import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.memegeneratorapp.domain.model.MemeText
import com.example.memegeneratorapp.presentation.viewmodel.MemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class MemeActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemeEditorScreen()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MemeEditorScreen(viewModel: MemeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var showTextDialog by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source).asImageBitmap()
            viewModel.onImageSelected(uri, bitmap)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            viewModel.imageBitmap?.let { bitmap ->
                val canvasWidth = size.width
                val canvasHeight = size.height
                val imageWidth = bitmap.width.toFloat()
                val imageHeight = bitmap.height.toFloat()

                val scale: Float
                val drawOffsetX: Float
                val drawOffsetY: Float

                if (imageWidth / imageHeight > canvasWidth / canvasHeight) {
                    // Image is wider than canvas; crop horizontally
                    scale = canvasHeight / imageHeight
                } else {
                    // Image is taller than canvas; crop vertically or aspect ratios are equal
                    scale = canvasWidth / imageWidth
                }

                val scaledWidth = imageWidth * scale
                val scaledHeight = imageHeight * scale
                drawOffsetX = (canvasWidth - scaledWidth) / 2f
                drawOffsetY = (canvasHeight - scaledHeight) / 2f
                val topLeft = IntOffset(Math.round(drawOffsetX), Math.round(drawOffsetY))
                drawImage(
                    image = bitmap,
                    srcOffset = IntOffset.Zero,
                    srcSize = IntSize(bitmap.width, bitmap.height),
                    dstOffset = topLeft,
                    dstSize = IntSize(scaledWidth.toInt(), scaledHeight.toInt()),
                    alpha = 1.0f,
                    style = Fill,
                    colorFilter = null,
                    blendMode = BlendMode.SrcIn,
                    filterQuality = FilterQuality.Low
                )
            }

        }
        viewModel.memeTexts.forEachIndexed { index, memeText ->
            DraggableMemeText(
                memeText = memeText,
                onPositionChange = { newOffset ->
                    viewModel.updateTextPosition(index, newOffset)
                }
            )
        }

        // FABs to select image and add text
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FloatingActionButton(onClick = { launcher.launch("image/*") }) {
                Icon(Icons.Default.Face, contentDescription = "Select Image")
            }
            FloatingActionButton(
                onClick = { showTextDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Text")
            }

        }

        // Text Input Dialog
        if (showTextDialog) {
            var fontSize by remember { mutableStateOf(64f) }
            var selectedColor by remember { mutableStateOf(Color.White) }
            var isBold by remember { mutableStateOf(true) }
            var isItalic by remember { mutableStateOf(false) }
            var isUnderlined by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showTextDialog = false },
                title = { Text("Add Meme Text") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            label = { Text("Enter Text") }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Font Size Slider
                        Text("Font Size: ${fontSize.toInt()}")
                        Slider(
                            value = fontSize,
                            onValueChange = { fontSize = it },
                            valueRange = 16f..128f
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Font Color Picker (predefined)
                        Text("Select Color")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(
                                listOf(
                                    Color.White,
                                    Color.Black,
                                    Color.Red,
                                    Color.Blue,
                                    Color.Green,
                                    Color.Yellow,
                                    Color.Magenta,
                                    Color.Cyan,
                                    Color.Gray,
                                    Color.LightGray,
                                    Color.DarkGray,
                                    Color(0xFFFFA500) // orange
                                )
                            ) { color ->
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(color, shape = CircleShape)
                                        .border(
                                            width = 2.dp,
                                            color = if (selectedColor == color) Color.Gray else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColor = color }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Style Toggles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column {
                                Text("Bold", Modifier.align(Alignment.CenterHorizontally))
                                Switch(
                                    checked = isBold,
                                    onCheckedChange = { isBold = it },
                                    Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                            Column {
                                Text("Italic", Modifier.align(Alignment.CenterHorizontally))
                                Switch(
                                    checked = isItalic,
                                    onCheckedChange = { isItalic = it },
                                    Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                            Column {
                                Text("Underline")
                                Switch(
                                    checked = isUnderlined,
                                    onCheckedChange = { isUnderlined = it },
                                    Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (inputText.isNotBlank()) {
                            val canvasWidth = context.resources.displayMetrics.widthPixels
                            val canvasHeight = context.resources.displayMetrics.heightPixels
                            val fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
                            val fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal
                            val textDecoration =
                                if (isUnderlined) TextDecoration.Underline else TextDecoration.None

                            viewModel.addText(
                                MemeText(
                                    text = inputText,
                                    fontSize = fontSize,
                                    color = selectedColor,
                                    style = TextStyle(
                                        fontWeight = fontWeight,
                                        fontStyle = fontStyle,
                                        textDecoration = textDecoration
                                    ),
                                    offset = Offset(
                                        canvasWidth.toFloat(),
                                        canvasHeight.toFloat()
                                    ),
                                    alignment = TextAlign.Center
                                )
                            )
                            inputText = ""
                            showTextDialog = false
                        }
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTextDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DraggableMemeText(
    memeText: MemeText,
    onPositionChange: (Offset) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()

    val measuredText = textMeasurer.measure(
        text = AnnotatedString(memeText.text),
        style = TextStyle(
            fontSize = memeText.fontSize.toInt().sp,
            fontWeight = memeText.style.fontWeight,
            fontStyle = memeText.style.fontStyle,
            textDecoration = memeText.style.textDecoration
        )
    )

    val textWidth = measuredText.size.width.toFloat()
    val textHeight = measuredText.size.height.toFloat()

    var offset by remember {
        mutableStateOf(
            Offset(
                (memeText.offset.x - textWidth) / 2f,
                (memeText.offset.y - textHeight) / 2f
            )
        )
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newOffset = offset + dragAmount

                    // Clamp horizontal movement
                    val clampedX = newOffset.x.coerceIn(0f, memeText.offset.x - textWidth)
                    val clampedY = newOffset.y.coerceIn(0f, memeText.offset.y - textHeight)

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
