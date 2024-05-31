package com.example.myapplication

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

// DatabaseHelper class to manage the SQLite database for storing favorite songs
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1 // Database version
        private const val DATABASE_NAME = "MusicPlayerDB" // Database name
        private const val TABLE_FAVORITES = "favorites" // Table name for favorites
        private const val KEY_ID = "id" // Column name for ID
        private const val KEY_TITLE = "title" // Column name for title
        private const val KEY_URI = "uri" // Column name for URI
    }

    // Called when the database is created for the first time
    override fun onCreate(db: SQLiteDatabase) {
        val createFavoritesTable = ("CREATE TABLE $TABLE_FAVORITES($KEY_ID INTEGER PRIMARY KEY,$KEY_TITLE TEXT,$KEY_URI TEXT)")
        db.execSQL(createFavoritesTable) // Execute the SQL query to create the favorites table
    }

    // Called when the database needs to be upgraded
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES") // Drop the existing favorites table
        onCreate(db) // Recreate the table
    }

    // Adds a song to the favorites table
    fun addFavorite(title: String, uri: String) {
        val db = this.writableDatabase // Get writable database
        val values = ContentValues().apply {
            put(KEY_TITLE, title) // Put the title in ContentValues
            put(KEY_URI, uri) // Put the URI in ContentValues
        }
        db.insert(TABLE_FAVORITES, null, values) // Insert the new row into the favorites table
        db.close() // Close the database connection
    }

    // Removes a song from the favorites table by title
    fun removeFavorite(title: String) {
        val db = this.writableDatabase // Get writable database
        db.delete(TABLE_FAVORITES, "$KEY_TITLE=?", arrayOf(title)) // Delete the row matching the title
        db.close() // Close the database connection
    }

    // Retrieves all favorite songs from the database
    @SuppressLint("Range")
    fun getAllFavorites(): List<Audio> {
        val favoriteList = mutableListOf<Audio>() // List to hold the favorite songs
        val selectQuery = "SELECT * FROM $TABLE_FAVORITES" // SQL query to select all rows from the favorites table
        val db = this.readableDatabase // Get readable database
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null) // Execute the query and get a cursor
        } catch (e: Exception) {
            db.execSQL(selectQuery) // If there's an error, execute the query again
            return ArrayList() // Return an empty list
        }

        // Loop through the cursor and add each row to the favoriteList
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID)) // Get the ID
                val title = cursor.getString(cursor.getColumnIndex(KEY_TITLE)) // Get the title
                val uri = cursor.getString(cursor.getColumnIndex(KEY_URI)) // Get the URI
                favoriteList.add(Audio(title, Uri.parse(uri))) // Add the Audio object to the list
            } while (cursor.moveToNext())
        }
        cursor.close() // Close the cursor
        return favoriteList // Return the list of favorite songs
    }
}
