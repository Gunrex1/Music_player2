package com.example.myapplication

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

    private fun playPreviousSong() {
        if (currentIndex > 0) {
            val previousIndex = currentIndex - 1
            val previousAudio = favoriteSongs[previousIndex]
            playAudio(previousAudio.uri, favoriteSongs, previousIndex)
            musicAdapter.setCurrentPlayingPosition(previousIndex) // Highlight the previously played song
        } else {

            Toast.makeText(this, "No previous song available", Toast.LENGTH_SHORT).show()
        }
    }
}