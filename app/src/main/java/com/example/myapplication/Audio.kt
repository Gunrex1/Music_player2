package com.example.myapplication

import android.net.Uri

// Data class representing an audio item
data class Audio(
    val title: String, // The title of the audio track
    val uri: Uri // The URI of the audio file
)
