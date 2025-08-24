package com.example.mobileapp2025.ui.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import kotlinx.coroutines.delay

@Composable
fun MediaSlider(controller: MediaController) {
    var sliderPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }

    // Listener del MediaController
    DisposableEffect(controller) {
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                duration = player.duration.coerceAtLeast(0L)
            }
        }
        controller?.addListener(listener)

        onDispose {
            controller?.removeListener(listener)
        }
    }

    //Actualización del progreso caa 500ms
    LaunchedEffect(controller) {
        while (true) {
            controller.let {
                sliderPosition = it.currentPosition
                duration = it.duration.coerceAtLeast(0L)
            }
            delay(500) // medio segundo
        }
    }

    Slider(
        value = sliderPosition.toFloat(),
        onValueChange = { newValue ->
            sliderPosition = newValue.toLong() // mover el thumb sin todavía hacer seek
        },
        onValueChangeFinished = {
            controller.seekTo(sliderPosition) // aplica el seek cuando se suelta
        },
        valueRange = 0f..duration.toFloat(),
        modifier = Modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = Color(0xFFFFD700), // dorado
            activeTrackColor = Color(0xFFFFD700), // dorado
            inactiveTrackColor = Color(0xFF444444), // gris oscuro
        )
    )
}