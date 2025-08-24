package com.example.mobileapp2025.ui.elements

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import com.example.mobileapp2025.model.PlaylistViewModel
import com.example.mobileapp2025.model.Song

@Composable
fun MediaControlBar(controller: MediaController, songs:List<Song>) {
    //construcción del PlaylistViewmodel compartido
    val playlistViewModel: PlaylistViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    //variables del Viewmodel
    val isPlaying = playlistViewModel.isPlaying.collectAsState().value

    //Elementos UI de compose
    Column(
        modifier = Modifier.fillMaxWidth().background(Color.DarkGray)
    ) {
        //Slider de progresso
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)
        ) {
            MediaSlider(controller = controller)
        }
        //Barra de controles
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

}