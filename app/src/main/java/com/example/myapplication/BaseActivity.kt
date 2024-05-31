package com.example.myapplication

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

// BaseActivity is the base class for handling media playback functionality
open class BaseActivity : AppCompatActivity() {

    protected var mediaPlayer: MediaPlayer? = null // MediaPlayer instance for audio playback
    protected lateinit var playPauseButton: Button // Button for play/pause control
    protected lateinit var stopButton: Button // Button for stop control
    protected lateinit var seekBar: SeekBar // SeekBar for showing and seeking playback progress
    var currentIndex: Int = -1 // Index of the currently playing audio
    private lateinit var currentAudioList: List<Audio> // List of audio files
    private var musicAdapter: MusicAdapter? = null // Adapter for managing audio list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // Initialize media controls and set their click listeners
    protected fun initializeMediaControls(playPauseBtn: Button, stopBtn: Button, seekBarCtrl: SeekBar) {
        playPauseButton = playPauseBtn
        stopButton = stopBtn
        seekBar = seekBarCtrl

        playPauseButton.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                playPauseButton.text = "Play"
            } else {
                mediaPlayer?.start()
                playPauseButton.text = "Pause"
            }
        }

        stopButton.setOnClickListener {
            mediaPlayer?.stop()
            playPauseButton.text = "Play"
            seekBar.progress = 0
            currentIndex++
            playNextSong()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    // Play an audio file from a given URI and list of audio files starting from a specified index
    protected fun playAudio(uri: Uri, audioList: List<Audio>, startIndex: Int) {
        currentAudioList = audioList
        currentIndex = startIndex
        mediaPlayer?.release() // Release previous MediaPlayer if any
        mediaPlayer = MediaPlayer()

        try {
            mediaPlayer?.setDataSource(this, uri)
            mediaPlayer?.prepare()
            mediaPlayer?.start()

            playPauseButton.text = "Pause"
            seekBar.max = mediaPlayer?.duration ?: 0

            mediaPlayer?.setOnCompletionListener {
                playPauseButton.text = "Play"
                seekBar.progress = 0
                currentIndex++
                playNextSong()
            }

            // Update adapter with current playing position
            musicAdapter?.setCurrentPlayingPosition(currentIndex)

            // Update seekBar in a separate thread
            Thread {
                while (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    runOnUiThread {
                        seekBar.progress = mediaPlayer?.currentPosition ?: 0
                    }
                    Thread.sleep(1000)
                }
            }.start()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Play the next song in the list
    private fun playNextSong() {
        if (currentIndex < currentAudioList.size) {
            val nextAudio = currentAudioList[currentIndex]
            playAudio(nextAudio.uri, currentAudioList, currentIndex)
        } else {
            mediaPlayer?.release()
            mediaPlayer = null
            currentIndex = -1
            musicAdapter?.setCurrentPlayingPosition(currentIndex)
        }
    }

    // Set the adapter for the music list
    protected fun setMusicAdapter(adapter: MusicAdapter) {
        musicAdapter = adapter
    }

    // Release MediaPlayer resources when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
