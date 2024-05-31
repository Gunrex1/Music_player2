package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// MainActivity extends BaseActivity and manages the main screen of the app
class MainActivity : BaseActivity(), MusicAdapter.OnItemClickListener {

    private lateinit var listView: ListView // ListView for displaying audio files
    private lateinit var musicAdapter: MusicAdapter // Adapter for managing audio list
    private lateinit var searchView: SearchView // SearchView for filtering audio list
    private lateinit var databaseHelper: DatabaseHelper // Helper for database operations
    private lateinit var currentAudioList: List<Audio> // List of audio files

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        databaseHelper = DatabaseHelper(this)

        listView = findViewById(R.id.listView)
        searchView = findViewById(R.id.searchView)
        initializeMediaControls(
            findViewById(R.id.btn_play_pause),
            findViewById(R.id.btn_stop),
            findViewById(R.id.seekBar)
        )

        findViewById<Button>(R.id.btn_previous).setOnClickListener {
            playPreviousSong()
        }

        // Check for storage permission and load audio files if granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            loadAudioFiles()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                musicAdapter.filter(newText ?: "")
                return true
            }
        })
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadAudioFiles()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Load audio files from external storage
    private fun loadAudioFiles() {
        val audioList = mutableListOf<Audio>()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(0)
                val name = it.getString(1)
                val contentUri = Uri.withAppendedPath(uri, id.toString())
                audioList.add(Audio(name, contentUri))
            }
        }

        currentAudioList = audioList
        musicAdapter = MusicAdapter(this, audioList, this, databaseHelper)
        listView.adapter = musicAdapter
        setMusicAdapter(musicAdapter) // Set adapter in BaseActivity
    }

    // Handle item click in the list
    override fun onItemClick(audio: Audio) {
        val audioIndex = currentAudioList.indexOf(audio)
        playAudio(audio.uri, currentAudioList, audioIndex)
    }

    // Inflate the options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handle options menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorites -> {
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_close -> {
                finishAffinity() // This closes the app
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Play the previous song in the list
    private fun playPreviousSong() {
        if (currentIndex > 0) {
            val previousIndex = currentIndex - 1
            val previousAudio = currentAudioList[previousIndex]
            playAudio(previousAudio.uri, currentAudioList, previousIndex)
        } else {
            Toast.makeText(this, "No previous song available", Toast.LENGTH_SHORT).show()
        }
    }
}
