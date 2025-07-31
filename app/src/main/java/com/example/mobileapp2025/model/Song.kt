package com.example.mobileapp2025.model

import android.net.Uri

data class Song(
    val id: Long,
    val uri: Uri,
    val titulo: String,
    val artista: String,
    val duracion: Long
)