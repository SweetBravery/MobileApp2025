package com.example.mobileapp2025

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.mobileapp2025.model.Song

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var player: Player

    //LifeCycle Methods for the Session/Métodos Lifecycle

    //Player & MediaSession
    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()
    }

    @OptIn(UnstableApi::class)
    override fun onTaskRemoved(rootIntent: Intent?) {
        pauseAllPlayersAndStopSelf()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    fun setPlaylist(songs: List<Song>) {
        val mediaItems = songs.map { it.toMediaItem() }  // Convertir cada Song a MediaItem
        player.setMediaItems(mediaItems)                // Cargar la lista en ExoPlayer
        player.prepare()                                // Preparar reproducción
    }
}