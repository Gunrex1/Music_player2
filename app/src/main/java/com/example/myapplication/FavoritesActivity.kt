package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.ContextCompat

// FavoritesActivity extends BaseActivity and manages the favorites screen of the app
class FavoritesActivity : BaseActivity(), MusicAdapter.OnItemClickListener {

    private lateinit var listViewFavorites: ListView // ListView for displaying favorite songs
    private lateinit var musicAdapter: MusicAdapter // Adapter for managing favorite songs
    private lateinit var favoriteSongs: List<Audio> // List of favorite songs
    private lateinit var databaseHelper: DatabaseHelper // Helper for database operations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.orange)
        }
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

        // Load favorite songs from the database
        favoriteSongs = databaseHelper.getAllFavorites()
        musicAdapter = MusicAdapter(this, favoriteSongs, this, databaseHelper)
        listViewFavorites.adapter = musicAdapter
    }

    // Handle item click in the favorites list
    override fun onItemClick(audio: Audio) {
        val audioIndex = favoriteSongs.indexOf(audio)
        playAudio(audio.uri, favoriteSongs, audioIndex)
        musicAdapter.setCurrentPlayingPosition(audioIndex) // Highlight the currently playing song
    }

    // Play the previous song in the favorites list
    private fun playPreviousSong() {
        if (currentIndex > 0) {
            val previousIndex = currentIndex - 1
            val previousAudio = favoriteSongs[previousIndex]
            playAudio(previousAudio.uri, favoriteSongs, previousIndex)
            musicAdapter.setCurrentPlayingPosition(previousIndex) // Highlight the previously played song
        } else {
            // Display a message indicating that there is no previous song available
            // You can customize this message as per your requirement
            Toast.makeText(this, "No previous song available", Toast.LENGTH_SHORT).show()
        }
    }
}
