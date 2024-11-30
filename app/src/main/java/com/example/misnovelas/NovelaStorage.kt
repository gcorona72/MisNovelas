package com.example.misnovelas

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Novela(
    val nombre: String = "",
    val año: Int = 0,
    val descripcion: String = "",
    val valoracion: Double = 0.0,
    val isFavorite: Boolean = false
)

class NovelaStorage(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createNovelaTable = "CREATE TABLE $TABLE_NOVELAS (" +
                "$COLUMN_NOVELA_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NOVELA_NAME TEXT NOT NULL, " +
                "$COLUMN_NOVELA_YEAR INTEGER NOT NULL, " +
                "$COLUMN_NOVELA_DESCRIPTION TEXT NOT NULL, " +
                "$COLUMN_NOVELA_RATING REAL NOT NULL, " +
                "$COLUMN_NOVELA_FAVORITE INTEGER NOT NULL DEFAULT 0)"
        db.execSQL(createNovelaTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOVELAS")
        onCreate(db)
    }

    fun saveNovela(novela: Novela) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOVELA_NAME, novela.nombre)
            put(COLUMN_NOVELA_YEAR, novela.año)
            put(COLUMN_NOVELA_DESCRIPTION, novela.descripcion)
            put(COLUMN_NOVELA_RATING, novela.valoracion)
            put(COLUMN_NOVELA_FAVORITE, if (novela.isFavorite) 1 else 0)
        }
        db.insert(TABLE_NOVELAS, null, values)
    }

    fun getNovelas(): List<Novela> {
        val novelas = mutableListOf<Novela>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_NOVELAS, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
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

    fun deleteNovela(nombre: String): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_NOVELAS, "$COLUMN_NOVELA_NAME = ?", arrayOf(nombre))
        return rowsDeleted > 0
    }

    fun updateFavoriteStatus(novela: Novela): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOVELA_FAVORITE, if (novela.isFavorite) 1 else 0)
        }
        val rowsUpdated = db.update(TABLE_NOVELAS, values, "$COLUMN_NOVELA_NAME = ?", arrayOf(novela.nombre))
        return rowsUpdated > 0
    }

    companion object {
        private const val DATABASE_NAME = "novelas.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NOVELAS = "novelas"
        const val COLUMN_NOVELA_ID = "novela_id"
        const val COLUMN_NOVELA_NAME = "nombre"
        const val COLUMN_NOVELA_YEAR = "año"
        const val COLUMN_NOVELA_DESCRIPTION = "descripcion"
        const val COLUMN_NOVELA_RATING = "valoracion"
        const val COLUMN_NOVELA_FAVORITE = "favorite"
    }
}