package com.example.misnovelas

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.content.SharedPreferences

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT NOT NULL, " +
                "$COLUMN_PASSWORD TEXT NOT NULL, " +
                "$COLUMN_DARK_MODE INTEGER NOT NULL DEFAULT 0)"
        db.execSQL(createUserTable)

        val createNovelaTable = "CREATE TABLE $TABLE_NOVELAS (" +
                "$COLUMN_NOVELA_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NOVELA_NAME TEXT NOT NULL, " +
                "$COLUMN_NOVELA_YEAR INTEGER NOT NULL, " +
                "$COLUMN_NOVELA_DESCRIPTION TEXT NOT NULL, " +
                "$COLUMN_NOVELA_RATING REAL NOT NULL, " +
                "$COLUMN_NOVELA_FAVORITE INTEGER NOT NULL DEFAULT 0, " +
                "$COLUMN_USER_ID INTEGER NOT NULL, " +
                "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID))"
        db.execSQL(createNovelaTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOVELAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun getNovelasByUser(userId: Int): List<Novela> {
        val novelas = mutableListOf<Novela>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NOVELAS,
            null,
            "$COLUMN_USER_ID = ?",
            arrayOf(userId.toString()),
            null, null, null
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOVELA_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOVELA_NAME))
                val año = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOVELA_YEAR))
                val descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOVELA_DESCRIPTION))
                val valoracion = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_NOVELA_RATING))
                val isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOVELA_FAVORITE)) == 1
                novelas.add(Novela(nombre, año, descripcion, valoracion, isFavorite))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return novelas
    }

    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            null,
            null,
            null, null, null
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
                val darkMode = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DARK_MODE)) == 1
                users.add(User(id, username, password, darkMode))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return users
    }

    fun addNovelaForUser(userId: Int, novela: Novela): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOVELA_NAME, novela.nombre)
            put(COLUMN_NOVELA_YEAR, novela.año)
            put(COLUMN_NOVELA_DESCRIPTION, novela.descripcion)
            put(COLUMN_NOVELA_RATING, novela.valoracion)
            put(COLUMN_NOVELA_FAVORITE, if (novela.isFavorite) 1 else 0)
            put(COLUMN_USER_ID, userId)
        }
        return db.insert(TABLE_NOVELAS, null, values)
    }

    fun deleteUser(username: String, password: String): Boolean {
        val db = writableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            db.delete(TABLE_USERS, "$COLUMN_ID = ?", arrayOf(userId.toString()))
            db.delete(TABLE_NOVELAS, "$COLUMN_USER_ID = ?", arrayOf(userId.toString()))
            cursor.close()
            true
        } else {
            cursor.close()
            false
        }
    }


    fun deleteNovelaForUser(userId: Int, novelaName: String): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete(
            TABLE_NOVELAS,
            "$COLUMN_USER_ID = ? AND $COLUMN_NOVELA_NAME = ?",
            arrayOf(userId.toString(), novelaName)
        )
        return rowsDeleted > 0
    }

    fun getUserIdByUsername(username: String): Int {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            cursor.close()
            userId
        } else {
            cursor.close()
            -1
        }
    }

    fun updateUserDarkMode(username: String, darkMode: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DARK_MODE, if (darkMode) 1 else 0)
        }
        val rowsUpdated = db.update(
            TABLE_USERS,
            values,
            "$COLUMN_USERNAME = ?",
            arrayOf(username)
        )
        return rowsUpdated > 0
    }

    companion object {
        private const val DATABASE_NAME = "users.db"
        private const val DATABASE_VERSION = 3
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_DARK_MODE = "dark_mode"

        const val TABLE_NOVELAS = "novelas"
        const val COLUMN_NOVELA_ID = "novela_id"
        const val COLUMN_NOVELA_NAME = "nombre"
        const val COLUMN_NOVELA_YEAR = "año"
        const val COLUMN_NOVELA_DESCRIPTION = "descripcion"
        const val COLUMN_NOVELA_RATING = "valoracion"
        const val COLUMN_NOVELA_FAVORITE = "favorite"
        const val COLUMN_USER_ID = "user_id"
    }
    fun getCurrentUserId(sharedPreferences: SharedPreferences): Int {
        val username = sharedPreferences.getString("current_user", "") ?: ""
        return getUserIdByUsername(username)
    }
}