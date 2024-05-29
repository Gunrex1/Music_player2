package com.example.myapplication

import android.net.Uri


import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class MusicService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val audioUri = intent?.getParcelableExtra<Uri>("audioUri")
        if (audioUri != null) {
            mediaPlayer = MediaPlayer.create(this, audioUri)
            mediaPlayer.start()
        }
        return START_NOT_STICKY
    }



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
