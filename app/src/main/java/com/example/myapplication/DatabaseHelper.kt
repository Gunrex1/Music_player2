package com.example.myapplication

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "MusicPlayerDB"
        private const val TABLE_FAVORITES = "favorites"
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_URI = "uri"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createFavoritesTable = ("CREATE TABLE $TABLE_FAVORITES($KEY_ID INTEGER PRIMARY KEY,$KEY_TITLE TEXT,$KEY_URI TEXT)")
        db.execSQL(createFavoritesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES")
        onCreate(db)
    }

    fun addFavorite(title: String, uri: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_TITLE, title)
        values.put(KEY_URI, uri)
        db.insert(TABLE_FAVORITES, null, values)
        db.close()
    }

    fun removeFavorite(title: String) {
        val db = this.writableDatabase
        db.delete(TABLE_FAVORITES, "$KEY_TITLE=?", arrayOf(title))
        db.close()
    }

    @SuppressLint("Range")
    fun getAllFavorites(): List<Audio> {
        val favoriteList = mutableListOf<Audio>()
        val selectQuery = "SELECT * FROM $TABLE_FAVORITES"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var title: String
        var uri: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                title = cursor.getString(cursor.getColumnIndex(KEY_TITLE))
                uri = cursor.getString(cursor.getColumnIndex(KEY_URI))
                favoriteList.add(Audio(title, Uri.parse(uri)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return favoriteList
    }
}
