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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.mobileapp2025.model.PlaylistViewModel
import com.example.mobileapp2025.ui.screens.MainScreen
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
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                SongList(
                    songs = playlistViewModel.activePlaylist.value,
                    currentSong = playlistViewModel.currentSong.value,
                    onSongSelected = { song ->              // recibe el Song seleccionado
                        playlistViewModel.selectSong(song)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
*/