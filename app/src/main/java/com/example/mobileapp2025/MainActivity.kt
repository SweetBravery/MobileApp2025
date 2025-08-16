package com.example.mobileapp2025

//import para leer archivo
//import clase propia
// import del viewmodel
import android.Manifest
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.mobileapp2025.model.GetSongs
import com.example.mobileapp2025.model.Song
import com.example.mobileapp2025.ui.theme.MobileApp2025Theme
import com.google.common.util.concurrent.MoreExecutors

class MainActivity : ComponentActivity() {
    //var para asignar Mediacontroller
    lateinit var controller: MediaController
    // variable para controlar la carga de controlador
    private val controllerReady = mutableStateOf(false)

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
                if (controllerReady.value) {
                    MainScreen(controller = controller)
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //Implementación de conexión al Service mediante cliente con token

        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()

        controllerFuture.addListener(
            {
                controller = controllerFuture.get()
                controllerReady.value = true
                // controller.play()
            },
            MoreExecutors.directExecutor()
        )
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
fun MediaControlBar(controller: MediaController, songs:List<Song>) {
    var isPlaying = remember { mutableStateOf(controller.isPlaying) }
    val currentSong = remember { mutableStateOf<Song?>(null) }
    //Registro de Listeners
    //Este launchedeffect permite lanzar la canción individual y ponerla como un mediaItem
    LaunchedEffect(controller) {
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                isPlaying.value = isPlayingValue
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentSong.value = mediaItem?.let { item ->
                    songs.find { song -> song.uri == item.localConfiguration?.uri }
                }
            }
        })
    }
    //Elementos UI de compose
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
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
                modifier = Modifier.size(64.dp),
                contentDescription = "Icono Cancion")
        }
        Spacer(modifier = Modifier.width(12.dp))
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
            ) { Text(
                currentSong.value?.titulo ?: "Sin titulo",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Left)}
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ){ Text(
                currentSong.value?.artista ?: "Artista Desconocido",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Left)}
        }
        Spacer(modifier = Modifier.width(12.dp))
        //Controles de Reproduccion
        Column(modifier = Modifier.weight(2f),
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
                        val hasPrevious = controller.hasPreviousMediaItem()
                        if (hasPrevious) {
                            controller.seekToPreviousMediaItem()
                            controller.play()
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
                        if (isPlaying.value) {controller.pause()}
                        else {controller.play()}
                    }) {
                        Icon(imageVector = if (isPlaying.value) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying.value) "Pausar" else "Reproducir")
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
                        val hasNext = controller.hasNextMediaItem()
                        if (hasNext) {
                            controller.seekToNextMediaItem()
                            controller.play()
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
fun MainScreen(controller: MediaController) {
    val context = LocalContext.current
    val mediaController = controller
    val songs = remember { GetSongs(context) }

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
                MediaControlBar(mediaController, songs)
            }
        )
        {
                padding ->
            Column(modifier = Modifier.padding(padding)) {
                SongList(
                    songs = songs,
                    currentSong = songs.find { it.uri == mediaController.currentMediaItem?.localConfiguration?.uri },
                    onSongSelected = { song ->              // recibe el Song seleccionado
                        val mediaItem = song.toMediaItem()  // Lo convierte a MediaItem
                        mediaController.setMediaItem(mediaItem, 0)
                        mediaController.play()
                    }
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
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isPlaying) Color.Black.copy(alpha = 0.2f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column{
            //Imagen de Canción.
            if (song.albumArt != null) {
                Image(
                    modifier = Modifier.size(64.dp),
                    bitmap = song.albumArt.asImageBitmap(),
                    contentDescription = song.titulo
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.Black.copy(alpha = 0.5f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))
        //Nombre de cancion y Arista
        Column {
            Text(
                text = song.titulo,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = song.artista,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        //Botón de Añadir a Playlist
        //Sin implementar
    }
}

