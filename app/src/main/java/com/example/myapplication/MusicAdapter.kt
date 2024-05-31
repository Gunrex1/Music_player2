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

// Adapter class for managing and displaying the list of audio files
class MusicAdapter(
    private val context: Context, // Context to use for inflating layouts
    private var audioList: List<Audio>, // List of all audio files
    private val listener: OnItemClickListener, // Listener for item click events
    private val databaseHelper: DatabaseHelper // Helper for database operations
) : BaseAdapter() {

    // Interface for item click callbacks
    interface OnItemClickListener {
        fun onItemClick(audio: Audio)
    }

    private var filteredAudioList: List<Audio> = audioList // List of filtered audio files
    private var currentPlayingPosition: Int = -1 // Index of the currently playing audio file

    // Returns the number of items in the filtered list
    override fun getCount(): Int {
        return filteredAudioList.size
    }

    // Returns the audio item at the specified position
    override fun getItem(position: Int): Any {
        return filteredAudioList[position]
    }

    // Returns the ID of the item at the specified position (position as ID)
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Provides a view for an adapter view (ListView) and sets its content
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Inflate the view if it is not recycled
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_music, parent, false)

        // Get references to the TextView and Button in the view
        val musicTitle = view.findViewById<TextView>(R.id.music_title)
        val likeButton = view.findViewById<Button>(R.id.btn_like)
        val audio = filteredAudioList[position] // Get the audio item at the specified position

        // Set the music title
        musicTitle.text = audio.title

        // Update the like button text based on whether the audio is a favorite
        updateLikeButton(likeButton, audio)

        // Highlight the currently playing song
        if (position == currentPlayingPosition) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorHighlight))
        } else {
            view.setBackgroundColor(Color.TRANSPARENT)
        }

        // Set a click listener for the view to handle item clicks
        view.setOnClickListener {
            listener.onItemClick(audio)
        }

        // Set a click listener for the like button to handle like/unlike actions
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

    // Updates the text of the like button based on whether the audio is a favorite
    private fun updateLikeButton(button: Button, audio: Audio) {
        if (databaseHelper.getAllFavorites().contains(audio)) {
            button.text = "Unlike"
        } else {
            button.text = "Like"
        }
    }

    // Filters the audio list based on the provided query and updates the filtered list
    fun filter(query: String) {
        filteredAudioList = if (query.isEmpty()) {
            audioList
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            audioList.filter { it.title.lowercase(Locale.getDefault()).contains(lowerCaseQuery) }
        }
        notifyDataSetChanged() // Notify the adapter to refresh the list view
    }

    // Sets the current playing position and notifies the adapter to refresh the list view
    fun setCurrentPlayingPosition(position: Int) {
        currentPlayingPosition = position
        notifyDataSetChanged() // Notify the adapter to refresh the list view
    }
}
