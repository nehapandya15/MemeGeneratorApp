package com.example.memegeneratorapp.presentation.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemeActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemeEditorApp()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MemeEditorApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MemeEditorScreen(hiltViewModel())
        }
    }
}