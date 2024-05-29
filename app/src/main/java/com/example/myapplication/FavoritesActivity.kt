package com.example.myapplication

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.util.Locale

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast

class FavoritesActivity : BaseActivity(), MusicAdapter.OnItemClickListener {

    private lateinit var listViewFavorites: ListView
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var favoriteSongs: List<Audio>
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        databaseHelper = DatabaseHelper(this)

        listViewFavorites = findViewById(R.id.listViewFavorites)
        initializeMediaControls(
            findViewById(R.id.btn_play_pause),
            findViewById(R.id.btn_stop),
            findViewById(R.id.seekBar)
        )

        findViewById<Button>(R.id.btn_previous).setOnClickListener {
            playPreviousSong()
        }

        favoriteSongs = databaseHelper.getAllFavorites()
        musicAdapter = MusicAdapter(this, favoriteSongs, this, databaseHelper)
        listViewFavorites.adapter = musicAdapter
    }

    override fun onItemClick(audio: Audio) {
        val audioIndex = favoriteSongs.indexOf(audio)
        playAudio(audio.uri, favoriteSongs, audioIndex)
        musicAdapter.setCurrentPlayingPosition(audioIndex) // Highlight the currently playing song
    }