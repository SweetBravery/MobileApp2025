package com.example.mobileapp2025.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import java.io.ByteArrayOutputStream

data class Song(
    val id: Long,
    val uri: Uri,
    val titulo: String,
    val artista: String,
    val duracion: Long,
    val albumArt: Bitmap?
) {
    fun toMediaItem(): MediaItem {
        val metadataBuilder = MediaMetadata.Builder()
            .setTitle(titulo)
            .setArtist(artista)
            .setDurationMs(duracion)

        albumArt?.let { bitmap ->
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val artworkBytes = stream.toByteArray()
            metadataBuilder.setArtworkData(artworkBytes, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
        }

        return MediaItem.Builder()
            .setUri(uri)
            .setMediaMetadata(metadataBuilder.build())
            .build()
    }
}