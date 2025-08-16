package com.example.mobileapp2025.model

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.Player
import androidx.media3.session.MediaController

/*
class PlaylistViewModel (application: Application) : AndroidViewModel(application){
    private val context = application.applicationContext

    var songs by mutableStateOf<List<Song>>(emptyList())
        private set

    var playlist by mutableStateOf<List<Song>>(emptyList())
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var currentSong by mutableStateOf<Song?>(null)
        private set
    var player: ExoPlayer = ExoPlayer.Builder(context).build()

    init {
        // Carga las canciones con Getsongs
        songs = GetSongs(context)
        //Por defecto la playlist será igual a las canciones obtenidas
        playlist = songs

        //mostrar la primera canción como seleccionada pero sin reproducir
        if (playlist.isNotEmpty()) {
            currentSong = playlist.first()
            val mediaItems = playlist.map {
                MediaItem.fromUri(it.uri)}
                .toMutableList()
            player.setMediaItems(mediaItems, 0, 0L)
            //preparar el player para que funcione la canción por defecto
            player.prepare()
            //para repetir canciones ciclicamente
            player.repeatMode = Player.REPEAT_MODE_ALL
        }
        //listener para actualizar la cancion actual cuando cambie
        player.addListener(object: Player.Listener {
            override fun onIsPlayingChanged(isPlayingNow: Boolean){
                isPlaying = isPlayingNow
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.let { item ->
                    val song = playlist.find {s -> s.uri == item.localConfiguration?.uri}
                    currentSong = song
                }
            }
        })
    }

    //funciones de clase para la playlist.
    fun playNow(song: Song) {
        // si la playlist está bacía usará todas las canciones por defecto.
        /*
        if (playlist.isEmpty()) {
            playlist = songs
        }
         */
        //crea una nueva lista con la canción seleccioanda al inicio, añade las canciones de la
        //playlist escepto la canción seleccionada


        // playlist = listOf(song) + playlist.filter {it != song}
        //convierte un item song de clase Song en un MediaItem que puede ser añadido a exoplayer
        // val mediaItems = playlist.map { MediaItem.fromUri(it.uri)}.toMutableList()
        // player.setMediaItems(mediaItems, 0, 0L)
        // player.prepare()
        // player.play()
        // currentSong = song


        //metodo que hace saltar a la cancion
        val index = playlist.indexOf(song)
        if (index != -1) {
            player.seekTo(index, 0L) //
            player.play()
        }
    }

    fun addToPlaylist(song: Song) {
        if (!playlist.contains(song)) {
            // agrega una canción al final de la lista
            playlist = playlist + song
            player.addMediaItem(MediaItem.fromUri(song.uri))
        }
    }

    fun playNext() {
        player.seekToNext()
    }

    fun playPrevious() {
        player.seekToPrevious()
    }

    fun togglePlayPause() {
        if (player.isPlaying) player.pause() else player.play()
    }

    override fun onCleared() {
        //destruye el viewmodel cuando las activities relacionadas se destruyen
        super.onCleared()
        player.release()
    }
}
*/

class PlaylistViewModel(application: Application): AndroidViewModel(application) {
    val allSongs = mutableStateOf<List<Song>>(emptyList())
    val activePlaylist = mutableStateOf<List<Song>>(emptyList())
    val currentSong = mutableStateOf<Song?>(null)
    // el valor de current song es configurado desde el
    //launched effect de seleccioanar

    @SuppressLint("StaticFieldLeak")
    lateinit var mediaController: MediaController

    fun loadAllSongs(context: Context) {
        allSongs.value = GetSongs(context)
        if (activePlaylist.value.isEmpty()) {
            //asignacion de playlist predeterminada
            activePlaylist.value = allSongs.value
            loadPlaylistIntoController()
        }
    }

    fun loadPlaylistIntoController() {
        mediaController.setMediaItems(activePlaylist.value.map { it.toMediaItem()})
        mediaController.prepare()
        enableRepeatAll()
        //inicialización de currentsong con la primera canción
        if (activePlaylist.value.isNotEmpty()) {
            currentSong.value = activePlaylist.value[0]
        }
    }

    fun selectSong(song: Song) {
        val index = activePlaylist.value.indexOf(song)
        mediaController.seekTo(index, 0L)
        mediaController.play()
    }

    fun enableRepeatAll() {
        mediaController.repeatMode = Player.REPEAT_MODE_ALL
    }

    fun disableRepeatAll() {
        mediaController.repeatMode = Player.REPEAT_MODE_OFF
    }

}
