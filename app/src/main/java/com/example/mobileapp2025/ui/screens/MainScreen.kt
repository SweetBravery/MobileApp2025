package com.example.mobileapp2025.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import com.example.mobileapp2025.model.PlaylistViewModel
import com.example.mobileapp2025.ui.elements.MediaControlBar
import com.example.mobileapp2025.ui.elements.SongList

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