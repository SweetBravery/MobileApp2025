package com.example.mobileapp2025.model

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import com.example.mobileapp2025.model.Song

fun GetSongs(context: Context): List<Song> {
    //lista modificable de canciones
    val songs = mutableListOf<Song>()
    val collection = Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        Media._ID,
        Media.DISPLAY_NAME,
        Media.ARTIST,
        Media.DURATION,
        Media.DATA //abrir archivo para imágen
    )
    //filtro de música del MediaStore
    val selection = null
        //"${MediaStore.Audio.Media.IS_MUSIC} != 0"

    context.contentResolver.query(
        collection,
        projection,
        selection,
        null, null
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val titulo = cursor.getString(nameColumn) ?: "Desconocido"
            val artista = cursor.getString(artistColumn) ?: "Desconocido"
            val duracion = cursor.getLong(durationColumn)
            val dataPath = cursor.getString(dataColumn)

            val uri = ContentUris.withAppendedId(collection, id)

            //Obtiene la portada en el archivo del mp3
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(dataPath)
            val artBytes = retriever.embeddedPicture
            val albumArt: Bitmap? = artBytes?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            }
            retriever.release()

            //crea la instancia del objeto cancion y lo asigna a la lista songs
            songs += Song(id, uri, titulo, artista, duracion, albumArt)
        }
        if (cursor == null) {
            Log.d("PERMISO", "La query devolvió null (sin permisos probablemente)")
        } else if (cursor.count == 0) {
            Log.d("PERMISO", "Cursor sin resultados (0 canciones encontradas)")
        }
    }

    return songs
}