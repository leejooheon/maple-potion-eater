package com.jooheon.maple.potion

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "maple-potion") {
        App(window)
    }
}