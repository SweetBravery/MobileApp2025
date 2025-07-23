package com.example.mobileapp2025

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.example.mobileapp2025.ui.theme.MobileApp2025Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileApp2025Theme {
                MainScreen()
            }
        }
    }
}

@Composable
fun rememberPlayer(context: Context, uri:Uri): ExoPlayer {
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            prepare()
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }
    return player
}

@Composable
fun MediaControlBar(onPlay: ()->Unit, onPause: ()->Unit) {
    var isPlaying by remember { mutableStateOf(false) }
    //Boton de atras
    IconButton(onClick = {}) {
        Icon(imageVector = Icons.Filled.SkipPrevious, contentDescription = "Anterior")
    }
    //Boton de Pausar/Reproducir intercambiable
    IconButton(onClick = {
        if (isPlaying) {onPause()}
        else {onPlay()}
        isPlaying = !isPlaying
    }) {
        Icon(imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pausar" else "Reproducir")
    }
    //Boton de Adelante
    IconButton(onClick = {}) {
        Icon(imageVector = Icons.Filled.SkipNext, contentDescription = "Posterior")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val uri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3".toUri()
    val player = rememberPlayer(context, uri)

    Scaffold (
        topBar = {
            TopAppBar(title = {Text("Music Player")})
    },
        bottomBar = {
            MediaControlBar(
                onPlay = {player.play()},
                onPause = {player.pause()}
            )
        }
    )
    {
        padding ->
        Column(modifier = Modifier.padding(padding)) {
            PlayerScreen(player = player)
        }
    }
}

@Composable
fun PlayerScreen (player: ExoPlayer) {

}
