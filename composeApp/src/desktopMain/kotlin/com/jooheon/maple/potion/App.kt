package com.jooheon.maple.potion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jooheon.maple.potion.main.MainScreen
import java.awt.Window
import javax.swing.UIManager

@Composable
fun App(window: Window) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            MainScreen(window)
        }
    }
}