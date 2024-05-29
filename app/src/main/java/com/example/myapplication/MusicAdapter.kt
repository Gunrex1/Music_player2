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

class MusicAdapter(
    private val context: Context,
    private var audioList: List<Audio>,
    private val listener: OnItemClickListener,
    private val databaseHelper: DatabaseHelper
) : BaseAdapter() {

    interface OnItemClickListener {
        fun onItemClick(audio: Audio)
    }

    private var filteredAudioList: List<Audio> = audioList
    private var currentPlayingPosition: Int = -1

    override fun getCount(): Int {
        return filteredAudioList.size
    }

    override fun getItem(position: Int): Any {
        return filteredAudioList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_music, parent, false)

        val musicTitle = view.findViewById<TextView>(R.id.music_title)
        val likeButton = view.findViewById<Button>(R.id.btn_like)
        val audio = filteredAudioList[position]
        musicTitle.text = audio.title

        updateLikeButton(likeButton, audio)

        // Highlight the current playing song
        if (position == currentPlayingPosition) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorHighlight))
        } else {
            view.setBackgroundColor(Color.TRANSPARENT)
        }

        view.setOnClickListener {
            listener.onItemClick(audio)
        }

        likeButton.setOnClickListener {
            if (databaseHelper.getAllFavorites().contains(audio)) {
                databaseHelper.removeFavorite(audio.title)
            } else {
                databaseHelper.addFavorite(audio.title, audio.uri.toString())
            }
            updateLikeButton(likeButton, audio)
        }

        return view
    }
    private fun updateLikeButton(button: Button, audio: Audio) {
        if (databaseHelper.getAllFavorites().contains(audio)) {
            button.text = "Unlike"
        } else {
            button.text = "Like"
        }
    }

    fun filter(query: String) {
        filteredAudioList = if (query.isEmpty()) {
            audioList
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            audioList.filter { it.title.lowercase(Locale.getDefault()).contains(lowerCaseQuery) }
        }
        notifyDataSetChanged()
    }

    fun setCurrentPlayingPosition(position: Int) {
        currentPlayingPosition = position
        notifyDataSetChanged()
    }
}