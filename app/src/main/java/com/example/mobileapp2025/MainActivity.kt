package com.example.mobileapp2025

import android.Manifest
import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.mobileapp2025.ui.theme.MobileApp2025Theme
//import para leer archivo
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobileapp2025.model.GetSongs
import com.example.mobileapp2025.model.PlaylistViewModel
import java.io.File
//import clase propia
import com.example.mobileapp2025.model.Song
// import del viewmodel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Android 11+ requiere de revisión de permisos
        // manuales además de las declaraciones del manifest
        val permissions = if(Build.VERSION.SDK_INT >= 33) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO
            )
            } else {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        ActivityCompat.requestPermissions(
            this,
            permissions,
            0
        )

        setContent {
            MobileApp2025Theme {
                MainScreen()
            }
        }
    }
}

/*
@Composable
fun rememberPlayer(context: Context): ExoPlayer {
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }
    return player
}
*/

@Composable
fun MediaControlBar(playlistViewModel: PlaylistViewModel) {
    val player = playlistViewModel.player
    // var isPlaying by remember { mutableStateOf(player.isPlaying) }
    var isPlaying = playlistViewModel.isPlaying
    // cancíón actual si hay
    //val currentSong = playlistViewModel.playlist.getOrNull(player.currentMediaItemIndex)
    var currentSong = playlistViewModel.currentSong
    //Boton de atras
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        //Imagen de canción
        Column(
            modifier = Modifier
                .weight(1f)
                .background(Color.Transparent),
            verticalArrangement = Arrangement.Center
            ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                modifier = Modifier.size(100.dp),
                contentDescription = "Icono Cancion")
        }
        //Nombre Cancion & Artista
        Column(modifier = Modifier
            .weight(3f)
            .fillMaxWidth()
            .background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally

            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) { Text(currentSong?.titulo ?: "Sin titulo",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Left)}
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ){ Text(currentSong?.artista ?: "Artista desconocido",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Left)}
        }
        //Controles de Reproduccion
        Column(modifier = Modifier.weight(3f),
            horizontalAlignment = Alignment.End)
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                //atras
                Column (modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent),
                    verticalArrangement = Arrangement.Center){
                    IconButton(onClick = {
                        val hasPrevious = player.hasPreviousMediaItem()
                        if (hasPrevious) {
                            player.seekToPreviousMediaItem()
                            player.play()
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.SkipPrevious, contentDescription = "Anterior")
                    }
                }
                //reproducir o pausar
                Column (modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Transparent),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    //Boton de Pausar/Reproducir intercambiable
                    IconButton(onClick = {
                        if (isPlaying) {player.pause()}
                        else {player.play()}
                        isPlaying = !isPlaying
                    }) {
                        Icon(imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pausar" else "Reproducir")
                    }
                }
                //adelante
                Column (modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Transparent),
                    horizontalAlignment = Alignment.End) {
                    //Boton de Adelante
                    IconButton(onClick = {
                        val hasNext = player.hasNextMediaItem()
                        if (hasNext) {
                            player.seekToNextMediaItem()
                            player.play()
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.SkipNext, contentDescription = "Posterior")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val playlistViewModel: PlaylistViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            context.applicationContext as Application
        )
    )

    //inicializador del Exoplayer
    // val player = rememberPlayer(context)
    //Listado de canciones del Dispositivo
    //var songs by remember {mutableStateOf<List<Song>>(emptyList())}
    // LaunchedEffect(Unit) {
    //    songs = GetSongs(context)
    //    Log.d("DEBUG", "Canciones encontradas: ${songs.size}")
    //}
    //Listado de cancioens en la cola de reproducción.
    //var playlist by remember {mutableStateOf<List<Song>>(emptyList())}
    //reproducir cancion seleccionada
    /*
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    LaunchedEffect(selectedSong) {
        selectedSong?.let { thisSong ->
            val mediaItem = MediaItem.fromUri(thisSong.uri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }
    */
    /*
    SongList(
        songs = playlistViewModel.songs,
        onSongSelected = { song -> playlistViewModel.playNow(song)}
    )
    */
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.LightGray
    ) {
        Scaffold (
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(title = {Text("Music Player")},
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Gray
                    )
                )
            },
            bottomBar = {
                MediaControlBar(playlistViewModel)
            }
        )
        {
                padding ->
            Column(modifier = Modifier.padding(padding)) {
                SongList(
                    songs = playlistViewModel.songs,
                    currentSong = playlistViewModel.playlist.getOrNull(playlistViewModel.player.currentMediaItemIndex),
                    onSongSelected = {song -> playlistViewModel.playNow(song) }
                )
            }
        }
    }
}

@Composable
fun SongList(songs: List<Song>, currentSong: Song?, onSongSelected: (Song) -> Unit) {
    LazyColumn {
        items(
            items = songs,
            key = {it.id}
        ) { song ->
            val isCurrent = song == currentSong
            SongItem(
                song = song,
                onClick = {onSongSelected(song)},
                isPlaying = isCurrent
                )
        }
    }
}
@Composable
fun SongItem(song: Song, onClick: () -> Unit, isPlaying: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isPlaying) Color.Black.copy(alpha=0.2f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(text = song.titulo, style = MaterialTheme.typography.titleMedium)
        Text(text = song.artista, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

