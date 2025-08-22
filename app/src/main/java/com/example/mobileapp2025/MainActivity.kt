package com.example.mobileapp2025

//import para leer archivo
//import clase propia
// import del viewmodel
import android.Manifest
import android.app.Application
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.mobileapp2025.model.PlaylistViewModel
import com.example.mobileapp2025.model.Song
import com.example.mobileapp2025.ui.theme.MobileApp2025Theme
import com.google.common.util.concurrent.MoreExecutors

//import androidx.compose.runtime.livedata.observeAsState


class MainActivity : ComponentActivity() {
    //var para asignar Mediacontroller
    lateinit var controller: MediaController
    // variable para controlar la carga de controlador
    lateinit var playlistViewModel: PlaylistViewModel
    // es necesario declarar y construir el playlist
    //view model en OnCreate() ya que utilzia el MediaController, asi que
    //se le tiene que asingar el controlaor después
    //de ser creado en OnStart()
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

        //creación del viewmodel
        playlistViewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application))[PlaylistViewModel::class.java]

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
                //asignacion del controlador a ViewModel
                playlistViewModel.attachController(controller)
                playlistViewModel.loadPlaylistIntoController()
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
    //construcción del PlaylistViewmodel compartido
    val playlistViewModel: PlaylistViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val isPlaying = playlistViewModel.isPlaying.collectAsState().value
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
                playlistViewModel.currentSong.value?.titulo ?: "Sin titulo",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Left)}
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ){ Text(
                playlistViewModel.currentSong.value?.artista ?: "Artista Desconocido",
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
                        if (isPlaying) {controller.pause()}
                        else {controller.play()}
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
    //construcción del PlaylistViewmodel compartido
    val playlistViewModel: PlaylistViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    //carga inicial de las canciones
    LaunchedEffect(Unit) {
        playlistViewModel.loadAllSongs(context = context)
    }

    val mediaController = controller
    val allSongs = playlistViewModel.allSongs.value
    val activePlaylist = playlistViewModel.activePlaylist.value


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
                MediaControlBar(mediaController, activePlaylist)
            }
        )
        {
                padding ->
            Column(modifier = Modifier.padding(padding)) {
                SongList(
                    songs = playlistViewModel.activePlaylist.value,
                    currentSong = playlistViewModel.currentSong.value,
                    onSongSelected = { song ->              // recibe el Song seleccionado
                        playlistViewModel.selectSong(song)
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

