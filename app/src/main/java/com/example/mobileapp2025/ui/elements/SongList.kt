package com.example.mobileapp2025.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mobileapp2025.model.Song

@Composable
fun SongList(songs: List<Song>,
             currentSong: Song?,
             onSongSelected: (Song) -> Unit,
             modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
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