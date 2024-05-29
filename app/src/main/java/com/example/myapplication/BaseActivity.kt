package com.example.myapplication


import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

open class BaseActivity : AppCompatActivity() {

    protected var mediaPlayer: MediaPlayer? = null
    protected lateinit var playPauseButton: Button
    protected lateinit var stopButton: Button
    protected lateinit var seekBar: SeekBar
    var currentIndex: Int = -1
    private lateinit var currentAudioList: List<Audio>
    private var musicAdapter: MusicAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected fun initializeMediaControls(
        playPauseBtn: Button,
        stopBtn: Button,
        seekBarCtrl: SeekBar
    ) {
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

    protected fun playAudio(uri: Uri, audioList: List<Audio>, startIndex: Int) {
        currentAudioList = audioList
        currentIndex = startIndex
        mediaPlayer?.release()
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
}

