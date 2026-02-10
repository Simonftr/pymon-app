package com.sae.pymon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sae.pymon.haptic.HapticManager
import com.sae.pymon.sound.SoundManager
import com.sae.pymon.ui.PymonApp
import com.sae.pymon.ui.theme.PymonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundManager.init(this)
        HapticManager.init(this)
        enableEdgeToEdge()
        setContent {
            PymonTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PymonApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
