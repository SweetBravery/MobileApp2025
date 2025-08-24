package com.example.mobileapp2025.model

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import kotlinx.coroutines.flow.MutableStateFlow

class PlaylistViewModel(application: Application): AndroidViewModel(application) {
    val isPlaying = MutableStateFlow(false)
    val allSongs = mutableStateOf<List<Song>>(emptyList())
    val activePlaylist = mutableStateOf<List<Song>>(emptyList())
    val currentSong = mutableStateOf<Song?>(null)
    // el valor de current song es configurado desde el
    //launched effect de seleccioanar

    @SuppressLint("StaticFieldLeak")
    lateinit var mediaController: MediaController

    fun attachController(controller: MediaController) {
        mediaController = controller
        mediaController.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                isPlaying.value = isPlayingValue
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentSong.value = mediaItem?.let { item ->
                    activePlaylist.value.find { song ->
                        song.uri == item.localConfiguration?.uri
                    }
                }
            }
        })
        syncFromController()
    }

    fun syncFromController() {
        val mediaItem = mediaController.currentMediaItem
        if (mediaItem != null) {
            currentSong.value = mediaController.currentMediaItem?.let { item ->
                activePlaylist.value.find { it.uri == item.localConfiguration?.uri }
            }
        }
    }

    fun loadAllSongs(context: Context) {
        allSongs.value = GetSongs(context)
        if (activePlaylist.value.isEmpty()) {
            //asignacion de playlist predeterminada
            activePlaylist.value = allSongs.value
            loadPlaylistIntoController()
        }
    }

    fun loadPlaylistIntoController() {
        if (mediaController.mediaItemCount == 0) {
            mediaController.setMediaItems(activePlaylist.value.map { it.toMediaItem()})
            mediaController.prepare()
            enableRepeatAll()
        }

        syncFromController()
    }

    // sincronización del currentsong con el MediaController

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
