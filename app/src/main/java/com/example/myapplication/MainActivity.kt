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

class MainActivity : BaseActivity(), MusicAdapter.OnItemClickListener {

    private lateinit var listView: ListView
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var searchView: SearchView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var currentAudioList: List<Audio>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.orange)
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadAudioFiles()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

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

    override fun onItemClick(audio: Audio) {
        val audioIndex = currentAudioList.indexOf(audio)
        playAudio(audio.uri, currentAudioList, audioIndex)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

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
